package com.example.frontpage.sleep

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.sleep.data.SleepHealthConnectManager
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepEntry
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

    private val repository: SleepRepository
    private val authRepository: AuthRepository
    private val sleepHealthConnectManager: SleepHealthConnectManager

    private val authPreferences: SharedPreferences
    private val appContext = application.applicationContext

    private val currentUserId = MutableStateFlow<Long?>(null)

    val sleepLogs: StateFlow<List<SleepEntry>>

    private val _goalMinutes = MutableStateFlow(SleepSettingsRepository.DEFAULT_SLEEP_GOAL_MINUTES)
    val goalMinutes: StateFlow<Int> = _goalMinutes

    private val _healthConnectState = MutableStateFlow(SleepHealthConnectState())
    val healthConnectState: StateFlow<SleepHealthConnectState> = _healthConnectState

    private val authPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            refreshCurrentUser()
        }

    init {
        val database = AppDatabase.getDatabase(application)

        repository = SleepRepository(database.sleepDao())
        sleepHealthConnectManager = SleepHealthConnectManager(appContext)

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
        refreshSleepGoal()
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
        refreshSleepGoal()
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        refreshSleepGoal()
        return userId
    }

    fun updateSleepGoalMinutes(newGoalMinutes: Int) {
        val updatedGoalMinutes = SleepSettingsRepository.updateSleepGoalMinutes(
            context = appContext,
            userId = getCurrentUserIdOrRefresh(),
            newGoalMinutes = newGoalMinutes
        )

        _goalMinutes.value = updatedGoalMinutes
    }

    fun refreshHealthConnectState() {
        viewModelScope.launch {
            _healthConnectState.value = sleepHealthConnectManager.getState().copy(
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
        val hasSleepPermission = grantedPermissions.containsAll(SleepHealthConnectManager.PERMISSIONS)

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

            val importedEntries = sleepHealthConnectManager.readSleepSessionsFromLast30Days()

            importedEntries.forEach { entry ->
                repository.addSleep(
                    userId = userId,
                    entry = entry
                )
            }

            _healthConnectState.value = sleepHealthConnectManager.getState().copy(
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
        _goalMinutes.value = SleepSettingsRepository.getSleepGoalMinutes(
            context = appContext,
            userId = currentUserId.value
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
