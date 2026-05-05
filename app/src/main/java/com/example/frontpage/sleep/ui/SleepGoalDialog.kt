package com.example.frontpage.sleep.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator

@Composable
fun SleepGoalDialog(
    currentGoalMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedGoalHours by remember(currentGoalMinutes) {
        mutableStateOf((currentGoalMinutes / 60).coerceIn(4, 12))
    }

    var expanded by remember { mutableStateOf(false) }

    val selectedGoalMinutes = selectedGoalHours * 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Sleep Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Choose your target sleep duration.")

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Selected goal",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = SleepCalculator.formatDuration(selectedGoalMinutes),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$selectedGoalHours hours")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            for (hour in 4..12) {
                                DropdownMenuItem(
                                    text = {
                                        Text("$hour hours")
                                    },
                                    onClick = {
                                        selectedGoalHours = hour
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Sleep goal must be between 4 and 12 hours.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
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