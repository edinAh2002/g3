package com.example.frontpage.mood.model

enum class MoodFeelingFilter(val label: String, val moodValue: Int?) {
    All("All moods", null),
    VeryBad("Very bad", 1),
    Bad("Bad", 2),
    Okay("Okay", 3),
    Good("Good", 4),
    Great("Great", 5)
}

data class MoodLogFilterState(
    val feelingFilter: MoodFeelingFilter = MoodFeelingFilter.All
)
