package com.example.frontpage.sleep.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SleepDateUtils {

    private val historyDateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun formatHistoryDate(dateMillis: Long): String {
        return when {
            isToday(dateMillis) -> "Today"
            isYesterday(dateMillis) -> "Yesterday"
            else -> historyDateFormat.format(Date(dateMillis))
        }
    }

    fun formatShortDate(dateMillis: Long): String {
        return shortDateFormat.format(Date(dateMillis))
    }

    fun formatDayName(dateMillis: Long): String {
        return dayNameFormat.format(Date(dateMillis))
    }

    fun isToday(dateMillis: Long): Boolean {
        val today = Calendar.getInstance()
        val entryDate = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }

        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(dateMillis: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        val entryDate = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }

        return yesterday.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == entryDate.get(Calendar.DAY_OF_YEAR)
    }

    fun isThisWeek(dateMillis: Long): Boolean {
        val today = Calendar.getInstance()
        val entryDate = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }

        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                today.get(Calendar.WEEK_OF_YEAR) == entryDate.get(Calendar.WEEK_OF_YEAR)
    }

    fun isThisMonth(dateMillis: Long): Boolean {
        val today = Calendar.getInstance()
        val entryDate = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }

        return today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == entryDate.get(Calendar.MONTH)
    }

    fun isSameDay(firstMillis: Long, secondMillis: Long): Boolean {
        val first = Calendar.getInstance().apply {
            timeInMillis = firstMillis
        }

        val second = Calendar.getInstance().apply {
            timeInMillis = secondMillis
        }

        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
    }
}