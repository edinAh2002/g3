package com.example.frontpage.mood.ui.components

import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal data class MoodCalendarMonthState(
    val year: Int,
    val month: Int
) {
    fun offset(monthOffset: Int): MoodCalendarMonthState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, monthOffset)
        }

        return MoodCalendarMonthState(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH)
        )
    }

    fun firstDayMillis(): Long {
        return Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
    }

    fun label(): String {
        val calendar = Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }
}

internal data class CalendarDayMood(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val averageMood: Double?,
    val entryCount: Int
) {
    val progress: Float
        get() = ((averageMood ?: 0.0) / 5.0).toFloat()
}

internal fun buildMonthMoodProgress(
    monthState: MoodCalendarMonthState,
    moodEntries: List<MoodEntry>
): List<CalendarDayMood?> {
    val entriesByDate = moodEntries.groupBy { entry -> entry.date }

    val firstDayCalendar = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, monthState.year)
        set(Calendar.MONTH, monthState.month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = firstDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingBlankDays = mondayFirstOffset(firstDayCalendar.get(Calendar.DAY_OF_WEEK))

    val days = mutableListOf<CalendarDayMood?>()
    repeat(leadingBlankDays) {
        days += null
    }

    for (day in 1..daysInMonth) {
        val dateMillis = Calendar.getInstance().apply {
            clear()
            set(Calendar.YEAR, monthState.year)
            set(Calendar.MONTH, monthState.month)
            set(Calendar.DAY_OF_MONTH, day)
        }.timeInMillis

        val date = MoodDateUtils.formatIsoDate(dateMillis)
        val entries = entriesByDate[date].orEmpty()

        days += CalendarDayMood(
            dayOfMonth = day,
            dateMillis = dateMillis,
            averageMood = MoodStatsCalculator.getAverageMood(entries),
            entryCount = entries.size
        )
    }

    while (days.size % 7 != 0) {
        days += null
    }

    return days
}

internal fun startOfMoodCalendarDayMillis(dateMillis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun mondayFirstOffset(dayOfWeek: Int): Int {
    return (dayOfWeek + 5) % 7
}

