package com.example.frontpage.mood.model

enum class MoodFeelingFilter(val label: String, val moodValue: Int?) {
    All("All moods", null),
    VeryBad("Very bad", 1),
    Bad("Bad", 2),
    Okay("Okay", 3),
    Good("Good", 4),
    Great("Great", 5)
}

enum class MoodDateFilter(val label: String) {
    All("All dates"),
    Today("Today"),
    ThisWeek("This week")
}

data class MoodLogFilterState(
    val feelingFilter: MoodFeelingFilter = MoodFeelingFilter.All,
    val dateFilter: MoodDateFilter = MoodDateFilter.All
)

data class MoodTrackingUiState(
    val selectedMood: Int = 0,
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)