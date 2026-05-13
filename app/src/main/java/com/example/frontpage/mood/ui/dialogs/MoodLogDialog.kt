package com.example.frontpage.mood.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodLogDraft
import com.example.frontpage.mood.model.MoodScalePreset

@Composable
fun MoodLogDialog(
    existingEntry: MoodEntry? = null,
    defaultScalePreset: MoodScalePreset,
    onDismiss: () -> Unit,
    onSave: (MoodLogDraft) -> Unit
) {
    var selectedMood by remember(existingEntry?.id) {
        mutableIntStateOf(existingEntry?.moodValue ?: 0)
    }

    var note by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.note ?: "")
    }

    var selectedDateMillis by remember(existingEntry?.id) {
        mutableStateOf(
            existingEntry?.let { entry ->
                MoodDateUtils.parseDateMillis(entry.date)
            } ?: System.currentTimeMillis()
        )
    }

    var selectedPreset by remember(existingEntry?.id, defaultScalePreset) {
        mutableStateOf(defaultScalePreset)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showScalePicker by remember { mutableStateOf(false) }

    val isMoodValid = selectedMood in 1..5
    val selectedDate = MoodDateUtils.formatIsoDate(selectedDateMillis)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingEntry == null) "Log Mood" else "Edit Mood")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Choose the mood that best matches this moment.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = {
                        showDatePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Date: ${MoodDateUtils.formatDisplayDate(selectedDate)}")
                }

                OutlinedButton(
                    onClick = {
                        showScalePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scale: ${selectedPreset.label}")
                }

                selectedPreset.options.forEach { option ->
                    MoodOptionButton(
                        label = "${option.label} - ${option.value}/5",
                        description = option.description,
                        selected = selectedMood == option.value,
                        onClick = {
                            selectedMood = option.value
                        }
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { value -> note = value },
                    label = { Text("Note") },
                    placeholder = {
                        Text("Optional: what influenced this mood?")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (!isMoodValid) {
                    Text(
                        text = "Select a mood before saving.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isMoodValid,
                onClick = {
                    onSave(
                        MoodLogDraft(
                            moodValue = selectedMood,
                            note = note,
                            date = selectedDate
                        )
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

    if (showDatePicker) {
        MoodDatePickerDialog(
            initialDateMillis = selectedDateMillis,
            onDismiss = {
                showDatePicker = false
            },
            onConfirm = { dateMillis ->
                selectedDateMillis = dateMillis
                showDatePicker = false
            }
        )
    }

    if (showScalePicker) {
        MoodScalePresetDialog(
            selectedPreset = selectedPreset,
            onPresetSelected = { preset ->
                selectedPreset = preset
                showScalePicker = false
            },
            onDismiss = {
                showScalePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoodDatePickerDialog(
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        datePickerState.selectedDateMillis ?: initialDateMillis
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
    ) {
        DatePicker(state = datePickerState)
    }
}

