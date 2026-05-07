package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.ui.components.SleepHistoryCard
import com.example.frontpage.sleep.ui.components.SleepHistoryFilterRow
import com.example.frontpage.sleep.ui.components.SleepMetricTile
import com.example.frontpage.sleep.ui.components.SleepSectionHeader
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@Composable
fun SleepHistoryPage(
    sleepLogs: List<SleepEntry>,
    filteredSleepLogs: List<SleepEntry>,
    selectedHistoryFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit,
    onEditEntry: (SleepEntry) -> Unit,
    onDeleteEntry: (SleepEntry) -> Unit
) {
    var deletingEntry by remember { mutableStateOf<SleepEntry?>(null) }
    val averageSleepMinutes = if (sleepLogs.isEmpty()) {
        0
    } else {
        sleepLogs.map { entry -> entry.durationMinutes }.average().toInt()
    }

    val thisWeekCount = sleepLogs.count { entry ->
        SleepDateUtils.isThisWeek(entry.dateMillis)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SleepSectionHeader(
            title = "Sleep History",
            subtitle = "Browse, edit, and clean up saved sleep logs."
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
                title = "Showing",
                value = filteredSleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        SleepSectionHeader(
            title = "Filter",
            subtitle = "Narrow the list without changing saved logs."
        )

        SleepHistoryFilterRow(
            selectedFilter = selectedHistoryFilter,
            onFilterSelected = onFilterSelected
        )

        if (sleepLogs.isEmpty()) {
            Text("Your sleep history will appear here.")
        } else if (filteredSleepLogs.isEmpty()) {
            Text("No sleep logs found for this filter.")
        } else {
            filteredSleepLogs.reversed().forEach { entry ->
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
