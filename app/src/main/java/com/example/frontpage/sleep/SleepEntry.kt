package com.example.frontpage.sleep

data class SleepEntry(
    val id: Int,
    val date: String,
    val sleepHour: Int,
    val sleepMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val durationMinutes: Int,
    val quality: SleepQuality,
    val notes: String = ""
)