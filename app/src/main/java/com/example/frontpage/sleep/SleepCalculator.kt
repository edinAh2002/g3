package com.example.frontpage.sleep
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object SleepCalculator {

    fun calculateDurationMinutes(
        sleepHour: Int,
        sleepMinute: Int,
        wakeHour: Int,
        wakeMinute: Int
    ): Int {
        val sleepTotalMinutes = sleepHour * 60 + sleepMinute
        var wakeTotalMinutes = wakeHour * 60 + wakeMinute

        // Handles sleeping before midnight and waking up after midnight
        if (wakeTotalMinutes < sleepTotalMinutes) {
            wakeTotalMinutes += 24 * 60
        }

        return wakeTotalMinutes - sleepTotalMinutes
    }

    fun calculateDurationHours(
        sleepHour: Int,
        sleepMinute: Int,
        wakeHour: Int,
        wakeMinute: Int
    ): Double {
        val durationMinutes = calculateDurationMinutes(
            sleepHour,
            sleepMinute,
            wakeHour,
            wakeMinute
        )

        return durationMinutes / 60.0
    }

    fun formatDuration(durationMinutes: Int): String {
        val hours = durationMinutes / 60
        val minutes = durationMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }

    fun formatTime(hour: Int, minute: Int): String {
        val formattedHour = hour.toString().padStart(2, '0')
        val formattedMinute = minute.toString().padStart(2, '0')

        return "$formattedHour:$formattedMinute"
    }

    fun getSleepFeedback(durationMinutes: Int): String {
        return when {
            durationMinutes < 5 * 60 -> "You slept less than 5 hours. Try to get more rest tonight."
            durationMinutes < 7 * 60 -> "You were a bit short on sleep. Aim for at least 7 hours."
            durationMinutes <= 9 * 60 -> "Great job! Your sleep duration is in a healthy range."
            else -> "You slept more than 9 hours. Make sure your sleep schedule stays consistent."
        }
    }

    fun calculateGoalProgress(
        durationMinutes: Int,
        goalMinutes: Int = 8 * 60
    ): Float {
        if (goalMinutes <= 0) return 0f

        return (durationMinutes.toFloat() / goalMinutes.toFloat()).coerceAtMost(1f)
    }

    fun getGoalStatusTitle(
        durationMinutes: Int,
        goalMinutes: Int
    ): String {
        if (goalMinutes <= 0) return "Sleep logged"

        val progress = durationMinutes.toFloat() / goalMinutes.toFloat()

        return when {
            progress < 0.75f -> "Needs more rest"
            progress < 1f -> "Almost there"
            progress <= 1.15f -> "Goal reached"
            else -> "Long sleep logged"
        }
    }

    fun getGoalDifferenceText(
        durationMinutes: Int,
        goalMinutes: Int
    ): String {
        val difference = durationMinutes - goalMinutes

        return when {
            difference == 0 -> "You exactly reached your sleep goal."
            difference < 0 -> "You were ${formatDuration(-difference)} short of your goal."
            else -> "You slept ${formatDuration(difference)} more than your goal."
        }
    }

    fun getImprovementSuggestion(
        durationMinutes: Int,
        goalMinutes: Int
    ): String {
        val difference = goalMinutes - durationMinutes

        return when {
            difference > 90 -> "Try going to bed around 30 minutes earlier tonight."
            difference > 30 -> "Try going to bed 15–20 minutes earlier tonight."
            difference > 0 -> "You were very close. A slightly earlier bedtime could help."
            durationMinutes <= goalMinutes + 90 -> "Great job. Try to keep this sleep routine consistent."
            else -> "You slept much longer than your goal. Check if your schedule feels balanced."
        }
    }

    fun calculateGoalProgressPercent(
        durationMinutes: Int,
        goalMinutes: Int
    ): Int {
        if (goalMinutes <= 0) return 0

        return ((durationMinutes.toFloat() / goalMinutes.toFloat()) * 100).toInt()
    }

    private const val MINUTES_IN_DAY = 24 * 60

    fun toClockMinutes(hour: Int, minute: Int): Int {
        val total = hour * 60 + minute
        return ((total % MINUTES_IN_DAY) + MINUTES_IN_DAY) % MINUTES_IN_DAY
    }

    fun formatClockMinutes(clockMinutes: Int): String {
        val normalizedMinutes = ((clockMinutes % MINUTES_IN_DAY) + MINUTES_IN_DAY) % MINUTES_IN_DAY
        val hour = normalizedMinutes / 60
        val minute = normalizedMinutes % 60

        return formatTime(hour, minute)
    }

    fun calculateAverageBedtimeMinutes(sleepLogs: List<SleepEntry>): Int? {
        return calculateCircularAverageMinutes(
            sleepLogs.map {
                toClockMinutes(it.sleepHour, it.sleepMinute)
            }
        )
    }

    fun calculateAverageWakeTimeMinutes(sleepLogs: List<SleepEntry>): Int? {
        return calculateCircularAverageMinutes(
            sleepLogs.map {
                toClockMinutes(it.wakeHour, it.wakeMinute)
            }
        )
    }

    private fun calculateCircularAverageMinutes(clockTimes: List<Int>): Int? {
        if (clockTimes.isEmpty()) return null

        var totalSin = 0.0
        var totalCos = 0.0

        clockTimes.forEach { minutes ->
            val angle = minutes.toDouble() / MINUTES_IN_DAY.toDouble() * 2.0 * PI
            totalSin += sin(angle)
            totalCos += cos(angle)
        }

        val averageAngle = atan2(
            totalSin / clockTimes.size,
            totalCos / clockTimes.size
        )

        val normalizedAngle = if (averageAngle < 0) {
            averageAngle + 2.0 * PI
        } else {
            averageAngle
        }

        return ((normalizedAngle / (2.0 * PI)) * MINUTES_IN_DAY).roundToInt() % MINUTES_IN_DAY
    }

    fun getSleepTrendSummary(sleepLogs: List<SleepEntry>): String {
        val averageBedtime = calculateAverageBedtimeMinutes(sleepLogs)
        val averageWakeTime = calculateAverageWakeTimeMinutes(sleepLogs)

        if (averageBedtime == null || averageWakeTime == null) {
            return "Log more sleep entries to see your bedtime and wake-up trends."
        }

        return "You usually sleep around ${formatClockMinutes(averageBedtime)} and wake up around ${formatClockMinutes(averageWakeTime)}."
    }
}