package com.example.frontpage.mood.ui.pages

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodDateFilter
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState
import com.example.frontpage.mood.ui.components.MoodHistoryCard
import com.example.frontpage.mood.ui.components.MoodHistoryFilterRow
import com.example.frontpage.mood.ui.components.MoodStatCard

@Composable
fun MoodHistoryPage(
    moodEntries: List<MoodEntry>,
    filteredMoodEntries: List<MoodEntry>,
    filteredAverageMood: Double?,
    filterState: MoodLogFilterState,
    onFeelingFilterSelected: (MoodFeelingFilter) -> Unit,
    onDateFilterSelected: (MoodDateFilter) -> Unit,
    onClearFilters: () -> Unit,
    onLogMoodClick: () -> Unit,
    onEditEntry: (MoodEntry, Int, String) -> Unit,
    onDeleteEntry: (MoodEntry) -> Unit
) {
    var editingEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var deletingEntry by remember { mutableStateOf<MoodEntry?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood History",
            style = MaterialTheme.typography.titleMedium
        )

        MoodHistoryFilterRow(
            filterState = filterState,
            onFeelingFilterSelected = onFeelingFilterSelected,
            onDateFilterSelected = onDateFilterSelected,
            onClearFilters = onClearFilters
        )

        MoodStatCard(
            title = "Filtered Average",
            value = filteredAverageMood?.let {
                "${String.format("%.1f", it)} / 5"
            } ?: "No data"
        )

        androidx.compose.material3.Button(
            onClick = onLogMoodClick
        ) {
            Text("Log Mood")
        }

        if (moodEntries.isEmpty()) {
            Text("Your mood history will appear here.")
        } else if (filteredMoodEntries.isEmpty()) {
            Text("No mood logs found for this filter.")
        } else {
            filteredMoodEntries.forEach { entry ->
                MoodHistoryCard(
                    entry = entry,
                    onEdit = {
                        editingEntry = entry
                    },
                    onDelete = {
                        deletingEntry = entry
                    }
                )
            }
        }
    }

    editingEntry?.let { entry ->
        EditMoodDialog(
            entry = entry,
            onDismiss = {
                editingEntry = null
            },
            onSave = { moodValue, note ->
                onEditEntry(entry, moodValue, note)
                editingEntry = null
            }
        )
    }

    deletingEntry?.let { entry ->
        DeleteMoodDialog(
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
private fun EditMoodDialog(
    entry: MoodEntry,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit
) {
    var selectedMood by remember { mutableIntStateOf(entry.moodValue) }
    var note by remember { mutableStateOf(entry.note) }

    val moods = listOf(
        1 to "😞 Very bad",
        2 to "😕 Bad",
        3 to "😐 Okay",
        4 to "🙂 Good",
        5 to "😄 Great"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Mood")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                moods.forEach { mood ->
                    androidx.compose.foundation.layout.Row {
                        RadioButton(
                            selected = selectedMood == mood.first,
                            onClick = {
                                selectedMood = mood.first
                            }
                        )

                        Text(mood.second)
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedMood, note)
                }
            ) {
                Text("Save")
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

@Composable
private fun DeleteMoodDialog(
    entry: MoodEntry,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Mood?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Are you sure you want to delete this mood log?")
                Text("${entry.date} at ${entry.time}")
                Text("Mood: ${MoodLabelUtils.getMoodLabel(entry.moodValue)}")
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