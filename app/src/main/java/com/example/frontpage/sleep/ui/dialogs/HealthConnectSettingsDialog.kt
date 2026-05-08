package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.ui.components.healthConnectSummary

@Composable
internal fun HealthConnectSettingsDialog(
    healthConnectState: SleepHealthConnectState,
    onRequestHealthConnectAccessClick: () -> Unit,
    onImportHealthConnectSleepClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Wearables & Health Connect")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = healthConnectState.availability.label,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = healthConnectSummary(healthConnectState),
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
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
