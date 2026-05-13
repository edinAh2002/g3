package com.example.frontpage.sleep.model

import java.util.Calendar

enum class SleepWeekday(
    val label: String,
    val shortLabel: String,
    val calendarDay: Int
) {
    Monday("Monday", "Mon", Calendar.MONDAY),
    Tuesday("Tuesday", "Tue", Calendar.TUESDAY),
    Wednesday("Wednesday", "Wed", Calendar.WEDNESDAY),
    Thursday("Thursday", "Thu", Calendar.THURSDAY),
    Friday("Friday", "Fri", Calendar.FRIDAY),
    Saturday("Saturday", "Sat", Calendar.SATURDAY),
    Sunday("Sunday", "Sun", Calendar.SUNDAY);

    companion object {
        fun fromDateMillis(dateMillis: Long): SleepWeekday {
            val calendarDay = Calendar.getInstance().apply {
                timeInMillis = dateMillis
            }.get(Calendar.DAY_OF_WEEK)

            return entries.first { weekday ->
                weekday.calendarDay == calendarDay
            }
        }
    }
}

data class WeekdaySleepSettings(
    val weekday: SleepWeekday,
    val goalMinutes: Int,
    val bedtimeMinutes: Int,
    val wakeMinutes: Int
)

data class SleepCustomTag(
    val id: String,
    val label: String
)

data class SleepTagOption(
    val storageValue: String,
    val label: String,
    val category: String,
    val customId: String? = null
)
