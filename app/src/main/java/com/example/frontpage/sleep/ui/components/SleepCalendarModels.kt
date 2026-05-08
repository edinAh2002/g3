package com.example.frontpage.sleep.ui.components

import com.example.frontpage.sleep.model.SleepEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal data class SleepCalendarMonthState(
    val year: Int,
    val month: Int
) {
    fun offset(monthOffset: Int): SleepCalendarMonthState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, monthOffset)
        }

        return SleepCalendarMonthState(
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

internal data class CalendarDayProgress(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val durationMinutes: Int,
    val goalMinutes: Int
) {
    val progress: Float
        get() = if (goalMinutes <= 0) {
            0f
        } else {
            durationMinutes.toFloat() / goalMinutes.toFloat()
        }
}

internal fun buildMonthProgress(
    monthState: SleepCalendarMonthState,
    sleepLogs: List<SleepEntry>,
    goalMinutesForDate: (Long) -> Int
): List<CalendarDayProgress?> {
    val logsByDay = sleepLogs.groupBy { entry ->
        startOfSleepCalendarDayMillis(entry.dateMillis)
    }

    val firstDayCalendar = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, monthState.year)
        set(Calendar.MONTH, monthState.month)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = firstDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingBlankDays = mondayFirstOffset(firstDayCalendar.get(Calendar.DAY_OF_WEEK))

    val days = mutableListOf<CalendarDayProgress?>()
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

        val durationMinutes = logsByDay[dateMillis].orEmpty().sumOf { entry ->
            entry.durationMinutes
        }

        days += CalendarDayProgress(
            dayOfMonth = day,
            dateMillis = dateMillis,
            durationMinutes = durationMinutes,
            goalMinutes = goalMinutesForDate(dateMillis)
        )
    }

    while (days.size % 7 != 0) {
        days += null
    }

    return days
}

internal fun startOfSleepCalendarDayMillis(dateMillis: Long): Long {
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
