package com.example.frontpage.workout.data

import com.example.frontpage.workout.model.WorkoutEntry
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val workoutDao: WorkoutDao
) {
    fun getWorkoutsForUser(userId: Long): Flow<List<WorkoutEntry>> {
        return workoutDao.getWorkoutsForUser(userId)
    }

    fun getLatestWorkoutForUser(userId: Long): Flow<WorkoutEntry?> {
        return workoutDao.getLatestWorkoutForUser(userId)
    }

    suspend fun addWorkout(
        userId: Long,
        workout: WorkoutEntry
    ) {
        workoutDao.addWorkout(
            workout.copy(userId = userId)
        )
    }

    suspend fun deleteWorkout(
        userId: Long,
        id: Long
    ) {
        workoutDao.deleteWorkoutForUser(
            id = id,
            userId = userId
        )
    }

    suspend fun clearWorkoutsForUser(userId: Long) {
        workoutDao.clearWorkoutsForUser(userId)
    }
}