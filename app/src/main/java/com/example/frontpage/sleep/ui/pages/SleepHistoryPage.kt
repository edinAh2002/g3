package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepHistoryCard
import com.example.frontpage.sleep.ui.components.SleepMetricTile
import com.example.frontpage.sleep.ui.components.SleepSectionHeader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
            MonthState(
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

    val selectedDayStart = startOfDayMillis(selectedDateMillis)
    val selectedDayLogs = sleepLogs
        .filter { entry -> startOfDayMillis(entry.dateMillis) == selectedDayStart }
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

@Composable
private fun SleepProgressCalendar(
    monthState: MonthState,
    sleepLogs: List<SleepEntry>,
    selectedDateMillis: Long,
    goalMinutesForDate: (Long) -> Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (Long) -> Unit
) {
    val dayProgress = buildMonthProgress(
        monthState = monthState,
        sleepLogs = sleepLogs,
        goalMinutesForDate = goalMinutesForDate
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onPreviousMonth) {
                    Text("Prev")
                }

                Text(
                    text = monthState.label(),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(onClick = onNextMonth) {
                    Text("Next")
                }
            }

            CalendarWeekHeader()

            dayProgress.chunked(7).forEach { week ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        SleepCalendarDayCell(
                            day = day,
                            isSelected = day?.dateMillis?.let { dateMillis ->
                                startOfDayMillis(dateMillis) == startOfDayMillis(selectedDateMillis)
                            } == true,
                            onDaySelected = onDaySelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Text(
                text = "Filled days show progress toward that day's sleep goal.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SleepCalendarDayCell(
    day: CalendarDayProgress?,
    isSelected: Boolean,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val colorScheme = MaterialTheme.colorScheme
    val progress = day?.progress?.coerceIn(0f, 1f) ?: 0f
    val fillColor = sleepProgressColor(progress)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(colorScheme.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colorScheme.primary else colorScheme.outlineVariant,
                shape = shape
            )
            .then(
                if (day == null) {
                    Modifier
                } else {
                    Modifier.clickable {
                        onDaySelected(day.dateMillis)
                    }
                }
            )
    ) {
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(progress)
                    .align(Alignment.BottomCenter)
                    .background(fillColor)
            )
        }

        Text(
            text = day?.dayOfMonth?.toString().orEmpty(),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DeleteSleepDialog(
    entry: SleepEntry,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Sleep Log?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Are you sure you want to delete this sleep log?")
                Text(SleepDateUtils.formatHistoryDate(entry.dateMillis))
                Text(
                    "${SleepCalculator.formatTime(entry.sleepHour, entry.sleepMinute)} to " +
                            SleepCalculator.formatTime(entry.wakeHour, entry.wakeMinute)
                )
                Text("Duration: ${SleepCalculator.formatDuration(entry.durationMinutes)}")
                Text("Quality: ${entry.quality}")
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmDelete
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

private data class MonthState(
    val year: Int,
    val month: Int
) {
    fun offset(monthOffset: Int): MonthState {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, monthOffset)
        }

        return MonthState(
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

private data class CalendarDayProgress(
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

    val reachedGoal: Boolean
        get() = durationMinutes >= goalMinutes
}

private fun buildMonthProgress(
    monthState: MonthState,
    sleepLogs: List<SleepEntry>,
    goalMinutesForDate: (Long) -> Int
): List<CalendarDayProgress?> {
    val logsByDay = sleepLogs.groupBy { entry ->
        startOfDayMillis(entry.dateMillis)
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

private fun mondayFirstOffset(dayOfWeek: Int): Int {
    return (dayOfWeek + 5) % 7
}

private fun startOfDayMillis(dateMillis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
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

private fun sleepProgressColor(progress: Float): Color {
    val red = Color(0xFFE57373)
    val yellow = Color(0xFFFFD54F)
    val green = Color(0xFF43A047)
    val clampedProgress = progress.coerceIn(0f, 1f)

    return when {
        progress <= 0f -> Color.Transparent
        clampedProgress < 0.5f -> lerp(
            start = red,
            stop = yellow,
            fraction = clampedProgress / 0.5f
        )

        else -> lerp(
            start = yellow,
            stop = green,
            fraction = (clampedProgress - 0.5f) / 0.5f
        )
    }
}
