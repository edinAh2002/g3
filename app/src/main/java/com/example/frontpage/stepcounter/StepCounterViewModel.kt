package com.example.frontpage.stepcounter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StepCounterUiState(
    val steps: Int = 0,
    val goal: Int = 10000,
    val isSensorAvailable: Boolean = true
) {
    val progress: Float
        get() = (steps / goal.toFloat()).coerceIn(0f, 1f)
}

class StepCounterViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StepCounterUiState())
    val uiState: StateFlow<StepCounterUiState> = _uiState.asStateFlow()

    private val stepCounterManager = StepCounterManager(
        context = application.applicationContext,
        onStepsChanged = { newSteps ->
            _uiState.value = _uiState.value.copy(
                steps = newSteps
            )
        },
        onSensorAvailableChanged = { available ->
            _uiState.value = _uiState.value.copy(
                isSensorAvailable = available
            )
        }
    )

    fun startStepCounter() {
        stepCounterManager.startListening()
    }

    fun stopStepCounter() {
        stepCounterManager.stopListening()
    }

    fun resetSteps() {
        stepCounterManager.resetSteps()
        _uiState.value = _uiState.value.copy(
            steps = 0
        )
    }

    fun changeGoal(newgoal: Int) {
        _uiState.value = _uiState.value.copy(
            goal = newgoal
        )
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopListening()
    }
}