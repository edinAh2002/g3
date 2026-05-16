package com.example.frontpage.stepcounter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.stepcounter.data.StepsHealthConnectManager
import com.example.frontpage.stepcounter.data.StepsRepository
import com.example.frontpage.stepcounter.model.StepsEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StepCounterUiState(
    val steps: Int = 0,
    val goal: Int = 10000,
    val isHealthConnectAvailable: Boolean = true,
    val hasHealthConnectPermission: Boolean = false
) {
    val progress: Float
        get() = if (goal > 0) {
            (steps / goal.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}
@OptIn(ExperimentalCoroutinesApi::class)
class StepCounterViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: StepsRepository
    private val authRepository: AuthRepository

    private val currentUserId = MutableStateFlow<Long?>(null)

    val entries: StateFlow<List<StepsEntry>>
    val latestEntry: StateFlow<StepsEntry?>

    private val _uiState = MutableStateFlow(StepCounterUiState())
    val uiState: StateFlow<StepCounterUiState> = _uiState.asStateFlow()

    private val healthConnectManager = StepsHealthConnectManager(
        context = application.applicationContext
    )


    private var lastSavedSteps = 0

    init {
        val database = AppDatabase.getDatabase(application)

        repository = StepsRepository(
            stepsDao = database.stepsDao()
        )

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        currentUserId.value = authRepository.getCurrentUserId()

        entries = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getEntriesForUser(userId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        latestEntry = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(null)
                } else {
                    repository.getLatestEntryForUser(userId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

        viewModelScope.launch {
            latestEntry.collect { entry ->
                if (entry != null) {
                    _uiState.value = _uiState.value.copy(
                        steps = entry.steps.toInt(),
                        goal = entry.goal.toInt()
                    )
                }
            }
        }
    }

    fun refreshCurrentUser() {
        currentUserId.value = authRepository.getCurrentUserId()
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        return userId
    }

    private fun saveTodayStepsToDatabase(
        steps: Int = _uiState.value.steps,
        goal: Int = _uiState.value.goal
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch
            val todayStartMillis = getStartOfTodayMillis()

            val existingEntry = repository.getEntryForDay(
                userId = userId,
                dayStartMillis = todayStartMillis
            )

            if (existingEntry == null) {
                repository.addEntry(
                    userId = userId,
                    entry = StepsEntry(
                        userId = userId,
                        dayStartMillis = todayStartMillis,
                        steps = steps.toLong(),
                        goal = goal.toLong()
                    )
                )
            } else {
                repository.updateEntry(
                    userId = userId,
                    entry = existingEntry.copy(
                        steps = steps.toLong(),
                        goal = goal.toLong()
                    )
                )
            }
        }
    }

    fun refreshHealthConnectState() {
        viewModelScope.launch {
            val state = healthConnectManager.getState()

            _uiState.value = _uiState.value.copy(
                isHealthConnectAvailable =
                    state.availability == com.example.frontpage.sleep.model.HealthConnectAvailability.Available,
                hasHealthConnectPermission = state.hasStepsPermission
            )
        }
    }

    fun refreshTodaySteps() {
        viewModelScope.launch {
            val state = healthConnectManager.getState()

            _uiState.value = _uiState.value.copy(
                isHealthConnectAvailable =
                    state.availability == com.example.frontpage.sleep.model.HealthConnectAvailability.Available,
                hasHealthConnectPermission = state.hasStepsPermission
            )

            if (!state.hasStepsPermission) {
                return@launch
            }

            val todaySteps = healthConnectManager.readTodaySteps().toInt()

            _uiState.value = _uiState.value.copy(
                steps = todaySteps
            )

            saveTodayStepsToDatabase(
                steps = todaySteps,
                goal = _uiState.value.goal
            )
        }
    }

    fun changeGoal(newGoal: Int) {
        _uiState.value = _uiState.value.copy(
            goal = newGoal
        )

        saveTodayStepsToDatabase(
            steps = _uiState.value.steps,
            goal = newGoal
        )
    }



    private fun getStartOfTodayMillis(): Long {
        val calendar = java.util.Calendar.getInstance()

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        return calendar.timeInMillis
    }

    private fun shouldSaveSteps(newSteps: Int): Boolean {
        return kotlin.math.abs(newSteps - lastSavedSteps) >= 10
    }
}