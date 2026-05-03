package com.example.frontpage.sleep.model

data class SleepEntry(
    val id: Int,
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