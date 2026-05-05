package com.example.frontpage.mood.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MoodDateUtils {

    private const val DATE_PATTERN = "yyyy-MM-dd"
    private const val TIME_PATTERN = "HH:mm"

    fun getTodayDate(): String {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return formatter.format(Date())
    }

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat(TIME_PATTERN, Locale.getDefault())
        return formatter.format(Date())
    }

    fun getCurrentWeekDateRange(): Pair<String, String> {
        val formatter = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = formatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = formatter.format(calendar.time)

        return startOfWeek to endOfWeek
    }

    fun getCurrentWeekDisplayRange(): String {
        val weekRange = getCurrentWeekDateRange()
        return "${weekRange.first} to ${weekRange.second}"
    }

    fun getDayNameFromDate(date: String): String {
        return try {
            val parser = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
            val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
            val parsedDate = parser.parse(date)

            if (parsedDate == null) {
                date
            } else {
                formatter.format(parsedDate)
            }
        } catch (exception: Exception) {
            date
        }
    }
}