package com.example.frontpage.sleep.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_entries")
data class SleepEntry(
    @PrimaryKey
    val id: Long,
    val date: String,
    val sleepHour: Int,
    val sleepMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val durationMinutes: Int,
    val quality: SleepQuality,
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)