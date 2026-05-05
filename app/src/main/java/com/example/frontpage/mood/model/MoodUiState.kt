package com.example.frontpage.mood.model

enum class MoodLogView {
    List,
    Week,
    Summary
}

data class MoodTrackingUiState(
    val selectedMood: Int = 0,
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)