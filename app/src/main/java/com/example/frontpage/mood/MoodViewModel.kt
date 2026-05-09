package com.example.frontpage.mood

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.mood.data.MoodRepository
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState
import com.example.frontpage.mood.model.MoodScalePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository
    private val authRepository: AuthRepository
    private val settingsPreferences = application.getSharedPreferences(
        MOOD_SETTINGS_PREFERENCES,
        Context.MODE_PRIVATE
    )

    private val currentUserId = MutableStateFlow<Long?>(null)

    private val _allMoodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val allMoodEntries: StateFlow<List<MoodEntry>> = _allMoodEntries.asStateFlow()

    private val _filteredMoodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val filteredMoodEntries: StateFlow<List<MoodEntry>> = _filteredMoodEntries.asStateFlow()

    private val _latestMood = MutableStateFlow<MoodEntry?>(null)
    val latestMood: StateFlow<MoodEntry?> = _latestMood.asStateFlow()

    private val _averageMood = MutableStateFlow<Double?>(null)
    val averageMood: StateFlow<Double?> = _averageMood.asStateFlow()

    private val _filteredAverageMood = MutableStateFlow<Double?>(null)
    val filteredAverageMood: StateFlow<Double?> = _filteredAverageMood.asStateFlow()

    private val _filterState = MutableStateFlow(MoodLogFilterState())
    val filterState: StateFlow<MoodLogFilterState> = _filterState.asStateFlow()

    private val _defaultScalePreset = MutableStateFlow(
        MoodScalePreset.fromStorageKey(
            settingsPreferences.getString(KEY_DEFAULT_SCALE_PRESET, null)
        )
    )
    val defaultScalePreset: StateFlow<MoodScalePreset> = _defaultScalePreset.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)

        repository = MoodRepository(database.moodDao())

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        currentUserId.value = authRepository.getCurrentUserId()

        loadMoods()
    }

    fun refreshCurrentUser() {
        currentUserId.value = authRepository.getCurrentUserId()
        loadMoods()
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        return userId
    }

    fun loadMoods() {
        viewModelScope.launch {
            refreshMoodData()
        }
    }

    fun setFeelingFilter(filter: MoodFeelingFilter) {
        _filterState.update { current ->
            current.copy(feelingFilter = filter)
        }
        applyFilters()
    }

    fun clearFilters() {
        _filterState.value = MoodLogFilterState()
        applyFilters()
    }

    fun updateDefaultScalePreset(preset: MoodScalePreset) {
        settingsPreferences.edit()
            .putString(KEY_DEFAULT_SCALE_PRESET, preset.storageKey)
            .apply()

        _defaultScalePreset.value = preset
    }

    fun addMood(moodEntry: MoodEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.addMood(
                userId = userId,
                moodEntry = moodEntry.copy(note = moodEntry.note.trim())
            )

            refreshMoodData()
        }
    }

    fun updateMood(moodEntry: MoodEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.updateMood(
                userId = userId,
                moodEntry = moodEntry.copy(
                    userId = userId,
                    note = moodEntry.note.trim()
                )
            )

            refreshMoodData()
        }
    }

    fun deleteMood(moodEntry: MoodEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.deleteMood(
                userId = userId,
                moodEntry = moodEntry
            )

            refreshMoodData()
        }
    }

    fun deleteMoods(moodEntries: List<MoodEntry>) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch
            val moodIds = moodEntries.map { moodEntry -> moodEntry.id }

            repository.deleteMoods(
                userId = userId,
                moodIds = moodIds
            )

            refreshMoodData()
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.clearAllMoods(userId)
            refreshMoodData()
        }
    }

    private suspend fun refreshMoodData() {
        val userId = getCurrentUserIdOrRefresh()

        if (userId == null) {
            _allMoodEntries.value = emptyList()
            _filteredMoodEntries.value = emptyList()
            _latestMood.value = null
            _averageMood.value = null
            _filteredAverageMood.value = null
            return
        }

        val entries = repository.getAllMoods(userId)

        _allMoodEntries.value = entries
        _latestMood.value = repository.getLatestMood(userId)
        _averageMood.value = MoodStatsCalculator.getAverageMood(entries)

        applyFilters()
    }

    private fun applyFilters() {
        val currentEntries = _allMoodEntries.value
        val currentFilters = _filterState.value

        val filteredEntries = currentEntries.filter { moodEntry ->
            matchesFeelingFilter(moodEntry, currentFilters.feelingFilter)
        }

        _filteredMoodEntries.value = filteredEntries
        _filteredAverageMood.value = MoodStatsCalculator.getAverageMood(filteredEntries)
    }

    private fun matchesFeelingFilter(
        moodEntry: MoodEntry,
        filter: MoodFeelingFilter
    ): Boolean {
        return filter.moodValue == null || moodEntry.moodValue == filter.moodValue
    }

    private companion object {
        const val MOOD_SETTINGS_PREFERENCES = "mood_settings"
        const val KEY_DEFAULT_SCALE_PRESET = "default_scale_preset"
    }
}
