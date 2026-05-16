package com.example.frontpage.workout.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontpage.workout.model.WorkoutEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query(
        """
        SELECT * FROM workout_entries
        WHERE userId = :userId
        ORDER BY dateMillis DESC
        """
    )
    fun getWorkoutsForUser(userId: Long): Flow<List<WorkoutEntry>>

    @Query(
        """
        SELECT * FROM workout_entries
        WHERE userId = :userId
        ORDER BY dateMillis DESC
        LIMIT 1
        """
    )
    fun getLatestWorkoutForUser(userId: Long): Flow<WorkoutEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWorkout(workout: WorkoutEntry)

    @Query(
        """
        DELETE FROM workout_entries
        WHERE id = :id AND userId = :userId
        """
    )
    suspend fun deleteWorkoutForUser(
        id: Long,
        userId: Long
    )

    @Query(
        """
        DELETE FROM workout_entries
        WHERE userId = :userId
        """
    )
    suspend fun clearWorkoutsForUser(userId: Long)
}