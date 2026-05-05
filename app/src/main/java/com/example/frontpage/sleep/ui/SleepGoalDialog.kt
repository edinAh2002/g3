package com.example.frontpage.sleep.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepGoalDialog(
    currentGoalMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    val currentHour = currentGoalMinutes / 60
    val currentMinute = currentGoalMinutes % 60

    val timePickerState = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = true
    )

    val selectedGoalMinutes = timePickerState.hour * 60 + timePickerState.minute
    val isGoalValid = selectedGoalMinutes in (4 * 60)..(12 * 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Sleep Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Choose your target sleep duration.")

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Selected goal: ${SleepCalculator.formatDuration(selectedGoalMinutes)}",
                    style = MaterialTheme.typography.titleMedium
                )

                if (!isGoalValid) {
                    Text(
                        text = "Sleep goal must be between 4h and 12h.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "This goal will be used for your progress bar and sleep feedback.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isGoalValid,
                onClick = {
                    onSave(selectedGoalMinutes)
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