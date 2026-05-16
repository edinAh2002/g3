package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.frontpage.sleep.domain.SleepCalculator

private enum class ScheduleTargetPicker {
    Bedtime,
    Wake
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditScheduleTargetsDialog(
    title: String,
    initialBedtimeMinutes: Int,
    initialWakeMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var selectedPicker by remember(title) {
        mutableStateOf(ScheduleTargetPicker.Bedtime)
    }

    val bedtimeState = rememberTimePickerState(
        initialHour = initialBedtimeMinutes / 60,
        initialMinute = initialBedtimeMinutes % 60,
        is24Hour = true
    )

    val wakeState = rememberTimePickerState(
        initialHour = initialWakeMinutes / 60,
        initialMinute = initialWakeMinutes % 60,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Choose your sleep start and wake-up target.")

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedPicker == ScheduleTargetPicker.Bedtime) {
                        OutlinedButton(
                            onClick = {
                                selectedPicker = ScheduleTargetPicker.Bedtime
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sleep ${SleepCalculator.formatClockMinutes(bedtimeState.hour * 60 + bedtimeState.minute)}")
                        }
                    } else {
                        TextButton(
                            onClick = {
                                selectedPicker = ScheduleTargetPicker.Bedtime
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sleep ${SleepCalculator.formatClockMinutes(bedtimeState.hour * 60 + bedtimeState.minute)}")
                        }
                    }

                    if (selectedPicker == ScheduleTargetPicker.Wake) {
                        OutlinedButton(
                            onClick = {
                                selectedPicker = ScheduleTargetPicker.Wake
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Wake ${SleepCalculator.formatClockMinutes(wakeState.hour * 60 + wakeState.minute)}")
                        }
                    } else {
                        TextButton(
                            onClick = {
                                selectedPicker = ScheduleTargetPicker.Wake
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Wake ${SleepCalculator.formatClockMinutes(wakeState.hour * 60 + wakeState.minute)}")
                        }
                    }
                }

                TimePicker(
                    state = if (selectedPicker == ScheduleTargetPicker.Bedtime) {
                        bedtimeState
                    } else {
                        wakeState
                    }
                )

                Text(
                    text = "Tap Sleep or Wake above, then choose the time on the clock.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        bedtimeState.hour * 60 + bedtimeState.minute,
                        wakeState.hour * 60 + wakeState.minute
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
}
