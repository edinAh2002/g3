package com.example.frontpage.sleep.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_entries")
data class SleepEntry(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(defaultValue = "0")
    val userId: Long = 0L,
    val date: String,
    val sleepHour: Int,
    val sleepMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val durationMinutes: Int,
    val quality: SleepQuality,
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis(),

    @ColumnInfo(defaultValue = "")
    val dreamJournal: String = "",

    @ColumnInfo(defaultValue = "None")
    val snoringLevel: SnoringLevel = SnoringLevel.None,

    @ColumnInfo(defaultValue = "")
    val tags: String = "",

    @ColumnInfo(defaultValue = "Manual")
    val source: SleepSource = SleepSource.Manual
)
