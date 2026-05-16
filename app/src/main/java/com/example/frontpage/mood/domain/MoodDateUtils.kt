package com.example.frontpage.mood.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MoodDateUtils {

    private const val DATE_PATTERN = "yyyy-MM-dd"
    private const val TIME_PATTERN = "HH:mm"

    fun getTodayDate(): String {
        return dateFormatter().format(Date())
    }

    fun getCurrentTime(): String {
        return SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(Date())
    }

    fun formatIsoDate(dateMillis: Long): String {
        return dateFormatter().format(Date(dateMillis))
    }

    fun parseDateMillis(date: String): Long {
        return try {
            dateFormatter().parse(date)?.time ?: System.currentTimeMillis()
        } catch (exception: Exception) {
            System.currentTimeMillis()
        }
    }

    fun getDateDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dateFormatter().format(calendar.time)
    }

    fun getLastNDates(dayCount: Int): List<String> {
        return (dayCount - 1 downTo 0).map { daysAgo ->
            getDateDaysAgo(daysAgo)
        }
    }

    fun getCurrentWeekDateRange(): Pair<String, String> {
        val formatter = dateFormatter()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = formatter.format(calendar.time)

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = formatter.format(calendar.time)

        return startOfWeek to endOfWeek
    }

    fun getCurrentMonthDateRange(): Pair<String, String> {
        val formatter = dateFormatter()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = formatter.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endOfMonth = formatter.format(calendar.time)

        return startOfMonth to endOfMonth
    }

    fun getCurrentWeekDisplayRange(): String {
        val weekRange = getCurrentWeekDateRange()
        return "${weekRange.first} to ${weekRange.second}"
    }

    fun getDayNameFromDate(date: String): String {
        return formatDate(date, "EEEE")
    }

    fun formatDayLabel(date: String): String {
        return formatDate(date, "EEE")
    }

    fun formatDisplayDate(date: String): String {
        return formatDate(date, "MMM d, yyyy")
    }

    private fun formatDate(
        date: String,
        outputPattern: String
    ): String {
        return try {
            val parsedDate = dateFormatter().parse(date)

            if (parsedDate == null) {
                date
            } else {
                SimpleDateFormat(outputPattern, Locale.getDefault()).format(parsedDate)
            }
        } catch (exception: Exception) {
            date
        }
    }

    private fun dateFormatter(): SimpleDateFormat {
        return SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
    }
}
