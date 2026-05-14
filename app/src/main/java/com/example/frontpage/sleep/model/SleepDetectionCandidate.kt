package com.example.frontpage.sleep.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "sleep_detection_candidates",
    indices = [
        Index(value = ["userId", "status"]),
        Index(value = ["userId", "wakeDateMillis"])
    ]
)
data class SleepDetectionCandidate(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(defaultValue = "0")
    val userId: Long = 0L,
    val startMillis: Long,
    val endMillis: Long,
    val wakeDateMillis: Long,
    val alarmMillis: Long?,
    val confidence: Int,
    val signalSummary: String,

    @ColumnInfo(defaultValue = "Pending")
    val status: SleepDetectionStatus = SleepDetectionStatus.Pending,

    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

enum class SleepDetectionStatus {
    Pending,
    Accepted,
    Dismissed
}

data class SleepDetectionSettings(
    val enabled: Boolean = false,
    val minimumSleepMinutes: Int = DEFAULT_MINIMUM_SLEEP_MINUTES,
    val alarmMatchWindowMinutes: Int = DEFAULT_ALARM_MATCH_WINDOW_MINUTES,
    val interruptionToleranceMinutes: Int = DEFAULT_INTERRUPTION_TOLERANCE_MINUTES
) {
    companion object {
        const val DEFAULT_MINIMUM_SLEEP_MINUTES = 4 * 60
        const val DEFAULT_ALARM_MATCH_WINDOW_MINUTES = 90
        const val DEFAULT_INTERRUPTION_TOLERANCE_MINUTES = 15
    }
}

fun SleepDetectionCandidate.toSleepLogDraft(): SleepLogDraft {
    val sleepCalendar = Calendar.getInstance().apply {
        timeInMillis = startMillis
    }
    val wakeCalendar = Calendar.getInstance().apply {
        timeInMillis = endMillis
    }
    val durationMinutes = ((endMillis - startMillis) / 60000L)
        .toInt()
        .coerceAtLeast(0)

    return SleepLogDraft(
        sleepHour = sleepCalendar.get(Calendar.HOUR_OF_DAY),
        sleepMinute = sleepCalendar.get(Calendar.MINUTE),
        wakeHour = wakeCalendar.get(Calendar.HOUR_OF_DAY),
        wakeMinute = wakeCalendar.get(Calendar.MINUTE),
        wakeDateMillis = wakeDateMillis,
        quality = SleepQuality.Good,
        durationMinutes = durationMinutes,
        notes = signalSummary,
        dreamJournal = "",
        snoringLevel = SnoringLevel.None,
        tags = "",
        source = SleepSource.Detected
    )
}
