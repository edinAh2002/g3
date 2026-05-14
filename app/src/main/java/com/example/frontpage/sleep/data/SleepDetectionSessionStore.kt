package com.example.frontpage.sleep.data

import android.content.Context
import androidx.core.content.edit

data class SleepDetectionSessionState(
    val startMillis: Long,
    val alarmMillis: Long?,
    val interruptionMillis: Long,
    val interruptionStartMillis: Long?
)

class SleepDetectionSessionStore(
    context: Context
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    fun load(): SleepDetectionSessionState? {
        val startMillis = preferences.getLong(KEY_START_MILLIS, 0L)
        if (startMillis <= 0L) return null

        val storedAlarmMillis = preferences.getLong(KEY_ALARM_MILLIS, 0L)
        val storedInterruptionStartMillis = preferences.getLong(
            KEY_INTERRUPTION_START_MILLIS,
            0L
        )

        return SleepDetectionSessionState(
            startMillis = startMillis,
            alarmMillis = storedAlarmMillis.takeIf { value -> value > 0L },
            interruptionMillis = preferences.getLong(KEY_INTERRUPTION_MILLIS, 0L),
            interruptionStartMillis = storedInterruptionStartMillis.takeIf { value ->
                value > 0L
            }
        )
    }

    fun save(state: SleepDetectionSessionState) {
        preferences.edit {
            putLong(KEY_START_MILLIS, state.startMillis)
            putLong(KEY_ALARM_MILLIS, state.alarmMillis ?: 0L)
            putLong(KEY_INTERRUPTION_MILLIS, state.interruptionMillis)
            putLong(KEY_INTERRUPTION_START_MILLIS, state.interruptionStartMillis ?: 0L)
        }
    }

    fun clear() {
        preferences.edit {
            remove(KEY_START_MILLIS)
            remove(KEY_ALARM_MILLIS)
            remove(KEY_INTERRUPTION_MILLIS)
            remove(KEY_INTERRUPTION_START_MILLIS)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "sleep_detection_session"
        const val KEY_START_MILLIS = "start_millis"
        const val KEY_ALARM_MILLIS = "alarm_millis"
        const val KEY_INTERRUPTION_MILLIS = "interruption_millis"
        const val KEY_INTERRUPTION_START_MILLIS = "interruption_start_millis"
    }
}
