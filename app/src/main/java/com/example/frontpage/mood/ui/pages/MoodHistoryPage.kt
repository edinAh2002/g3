package com.example.frontpage.mood.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.ui.components.MoodCalendarMonthState
import com.example.frontpage.mood.ui.components.MoodHistoryCard
import com.example.frontpage.mood.ui.components.MoodHistoryFilterRow
import com.example.frontpage.mood.ui.components.MoodMetricTile
import com.example.frontpage.mood.ui.components.MoodProgressCalendar
import com.example.frontpage.mood.ui.components.MoodSectionHeader
import com.example.frontpage.mood.ui.components.startOfMoodCalendarDayMillis
import com.example.frontpage.mood.ui.dialogs.DeleteMoodDialog
import com.example.frontpage.mood.ui.dialogs.DeleteMoodLogsDialog
import java.util.Calendar

@Composable
fun MoodHistoryPage(
    moodEntries: List<MoodEntry>,
    filteredMoodEntries: List<MoodEntry>,
    filteredAverageMood: Double?,
    filterState: MoodLogFilterState,
    scalePreset: MoodScalePreset,
    onFeelingFilterSelected: (MoodFeelingFilter) -> Unit,
    onClearFilters: () -> Unit,
    onEditEntry: (MoodEntry) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit,
    onDeleteEntries: (List<MoodEntry>) -> Unit
) {
    var deletingEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var showSelectedDeleteDialog by remember { mutableStateOf(false) }
    var selectedEntryIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    var selectedDateMillis by remember(filteredMoodEntries) {
        mutableLongStateOf(
            filteredMoodEntries.maxByOrNull { entry -> entry.id }?.let { entry ->
                MoodDateUtils.parseDateMillis(entry.date)
            } ?: System.currentTimeMillis()
        )
    }

    var visibleMonth by remember(selectedDateMillis) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
        }

        mutableStateOf(
            MoodCalendarMonthState(
                year = calendar.get(Calendar.YEAR),
                month = calendar.get(Calendar.MONTH)
            )
        )
    }

    val selectedDate = MoodDateUtils.formatIsoDate(
        startOfMoodCalendarDayMillis(selectedDateMillis)
    )

    val selectedDayLogs = filteredMoodEntries
        .filter { entry -> entry.date == selectedDate }
        .sortedByDescending { entry -> entry.id }

    val visibleSelectedEntries = selectedDayLogs.filter { entry ->
        entry.id in selectedEntryIds
    }

    LaunchedEffect(selectedDayLogs) {
        val visibleIds = selectedDayLogs.map { entry -> entry.id }.toSet()
        selectedEntryIds = selectedEntryIds.intersect(visibleIds)
    }

    val todayCount = moodEntries.count { entry ->
        entry.date == MoodDateUtils.getTodayDate()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MoodSectionHeader(
            title = "Mood History",
            subtitle = "Tap a day to see saved logs. Long press a log to select it."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Total Logs",
                value = moodEntries.size.toString(),
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Mood Avg",
                value = MoodLabelUtils.formatMoodAverage(filteredAverageMood),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Today",
                value = todayCount.toString(),
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Selected Day",
                value = selectedDayLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        MoodHistoryFilterRow(
            filterState = filterState,
            onFeelingFilterSelected = { filter ->
                selectedEntryIds = emptySet()
                onFeelingFilterSelected(filter)
            },
            onClearFilters = {
                selectedEntryIds = emptySet()
                onClearFilters()
            }
        )

        MoodProgressCalendar(
            monthState = visibleMonth,
            moodEntries = filteredMoodEntries,
            selectedDateMillis = selectedDateMillis,
            onPreviousMonth = {
                val previousMonth = visibleMonth.offset(monthOffset = -1)
                visibleMonth = previousMonth
                selectedDateMillis = previousMonth.firstDayMillis()
                selectedEntryIds = emptySet()
            },
            onNextMonth = {
                val nextMonth = visibleMonth.offset(monthOffset = 1)
                visibleMonth = nextMonth
                selectedDateMillis = nextMonth.firstDayMillis()
                selectedEntryIds = emptySet()
            },
            onDaySelected = { dayMillis ->
                selectedDateMillis = dayMillis
                selectedEntryIds = emptySet()
            }
        )

        MoodSectionHeader(
            title = MoodDateUtils.formatDisplayDate(selectedDate),
            subtitle = selectedDaySummary(
                selectedDayLogs = selectedDayLogs,
                filterState = filterState
            )
        )

        if (selectedEntryIds.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        selectedEntryIds = emptySet()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        showSelectedDeleteDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete ${selectedEntryIds.size}")
                }
            }
        }

        if (moodEntries.isEmpty()) {
            Text("Your mood history will appear here.")
        } else if (selectedDayLogs.isEmpty()) {
            Text("No mood logs saved for this day.")
        } else {
            selectedDayLogs.forEach { entry ->
                val isSelected = entry.id in selectedEntryIds
                val selectionMode = selectedEntryIds.isNotEmpty()

                MoodHistoryCard(
                    entry = entry,
                    scalePreset = scalePreset,
                    isSelected = isSelected,
                    selectionMode = selectionMode,
                    onClick = {
                        if (selectionMode) {
                            selectedEntryIds = toggleSelectedEntry(
                                selectedEntryIds = selectedEntryIds,
                                entryId = entry.id
                            )
                        }
                    },
                    onLongPress = {
                        selectedEntryIds = selectedEntryIds + entry.id
                    },
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
        DeleteMoodDialog(
            entry = entry,
            scalePreset = scalePreset,
            onDismiss = {
                deletingEntry = null
            },
            onConfirmDelete = {
                onDeleteEntry(entry)
                deletingEntry = null
            }
        )
    }

    if (showSelectedDeleteDialog) {
        DeleteMoodLogsDialog(
            selectedCount = visibleSelectedEntries.size,
            onDismiss = {
                showSelectedDeleteDialog = false
            },
            onConfirmDelete = {
                onDeleteEntries(visibleSelectedEntries)
                selectedEntryIds = emptySet()
                showSelectedDeleteDialog = false
            }
        )
    }
}

private fun selectedDaySummary(
    selectedDayLogs: List<MoodEntry>,
    filterState: MoodLogFilterState
): String {
    if (selectedDayLogs.isEmpty()) {
        return "No mood saved for this day."
    }

    val averageMood = MoodStatsCalculator.getAverageMood(selectedDayLogs)
    val filterText = if (filterState.feelingFilter == MoodFeelingFilter.All) {
        "all moods"
    } else {
        filterState.feelingFilter.label.lowercase()
    }

    return "${selectedDayLogs.size} $filterText logs - average ${MoodLabelUtils.formatMoodAverage(averageMood)}"
}

private fun toggleSelectedEntry(
    selectedEntryIds: Set<Int>,
    entryId: Int
): Set<Int> {
    return if (entryId in selectedEntryIds) {
        selectedEntryIds - entryId
    } else {
        selectedEntryIds + entryId
    }
}
