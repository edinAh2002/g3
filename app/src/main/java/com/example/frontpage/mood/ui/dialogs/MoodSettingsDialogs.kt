package com.example.frontpage.mood.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.model.MoodScalePreset

@Composable
fun MoodScaleDialog(
    selectedPreset: MoodScalePreset,
    onPresetSelected: (MoodScalePreset) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mood Scale")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Default scale: ${selectedPreset.label}")

                MoodScalePreset.entries.forEach { preset ->
                    MoodPresetButton(
                        preset = preset,
                        selectedPreset = selectedPreset,
                        onPresetSelected = onPresetSelected
                    )
                }

                Text("Preview:")

                selectedPreset.options.forEach { option ->
                    Text("${option.value}/5 - ${option.label}")
                }

                Text("Averages, scores, and insights still use the same 1 to 5 values.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
fun MoodScalePresetDialog(
    selectedPreset: MoodScalePreset,
    onPresetSelected: (MoodScalePreset) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Choose Scale")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoodScalePreset.entries.forEach { preset ->
                    MoodPresetButton(
                        preset = preset,
                        selectedPreset = selectedPreset,
                        onPresetSelected = onPresetSelected
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun MoodPresetButton(
    preset: MoodScalePreset,
    selectedPreset: MoodScalePreset,
    onPresetSelected: (MoodScalePreset) -> Unit
) {
    if (preset == selectedPreset) {
        Button(
            onClick = { onPresetSelected(preset) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${preset.label}: ${preset.description}")
        }
    } else {
        OutlinedButton(
            onClick = { onPresetSelected(preset) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("${preset.label}: ${preset.description}")
        }
    }
}

@Composable
fun MoodHistorySettingsDialog(
    totalLogs: Int,
    onClearMoodHistoryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Mood History")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("$totalLogs saved mood logs")
                Text("Clear mood logs for the current user.")
            }
        },
        confirmButton = {
            TextButton(
                enabled = totalLogs > 0,
                onClick = {
                    onClearMoodHistoryClick()
                    onDismiss()
                }
            ) {
                Text("Clear History")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MoodInfoDialog(
    title: String,
    lines: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lines.forEach { line ->
                    Text(line)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
