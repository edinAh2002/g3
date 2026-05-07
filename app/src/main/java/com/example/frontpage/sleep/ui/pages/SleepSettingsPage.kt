package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import com.example.frontpage.sleep.model.HealthConnectAvailability
import com.example.frontpage.sleep.model.SleepHealthConnectState

@Composable
fun SleepSettingsPage(
    goalMinutes: Int,
    totalLogs: Int,
    healthConnectState: SleepHealthConnectState,
    onEditGoalClick: () -> Unit,
    onClearSleepHistoryClick: () -> Unit,
    onRequestHealthConnectAccessClick: () -> Unit,
    onImportHealthConnectSleepClick: () -> Unit
) {
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sleep Goal",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = SleepCalculator.formatDuration(goalMinutes),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "This goal is used for your progress bar, score, and feedback cards.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = onEditGoalClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Sleep Goal")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sleep History",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$totalLogs saved logs",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Clearing history removes sleep logs for the current user only.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = {
                        showClearConfirmDialog = true
                    },
                    enabled = totalLogs > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Sleep History")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Wearables & Health Connect",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = healthConnectState.availability.label,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = when {
                        healthConnectState.availability == HealthConnectAvailability.Unavailable ->
                            "Health Connect is not available on this device."

                        healthConnectState.availability == HealthConnectAvailability.ProviderUpdateRequired ->
                            "Install or update Health Connect, then come back to import wearable sleep."

                        healthConnectState.hasSleepPermission ->
                            "Sleep read access is granted."

                        else ->
                            "Grant sleep read access to import wearable sessions."
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = onRequestHealthConnectAccessClick,
                    enabled = healthConnectState.canRequestPermission,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (healthConnectState.hasSleepPermission) {
                            "Review Access"
                        } else {
                            "Grant Sleep Access"
                        }
                    )
                }

                OutlinedButton(
                    onClick = onImportHealthConnectSleepClick,
                    enabled = healthConnectState.canImport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (healthConnectState.isImporting) {
                            "Importing..."
                        } else {
                            "Import Last 30 Days"
                        }
                    )
                }

                healthConnectState.lastImportMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showClearConfirmDialog = false
            },
            title = {
                Text("Clear sleep history?")
            },
            text = {
                Text("This will delete all sleep logs for the current user. Your sleep goal will stay saved.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearSleepHistoryClick()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
