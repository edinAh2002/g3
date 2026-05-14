package com.example.frontpage.sleep.domain

import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepDetectionSettings
import com.example.frontpage.sleep.model.SleepDetectionStatus
import java.util.Calendar
import kotlin.math.abs

object SleepDetectionAnalyzer {
    private const val MILLIS_PER_MINUTE = 60_000L
    private const val EVENING_START_HOUR = 18
    private const val MORNING_END_HOUR = 6

    fun shouldStartSession(screenOffMillis: Long): Boolean {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = screenOffMillis
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return hour >= EVENING_START_HOUR || hour < MORNING_END_HOUR
    }

    fun buildCandidate(
        userId: Long,
        startMillis: Long,
        endMillis: Long,
        alarmMillis: Long?,
        interruptionMillis: Long,
        settings: SleepDetectionSettings,
        nowMillis: Long = System.currentTimeMillis()
    ): SleepDetectionCandidate? {
        if (endMillis <= startMillis || alarmMillis == null) return null

        val durationMinutes = ((endMillis - startMillis) / MILLIS_PER_MINUTE).toInt()
        if (durationMinutes < settings.minimumSleepMinutes) return null

        val alarmDistanceMinutes = abs(endMillis - alarmMillis) / MILLIS_PER_MINUTE
        if (alarmDistanceMinutes > settings.alarmMatchWindowMinutes) return null

        val interruptionMinutes = (interruptionMillis / MILLIS_PER_MINUTE).toInt()
        val confidence = calculateConfidence(
            durationMinutes = durationMinutes,
            alarmDistanceMinutes = alarmDistanceMinutes.toInt(),
            interruptionMinutes = interruptionMinutes
        )
        val wakeDateMillis = startOfDayMillis(endMillis)

        return SleepDetectionCandidate(
            id = nowMillis,
            userId = userId,
            startMillis = startMillis,
            endMillis = endMillis,
            wakeDateMillis = wakeDateMillis,
            alarmMillis = alarmMillis,
            confidence = confidence,
            signalSummary = buildSignalSummary(
                durationMinutes = durationMinutes,
                alarmMillis = alarmMillis,
                interruptionMinutes = interruptionMinutes
            ),
            status = SleepDetectionStatus.Pending,
            createdAtMillis = nowMillis,
            updatedAtMillis = nowMillis
        )
    }

    fun startOfDayMillis(dateMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun endOfDayMillis(dateMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = startOfDayMillis(dateMillis)
            add(Calendar.DAY_OF_YEAR, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis
    }

    private fun calculateConfidence(
        durationMinutes: Int,
        alarmDistanceMinutes: Int,
        interruptionMinutes: Int
    ): Int {
        val durationBonus = when {
            durationMinutes >= 7 * 60 -> 25
            durationMinutes >= 6 * 60 -> 18
            durationMinutes >= 5 * 60 -> 10
            else -> 4
        }
        val alarmBonus = when {
            alarmDistanceMinutes <= 15 -> 20
            alarmDistanceMinutes <= 45 -> 14
            else -> 8
        }
        val interruptionPenalty = when {
            interruptionMinutes <= 5 -> 0
            interruptionMinutes <= 20 -> 8
            else -> 16
        }

        return (55 + durationBonus + alarmBonus - interruptionPenalty)
            .coerceIn(0, 100)
    }

    private fun buildSignalSummary(
        durationMinutes: Int,
        alarmMillis: Long,
        interruptionMinutes: Int
    ): String {
        val alarmCalendar = Calendar.getInstance().apply {
            timeInMillis = alarmMillis
        }
        val alarmText = SleepCalculator.formatTime(
            hour = alarmCalendar.get(Calendar.HOUR_OF_DAY),
            minute = alarmCalendar.get(Calendar.MINUTE)
        )
        val interruptionText = if (interruptionMinutes > 0) {
            " Brief screen use totaled ${SleepCalculator.formatDuration(interruptionMinutes)}."
        } else {
            ""
        }

        return "Detected ${SleepCalculator.formatDuration(durationMinutes)} of screen-off rest near your $alarmText alarm.$interruptionText"
    }
}
