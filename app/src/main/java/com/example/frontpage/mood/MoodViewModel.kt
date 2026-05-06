package com.example.frontpage.mood

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.mood.data.MoodRepository
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodDateFilter
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState
import com.example.frontpage.mood.model.MoodSection
import com.example.frontpage.mood.model.MoodTrackingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository
    private val authRepository: AuthRepository

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

    private val _activeSection = MutableStateFlow(MoodSection.Overview)
    val activeSection: StateFlow<MoodSection> = _activeSection.asStateFlow()

    private val _filterState = MutableStateFlow(MoodLogFilterState())
    val filterState: StateFlow<MoodLogFilterState> = _filterState.asStateFlow()

    private val _trackingState = MutableStateFlow(MoodTrackingUiState())
    val trackingState: StateFlow<MoodTrackingUiState> = _trackingState.asStateFlow()

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

    fun showSection(section: MoodSection) {
        _activeSection.value = section
    }

    fun setFeelingFilter(filter: MoodFeelingFilter) {
        _filterState.update { current ->
            current.copy(feelingFilter = filter)
        }
        applyFilters()
    }

    fun setDateFilter(filter: MoodDateFilter) {
        _filterState.update { current ->
            current.copy(dateFilter = filter)
        }
        applyFilters()
    }

    fun clearFilters() {
        _filterState.value = MoodLogFilterState()
        applyFilters()
    }

    fun selectMood(moodValue: Int) {
        _trackingState.update {
            it.copy(
                selectedMood = moodValue,
                errorMessage = null
            )
        }
    }

    fun updateNote(note: String) {
        _trackingState.update {
            it.copy(note = note)
        }
    }

    fun saveMood(onSuccess: () -> Unit) {
        val currentState = _trackingState.value

        if (currentState.selectedMood == 0) {
            _trackingState.update {
                it.copy(errorMessage = "Please select a mood before saving.")
            }
            return
        }

        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh()

            if (userId == null) {
                _trackingState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Please log in before saving your mood."
                    )
                }
                return@launch
            }

            _trackingState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            val moodEntry = MoodEntry(
                userId = userId,
                date = MoodDateUtils.getTodayDate(),
                time = MoodDateUtils.getCurrentTime(),
                moodValue = currentState.selectedMood,
                note = currentState.note.trim()
            )

            repository.addMood(
                userId = userId,
                moodEntry = moodEntry
            )

            refreshMoodData()

            _trackingState.value = MoodTrackingUiState()

            onSuccess()
        }
    }

    fun resetTrackingForm() {
        _trackingState.value = MoodTrackingUiState()
    }

    fun updateExistingMood(
        moodEntry: MoodEntry,
        newMoodValue: Int,
        newNote: String
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            val updatedMood = moodEntry.copy(
                userId = userId,
                moodValue = newMoodValue,
                note = newNote.trim()
            )

            repository.updateMood(
                userId = userId,
                moodEntry = updatedMood
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
            matchesFeelingFilter(moodEntry, currentFilters.feelingFilter) &&
                    matchesDateFilter(moodEntry, currentFilters.dateFilter)
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

    private fun matchesDateFilter(
        moodEntry: MoodEntry,
        filter: MoodDateFilter
    ): Boolean {
        return when (filter) {
            MoodDateFilter.All -> true

            MoodDateFilter.Today -> {
                moodEntry.date == MoodDateUtils.getTodayDate()
            }

            MoodDateFilter.ThisWeek -> {
                val weekRange = MoodDateUtils.getCurrentWeekDateRange()
                moodEntry.date >= weekRange.first && moodEntry.date <= weekRange.second
            }
        }
    }
}