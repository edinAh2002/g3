package com.example.frontpage.sleep.data

object SleepSettingsRepository {

    var sleepGoalMinutes: Int = 8 * 60
        private set

    fun updateSleepGoalMinutes(newGoalMinutes: Int) {
        sleepGoalMinutes = newGoalMinutes.coerceIn(
            minimumValue = 4 * 60,
            maximumValue = 12 * 60
        )
    }
}