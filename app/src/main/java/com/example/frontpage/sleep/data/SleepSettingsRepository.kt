package com.example.frontpage.sleep.data

import android.content.Context
import androidx.core.content.edit

object SleepSettingsRepository {

    const val DEFAULT_SLEEP_GOAL_MINUTES: Int = 8 * 60

    private const val PREFERENCES_NAME = "sleep_settings"
    private const val GOAL_KEY_PREFIX = "sleep_goal_minutes_user_"

    fun getSleepGoalMinutes(
        context: Context,
        userId: Long?
    ): Int {
        if (userId == null) return DEFAULT_SLEEP_GOAL_MINUTES

        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getInt(
                goalKey(userId),
                DEFAULT_SLEEP_GOAL_MINUTES
            )
    }

    fun updateSleepGoalMinutes(
        context: Context,
        userId: Long?,
        newGoalMinutes: Int
    ): Int {
        val goalMinutes = newGoalMinutes.coerceIn(
            minimumValue = 4 * 60,
            maximumValue = 12 * 60
        )

        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    putInt(
                        goalKey(userId),
                        goalMinutes
                    )
                }
        }

        return goalMinutes
    }

    private fun goalKey(userId: Long): String {
        return "$GOAL_KEY_PREFIX$userId"
    }
}
