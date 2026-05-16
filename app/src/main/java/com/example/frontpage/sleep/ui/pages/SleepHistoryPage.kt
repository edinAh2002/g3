package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepCalendarMonthState
import com.example.frontpage.sleep.ui.components.SleepHistoryCard
import com.example.frontpage.sleep.ui.components.SleepMetricTile
import com.example.frontpage.sleep.ui.components.SleepProgressCalendar
import com.example.frontpage.sleep.ui.components.SleepSectionHeader
import com.example.frontpage.sleep.ui.components.startOfSleepCalendarDayMillis
import com.example.frontpage.sleep.ui.dialogs.DeleteSleepDialog
import java.util.Calendar

@Composable
fun SleepHistoryPage(
    sleepLogs: List<SleepEntry>,
    goalMinutesForDate: (Long) -> Int,
    onEditEntry: (SleepEntry) -> Unit,
    onDeleteEntry: (SleepEntry) -> Unit
) {
    var deletingEntry by remember { mutableStateOf<SleepEntry?>(null) }
    var selectedDateMillis by remember(sleepLogs) {
        mutableLongStateOf(sleepLogs.maxByOrNull { entry -> entry.dateMillis }?.dateMillis ?: System.currentTimeMillis())
    }

    var visibleMonth by remember(selectedDateMillis) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
        }

        mutableStateOf(
            SleepCalendarMonthState(
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH)
            )
        )
    }

    val averageSleepMinutes = if (sleepLogs.isEmpty()) {
        0
    } else {
        sleepLogs.map { entry -> entry.durationMinutes }.average().toInt()
    }

    val thisWeekCount = sleepLogs.count { entry ->
        SleepDateUtils.isThisWeek(entry.dateMillis)
    }

    val selectedDayStart = startOfSleepCalendarDayMillis(selectedDateMillis)
    val selectedDayLogs = sleepLogs
        .filter { entry -> startOfSleepCalendarDayMillis(entry.dateMillis) == selectedDayStart }
        .sortedByDescending { entry -> entry.dateMillis }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SleepSectionHeader(
            title = "Sleep History",
            subtitle = "Tap a day to see the sleep logs saved for it."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Total Logs",
                value = sleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Average",
                value = SleepCalculator.formatDuration(averageSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "This Week",
                value = thisWeekCount.toString(),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Selected Day",
                value = selectedDayLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        SleepProgressCalendar(
            monthState = visibleMonth,
            sleepLogs = sleepLogs,
            selectedDateMillis = selectedDateMillis,
            goalMinutesForDate = goalMinutesForDate,
            onPreviousMonth = {
                visibleMonth = visibleMonth.offset(monthOffset = -1)
                selectedDateMillis = visibleMonth.offset(monthOffset = -1).firstDayMillis()
            },
            onNextMonth = {
                visibleMonth = visibleMonth.offset(monthOffset = 1)
                selectedDateMillis = visibleMonth.offset(monthOffset = 1).firstDayMillis()
            },
            onDaySelected = { dayMillis ->
                selectedDateMillis = dayMillis
            }
        )

        SleepSectionHeader(
            title = SleepDateUtils.formatHistoryDate(selectedDateMillis),
            subtitle = selectedDaySummary(
                selectedDayLogs = selectedDayLogs,
                goalMinutes = goalMinutesForDate(selectedDateMillis)
            )
        )

        if (sleepLogs.isEmpty()) {
            Text("Your sleep history will appear here.")
        } else if (selectedDayLogs.isEmpty()) {
            Text("No sleep logs saved for this day.")
        } else {
            selectedDayLogs.forEach { entry ->
                SleepHistoryCard(
                    entry = entry,
                    onEdit = {
                        onEditEntry(entry)
                    },
                    onDelete = {
                        deletingEntry = entry
                    }
                )
            }
        }
    }

    deletingEntry?.let { entry ->
        DeleteSleepDialog(
            entry = entry,
            onDismiss = {
                deletingEntry = null
            },
            onConfirmDelete = {
                onDeleteEntry(entry)
                deletingEntry = null
            }
        )
    }
}

private fun selectedDaySummary(
    selectedDayLogs: List<SleepEntry>,
    goalMinutes: Int
): String {
    if (selectedDayLogs.isEmpty()) {
        return "No sleep saved for this day."
    }

    val totalMinutes = selectedDayLogs.sumOf { entry ->
        entry.durationMinutes
    }

    val progressPercent = if (goalMinutes <= 0) {
        0
    } else {
        ((totalMinutes.toFloat() / goalMinutes.toFloat()) * 100).toInt()
    }

    return "${SleepCalculator.formatDuration(totalMinutes)} of ${SleepCalculator.formatDuration(goalMinutes)} goal - $progressPercent%"
}
