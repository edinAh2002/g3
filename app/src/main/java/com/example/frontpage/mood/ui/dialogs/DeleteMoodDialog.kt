package com.example.frontpage.mood.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset

@Composable
fun DeleteMoodDialog(
    entry: MoodEntry,
    scalePreset: MoodScalePreset,
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
                Text("${MoodDateUtils.formatDisplayDate(entry.date)} at ${entry.time}")
                Text("Mood: ${MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)}")
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

@Composable
fun DeleteMoodLogsDialog(
    selectedCount: Int,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Mood Logs?")
        },
        text = {
            Text("Delete $selectedCount selected mood logs? This cannot be undone.")
        },
        confirmButton = {
            TextButton(
                enabled = selectedCount > 0,
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
