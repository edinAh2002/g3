package com.example.frontpage.mood

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.mood.data.MoodRepository
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodLogView
import com.example.frontpage.mood.model.MoodTrackingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository

    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()

    private val _latestMood = MutableStateFlow<MoodEntry?>(null)
    val latestMood: StateFlow<MoodEntry?> = _latestMood.asStateFlow()

    private val _averageMood = MutableStateFlow<Double?>(null)
    val averageMood: StateFlow<Double?> = _averageMood.asStateFlow()

    private val _activeLogView = MutableStateFlow(MoodLogView.List)
    val activeLogView: StateFlow<MoodLogView> = _activeLogView.asStateFlow()

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

    fun showListView() {
        _activeLogView.value = MoodLogView.List
        loadMoods()
    }

    fun showWeekView() {
        _activeLogView.value = MoodLogView.Week
        loadMoods()
    }

    fun showSummaryView() {
        _activeLogView.value = MoodLogView.Summary
        loadMoods()
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
        val currentView = _activeLogView.value

        val entries = when (currentView) {
            MoodLogView.List -> {
                repository.getAllMoods()
            }

            MoodLogView.Week -> {
                val weekRange = MoodDateUtils.getCurrentWeekDateRange()
                repository.getMoodsBetweenDates(
                    startDate = weekRange.first,
                    endDate = weekRange.second
                )
            }

            MoodLogView.Summary -> {
                repository.getAllMoods()
            }
        }

        val average = when (currentView) {
            MoodLogView.List -> {
                repository.getAverageMood()
            }

            MoodLogView.Week -> {
                val weekRange = MoodDateUtils.getCurrentWeekDateRange()
                repository.getAverageMoodBetweenDates(
                    startDate = weekRange.first,
                    endDate = weekRange.second
                )
            }

            MoodLogView.Summary -> {
                repository.getAverageMood()
            }
        }

        _moodEntries.value = entries
        _averageMood.value = average
        _latestMood.value = repository.getLatestMood()
    }
}