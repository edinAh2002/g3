package com.example.frontpage.mood.domain

object MoodLabelUtils {

    fun getMoodLabel(moodValue: Int): String {
        return when (moodValue) {
            1 -> "😞 Very bad"
            2 -> "😕 Bad"
            3 -> "😐 Okay"
            4 -> "🙂 Good"
            5 -> "😄 Great"
            else -> "Unknown"
        }
    }
}