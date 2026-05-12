package com.example.frontpage.sleep

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.mood.data.MoodRepository
import com.example.frontpage.sleep.data.SharedPreferencesSleepSettingsDataSource
import com.example.frontpage.sleep.data.SleepHealthDataSource
import com.example.frontpage.sleep.data.SleepHealthConnectManager
import com.example.frontpage.sleep.data.SleepLogDataSource
import com.example.frontpage.sleep.data.SleepPreviewSeedCleaner
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.data.SleepSettingsDataSource
import com.example.frontpage.sleep.domain.SleepGoalHistoryProtector
import com.example.frontpage.sleep.domain.SleepPageLayoutManager
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepDefaults
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageLayoutDefaults
import com.example.frontpage.sleep.model.SleepPageSectionId
import com.example.frontpage.sleep.model.SleepThemePresetId
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SleepViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: SleepLogDataSource
    private val authRepository: AuthRepository
    private val sleepHealthDataSource: SleepHealthDataSource
    private val settingsDataSource: SleepSettingsDataSource
    private val goalHistoryProtector: SleepGoalHistoryProtector
    private val pageLayoutManager: SleepPageLayoutManager
    private val previewSeedCleaner: SleepPreviewSeedCleaner

    private val authPreferences: SharedPreferences
    private val appContext = application.applicationContext

    private val currentUserId = MutableStateFlow<Long?>(null)

    val sleepLogs: StateFlow<List<SleepEntry>>

    private val _goalMinutes = MutableStateFlow(SleepDefaults.SLEEP_GOAL_MINUTES)
    val goalMinutes: StateFlow<Int> = _goalMinutes

    private val _weekdaySettings = MutableStateFlow<List<WeekdaySleepSettings>>(emptyList())
    val weekdaySettings: StateFlow<List<WeekdaySleepSettings>> = _weekdaySettings

    private val _customTags = MutableStateFlow<List<SleepCustomTag>>(emptyList())
    val customTags: StateFlow<List<SleepCustomTag>> = _customTags

    private val _pageLayouts = MutableStateFlow(SleepPageLayoutDefaults.defaultLayouts())
    val pageLayouts: StateFlow<Map<SleepPageKey, SleepPageLayout>> = _pageLayouts

    private val _themePresetId = MutableStateFlow(SleepThemePresetId.Default)
    val themePresetId: StateFlow<SleepThemePresetId> = _themePresetId

    private val _healthConnectState = MutableStateFlow(SleepHealthConnectState())
    val healthConnectState: StateFlow<SleepHealthConnectState> = _healthConnectState
    val sleepHealthPermissions: Set<String>
        get() = sleepHealthDataSource.requiredPermissions

    private val authPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            refreshCurrentUser()
        }

    init {
        val database = AppDatabase.getDatabase(application)

        repository = SleepRepository(database.sleepDao())
        val previewMoodRepository = MoodRepository(database.moodDao())
        settingsDataSource = SharedPreferencesSleepSettingsDataSource(appContext)
        sleepHealthDataSource = SleepHealthConnectManager(appContext)
        goalHistoryProtector = SleepGoalHistoryProtector(settingsDataSource)
        pageLayoutManager = SleepPageLayoutManager(settingsDataSource)
        previewSeedCleaner = SleepPreviewSeedCleaner(
            context = appContext,
            sleepLogDataSource = repository,
            moodRepository = previewMoodRepository
        )

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        authPreferences = application.applicationContext.getSharedPreferences(
            AUTH_PREFS_NAME,
            Context.MODE_PRIVATE
        )

        authPreferences.registerOnSharedPreferenceChangeListener(authPreferenceListener)

        currentUserId.value = authRepository.getCurrentUserId()
        refreshSleepSettings()
        refreshHealthConnectState()

        sleepLogs = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getSleepLogsForUser(userId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun refreshCurrentUser() {
        currentUserId.value = authRepository.getCurrentUserId()
        refreshSleepSettings()
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        refreshSleepSettings()
        return userId
    }

    fun updateSleepGoalMinutes(newGoalMinutes: Int) {
        val userId = getCurrentUserIdOrRefresh()

        snapshotPastSleepGoalDates(
            userId = userId,
            shouldSnapshot = { true }
        )

        val updatedGoalMinutes = settingsDataSource.updateSleepGoalMinutes(
            userId = userId,
            newGoalMinutes = newGoalMinutes
        )

        _goalMinutes.value = updatedGoalMinutes
        refreshSleepSettings()
    }

    fun updateTodaySleepGoalMinutes(newGoalMinutes: Int) {
        val todayMillis = System.currentTimeMillis()
        val todayWeekday = SleepWeekday.fromDateMillis(todayMillis)
        val userId = getCurrentUserIdOrRefresh()

        snapshotPastSleepGoalDates(
            userId = userId,
            shouldSnapshot = { dateMillis ->
                SleepWeekday.fromDateMillis(dateMillis) == todayWeekday
            }
        )

        _weekdaySettings.value = settingsDataSource.updateWeekdayGoalMinutes(
            userId = userId,
            weekday = todayWeekday,
            newGoalMinutes = newGoalMinutes
        )

        val updatedGoalMinutes = settingsDataSource.updateSleepGoalMinutesForDate(
            userId = userId,
            dateMillis = todayMillis,
            newGoalMinutes = newGoalMinutes
        )

        _goalMinutes.value = updatedGoalMinutes
    }

    fun updateWeekdayGoalMinutes(
        weekday: SleepWeekday,
        newGoalMinutes: Int
    ) {
        val userId = getCurrentUserIdOrRefresh()

        snapshotPastSleepGoalDates(
            userId = userId,
            shouldSnapshot = { dateMillis ->
                SleepWeekday.fromDateMillis(dateMillis) == weekday
            }
        )

        _weekdaySettings.value = settingsDataSource.updateWeekdayGoalMinutes(
            userId = userId,
            weekday = weekday,
            newGoalMinutes = newGoalMinutes
        )

        refreshSleepGoal()
    }

    fun updateWeekdayScheduleTargets(
        weekday: SleepWeekday,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ) {
        _weekdaySettings.value = settingsDataSource.updateWeekdayScheduleTargets(
            userId = getCurrentUserIdOrRefresh(),
            weekday = weekday,
            bedtimeMinutes = bedtimeMinutes,
            wakeMinutes = wakeMinutes
        )
    }

    fun updateAllWeekdayScheduleTargets(
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ) {
        _weekdaySettings.value = settingsDataSource.updateAllWeekdayScheduleTargets(
            userId = getCurrentUserIdOrRefresh(),
            bedtimeMinutes = bedtimeMinutes,
            wakeMinutes = wakeMinutes
        )
    }

    fun addCustomTag(label: String) {
        _customTags.value = settingsDataSource.addCustomTag(
            userId = getCurrentUserIdOrRefresh(),
            label = label
        )
    }

    fun deleteCustomTag(tagId: String) {
        _customTags.value = settingsDataSource.deleteCustomTag(
            userId = getCurrentUserIdOrRefresh(),
            tagId = tagId
        )
    }

    fun addSleepPageSection(
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ) {
        val layout = pageLayoutManager.addSection(
            userId = getCurrentUserIdOrRefresh(),
            currentLayouts = _pageLayouts.value,
            pageKey = pageKey,
            sectionId = sectionId
        )

        _pageLayouts.value = _pageLayouts.value + (pageKey to layout)
    }

    fun removeSleepPageSection(
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ) {
        val layout = pageLayoutManager.removeSection(
            userId = getCurrentUserIdOrRefresh(),
            currentLayouts = _pageLayouts.value,
            pageKey = pageKey,
            sectionId = sectionId
        )

        _pageLayouts.value = _pageLayouts.value + (pageKey to layout)
    }

    fun moveSleepPageSectionUp(
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ) {
        moveSleepPageSection(
            pageKey = pageKey,
            sectionId = sectionId,
            offset = -1
        )
    }

    fun moveSleepPageSectionDown(
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ) {
        moveSleepPageSection(
            pageKey = pageKey,
            sectionId = sectionId,
            offset = 1
        )
    }

    fun resetSleepPageLayout(pageKey: SleepPageKey) {
        val layout = pageLayoutManager.resetLayout(
            userId = getCurrentUserIdOrRefresh(),
            pageKey = pageKey
        )

        _pageLayouts.value = _pageLayouts.value + (pageKey to layout)
    }

    fun updateSleepThemePreset(presetId: SleepThemePresetId) {
        _themePresetId.value = settingsDataSource.updateSleepThemePresetId(
            userId = getCurrentUserIdOrRefresh(),
            presetId = presetId
        )
    }

    fun removeFakeMoodSleepContextLinks(onRemoved: () -> Unit = {}) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            previewSeedCleaner.removeFakeMoodSleepContextLinks(userId)
            onRemoved()
        }
    }

    fun getGoalMinutesForDate(dateMillis: Long): Int {
        return settingsDataSource.getSleepGoalMinutesForDate(
            userId = currentUserId.value,
            dateMillis = dateMillis
        )
    }

    fun refreshHealthConnectState() {
        viewModelScope.launch {
            _healthConnectState.value = sleepHealthDataSource.getState().copy(
                lastImportMessage = _healthConnectState.value.lastImportMessage
            )
        }
    }

    fun onHealthConnectPermissionRequestStarted() {
        _healthConnectState.value = _healthConnectState.value.copy(
            lastImportMessage = "If the Health Connect screen opens, allow Sleep access."
        )
    }

    fun onHealthConnectPermissionRequestFailed() {
        _healthConnectState.value = _healthConnectState.value.copy(
            lastImportMessage = "Could not open Health Connect access. Open Android Settings > Health Connect > App permissions and allow Sleep."
        )
    }

    fun onHealthConnectPermissionsChanged(grantedPermissions: Set<String>) {
        val hasSleepPermission = grantedPermissions.containsAll(sleepHealthDataSource.requiredPermissions)

        _healthConnectState.value = _healthConnectState.value.copy(
            hasSleepPermission = hasSleepPermission,
            lastImportMessage = if (hasSleepPermission) {
                "Sleep access granted. You can import Health Connect sleep now."
            } else {
                "Sleep access was not granted. If no prompt opened, use Android Settings > Health Connect > App permissions."
            }
        )

        refreshHealthConnectState()
    }

    fun importHealthConnectSleep() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh()

            if (userId == null) {
                _healthConnectState.value = _healthConnectState.value.copy(
                    lastImportMessage = "Log in before importing sleep."
                )
                return@launch
            }

            _healthConnectState.value = _healthConnectState.value.copy(
                isImporting = true,
                lastImportMessage = null
            )

            val importedEntries = sleepHealthDataSource.readSleepSessionsFromLast30Days()

            importedEntries.forEach { entry ->
                repository.addSleep(
                    userId = userId,
                    entry = entry
                )
            }

            _healthConnectState.value = sleepHealthDataSource.getState().copy(
                isImporting = false,
                lastImportMessage = if (importedEntries.isEmpty()) {
                    "No Health Connect sleep sessions found from the last 30 days."
                } else {
                    "Imported ${importedEntries.size} sleep sessions from Health Connect."
                }
            )
        }
    }

    fun addSleep(entry: SleepEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.addSleep(
                userId = userId,
                entry = entry
            )
        }
    }

    fun updateSleep(entry: SleepEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.updateSleep(
                userId = userId,
                entry = entry
            )
        }
    }

    fun deleteSleep(id: Long) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.deleteSleep(
                userId = userId,
                id = id
            )
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.clearAllLogs(userId)
        }
    }

    private fun refreshSleepGoal() {
        _goalMinutes.value = settingsDataSource.getSleepGoalMinutes(
            userId = currentUserId.value
        )
    }

    private fun refreshSleepSettings() {
        refreshSleepGoal()

        _weekdaySettings.value = settingsDataSource.getWeekdaySleepSettings(
            userId = currentUserId.value
        )

        _customTags.value = settingsDataSource.getCustomTags(
            userId = currentUserId.value
        )

        _pageLayouts.value = pageLayoutManager.loadLayouts(
            userId = currentUserId.value
        )

        _themePresetId.value = settingsDataSource.getSleepThemePresetId(
            userId = currentUserId.value
        )
    }

    private fun moveSleepPageSection(
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId,
        offset: Int
    ) {
        val layout = pageLayoutManager.moveSection(
            userId = getCurrentUserIdOrRefresh(),
            currentLayouts = _pageLayouts.value,
            pageKey = pageKey,
            sectionId = sectionId,
            offset = offset
        )

        _pageLayouts.value = _pageLayouts.value + (pageKey to layout)
    }

    private fun snapshotPastSleepGoalDates(
        userId: Long?,
        shouldSnapshot: (Long) -> Boolean
    ) {
        goalHistoryProtector.snapshotPastGoalDates(
            userId = userId,
            sleepLogs = sleepLogs.value,
            shouldSnapshot = shouldSnapshot
        )
    }

    override fun onCleared() {
        authPreferences.unregisterOnSharedPreferenceChangeListener(authPreferenceListener)
        super.onCleared()
    }

    companion object {
        private const val AUTH_PREFS_NAME = "secure_auth_preferences"
    }
}
