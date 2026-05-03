package com.example.frontpage.sleep

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
}