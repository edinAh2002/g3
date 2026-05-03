package com.example.frontpage.sleep.model

data class WeeklySleepChartItem(
    val dayLabel: String,
    val durationMinutes: Int,
    val dateMillis: Long
)