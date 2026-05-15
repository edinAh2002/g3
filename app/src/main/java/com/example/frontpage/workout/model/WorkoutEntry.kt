package com.example.frontpage.workout.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_entries")
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val userId: Long,

    val dateMillis: Long,

    val name: String,

    val durationMinutes: Int,

    val exercisesText: String
)