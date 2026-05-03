package com.example.frontpage.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class TimePickerTarget {
    SleepTime,
    WakeTime
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepLogDialog(
    existingEntry: SleepEntry? = null,
    goalMinutes: Int = SleepSettingsRepository.sleepGoalMinutes,
    onDismiss: () -> Unit,
    onSave: (
        sleepHour: Int,
        sleepMinute: Int,
        wakeHour: Int,
        wakeMinute: Int,
        quality: SleepQuality,
        durationMinutes: Int,
        notes: String
    ) -> Unit
) {
    var sleepHour by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.sleepHour ?: 23)
    }

    var sleepMinute by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.sleepMinute ?: 0)
    }

    var wakeHour by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.wakeHour ?: 7)
    }

    var wakeMinute by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.wakeMinute ?: 0)
    }

    var selectedQuality by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.quality ?: SleepQuality.Good)
    }

    var notes by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.notes ?: "")
    }

    var activeTimePicker by remember { mutableStateOf<TimePickerTarget?>(null) }

    val durationMinutes = SleepCalculator.calculateDurationMinutes(
        sleepHour = sleepHour,
        sleepMinute = sleepMinute,
        wakeHour = wakeHour,
        wakeMinute = wakeMinute
    )

    val durationText = SleepCalculator.formatDuration(durationMinutes)
    val statusTitle = SleepCalculator.getGoalStatusTitle(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    val feedbackText = SleepCalculator.getGoalDifferenceText(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    val suggestionText = SleepCalculator.getImprovementSuggestion(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingEntry == null) "Log Sleep" else "Edit Sleep")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { activeTimePicker = TimePickerTarget.SleepTime },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sleep\n${SleepCalculator.formatTime(sleepHour, sleepMinute)}")
                    }

                    OutlinedButton(
                        onClick = { activeTimePicker = TimePickerTarget.WakeTime },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Wake\n${SleepCalculator.formatTime(wakeHour, wakeMinute)}")
                    }
                }

                Text(
                    text = "Sleep Quality",
                    style = MaterialTheme.typography.titleSmall
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QualityButton(
                            text = "😴 Poor",
                            selected = selectedQuality == SleepQuality.Poor,
                            onClick = { selectedQuality = SleepQuality.Poor },
                            modifier = Modifier.weight(1f)
                        )

                        QualityButton(
                            text = "🙂 Okay",
                            selected = selectedQuality == SleepQuality.Okay,
                            onClick = { selectedQuality = SleepQuality.Okay },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QualityButton(
                            text = "😊 Good",
                            selected = selectedQuality == SleepQuality.Good,
                            onClick = { selectedQuality = SleepQuality.Good },
                            modifier = Modifier.weight(1f)
                        )

                        QualityButton(
                            text = "🌟 Great",
                            selected = selectedQuality == SleepQuality.Great,
                            onClick = { selectedQuality = SleepQuality.Great },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("Example: Woke up twice, felt rested, had caffeine late...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Sleep Duration")

                        Text(
                            text = durationText,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text("Goal: ${SleepCalculator.formatDuration(goalMinutes)}")

                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = feedbackText,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = suggestionText,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = durationMinutes > 0,
                onClick = {
                    onSave(
                        sleepHour,
                        sleepMinute,
                        wakeHour,
                        wakeMinute,
                        selectedQuality,
                        durationMinutes,
                        notes.trim()
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    activeTimePicker?.let { pickerTarget ->
        ClockPickerDialog(
            title = if (pickerTarget == TimePickerTarget.SleepTime) {
                "Choose Sleep Time"
            } else {
                "Choose Wake Time"
            },
            initialHour = if (pickerTarget == TimePickerTarget.SleepTime) sleepHour else wakeHour,
            initialMinute = if (pickerTarget == TimePickerTarget.SleepTime) sleepMinute else wakeMinute,
            onDismiss = {
                activeTimePicker = null
            },
            onConfirm = { hour, minute ->
                if (pickerTarget == TimePickerTarget.SleepTime) {
                    sleepHour = hour
                    sleepMinute = minute
                } else {
                    wakeHour = hour
                    wakeMinute = minute
                }

                activeTimePicker = null
            }
        )
    }
}

@Composable
private fun QualityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockPickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}