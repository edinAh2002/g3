package com.example.frontpage.sleep

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