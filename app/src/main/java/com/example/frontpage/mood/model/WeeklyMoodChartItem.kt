package com.example.frontpage.mood.model

data class WeeklyMoodChartItem(
    val dayLabel: String,
    val averageMood: Double?,
    val entryCount: Int,
    val date: String
)

