package com.example.frontpage.sleep.domain

import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import java.util.Calendar

fun buildWeeklySleepChartData(
    sleepLogs: List<SleepEntry>
): List<WeeklySleepChartItem> {
    val calendar = Calendar.getInstance()
    val items = mutableListOf<WeeklySleepChartItem>()

    for (daysAgo in 6 downTo 0) {
        val dayCalendar = calendar.clone() as Calendar
        dayCalendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        val dayMillis = dayCalendar.timeInMillis

        val latestLogForDay = sleepLogs
            .filter { SleepDateUtils.isSameDay(it.dateMillis, dayMillis) }
            .maxByOrNull { it.dateMillis }

        items.add(
            WeeklySleepChartItem(
                dayLabel = SleepDateUtils.formatDayName(dayMillis),
                durationMinutes = latestLogForDay?.durationMinutes ?: 0,
                dateMillis = dayMillis
            )
        )
    }

    return items
}