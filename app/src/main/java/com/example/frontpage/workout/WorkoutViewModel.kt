package com.example.frontpage.workout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.workout.data.WorkoutRepository
import com.example.frontpage.workout.domain.encodeExercises
import com.example.frontpage.workout.model.WorkoutEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    private val authRepository: AuthRepository

    private val currentUserId = MutableStateFlow<Long?>(null)

    val workouts: StateFlow<List<WorkoutEntry>>

    init {
        val database = AppDatabase.getDatabase(application)

        repository = WorkoutRepository(
            workoutDao = database.workoutDao()
        )

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        currentUserId.value = authRepository.getCurrentUserId()

        workouts = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getWorkoutsForUser(userId)
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
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        return userId
    }

    fun addWorkout(
        name: String,
        durationMinutes: Int,
        exercises: List<String>
    ) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch
            val now = System.currentTimeMillis()

            val workout = WorkoutEntry(
                userId = userId,
                dateMillis = now,
                name = name,
                durationMinutes = durationMinutes,
                exercisesText = encodeExercises(exercises)
            )

            repository.addWorkout(
                userId = userId,
                workout = workout
            )
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.deleteWorkout(
                userId = userId,
                id = id
            )
        }
    }

    fun clearAllWorkouts() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.clearWorkoutsForUser(userId)
        }
    }
}