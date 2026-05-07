package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp

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

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep History",
            style = MaterialTheme.typography.titleMedium
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
