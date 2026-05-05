package com.example.frontpage.mood

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

        loadMoods()
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
            _trackingState.update {
                it.copy(isSaving = true, errorMessage = null)
            }

            val moodEntry = MoodEntry(
                date = MoodDateUtils.getTodayDate(),
                time = MoodDateUtils.getCurrentTime(),
                moodValue = currentState.selectedMood,
                note = currentState.note.trim()
            )

            repository.addMood(moodEntry)

            refreshMoodData()

            _trackingState.value = MoodTrackingUiState()

            onSuccess()
        }
    }

    fun updateExistingMood(
        moodEntry: MoodEntry,
        newMoodValue: Int,
        newNote: String
    ) {
        viewModelScope.launch {
            val updatedMood = moodEntry.copy(
                moodValue = newMoodValue,
                note = newNote.trim()
            )

            repository.updateMood(updatedMood)
            refreshMoodData()
        }
    }

    fun deleteMood(moodEntry: MoodEntry) {
        viewModelScope.launch {
            repository.deleteMood(moodEntry)
            refreshMoodData()
        }
    }

    private suspend fun refreshMoodData() {
        val entries = repository.getAllMoods()

        _allMoodEntries.value = entries
        _latestMood.value = repository.getLatestMood()
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