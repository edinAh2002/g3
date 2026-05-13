package com.example.frontpage.sleep.ui.dialogs

import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

internal fun List<WeekdaySleepSettings>.settingsForDate(dateMillis: Long): WeekdaySleepSettings? {
    val weekday = SleepWeekday.fromDateMillis(dateMillis)

    return firstOrNull { settings ->
        settings.weekday == weekday
    }
}
