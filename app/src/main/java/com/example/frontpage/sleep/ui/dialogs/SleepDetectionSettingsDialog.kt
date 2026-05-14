package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepDetectionSettings

@Composable
internal fun SleepDetectionSettingsDialog(
    settings: SleepDetectionSettings,
    onDismiss: () -> Unit,
    onSave: (SleepDetectionSettings) -> Unit
) {
    var enabled by remember(settings) { mutableStateOf(settings.enabled) }
    var minimumSleepHours by remember(settings) {
        mutableStateOf((settings.minimumSleepMinutes / 60f).toString())
    }
    var alarmWindowMinutes by remember(settings) {
        mutableStateOf(settings.alarmMatchWindowMinutes.toString())
    }
    var interruptionMinutes by remember(settings) {
        mutableStateOf(settings.interruptionToleranceMinutes.toString())
    }

    val parsedMinimumMinutes = minimumSleepHours.toFloatOrNull()
        ?.let { hours -> (hours * 60f).toInt() }
    val parsedAlarmWindowMinutes = alarmWindowMinutes.toIntOrNull()
    val parsedInterruptionMinutes = interruptionMinutes.toIntOrNull()
    val canSave = parsedMinimumMinutes != null &&
        parsedAlarmWindowMinutes != null &&
        parsedInterruptionMinutes != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Detection")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Detect sleep suggestions",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Uses screen lock and alarm signals while enabled.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Switch(
                        checked = enabled,
                        onCheckedChange = { checked ->
                            enabled = checked
                        }
                    )
                }

                OutlinedTextField(
                    value = minimumSleepHours,
                    onValueChange = { value ->
                        minimumSleepHours = value
                    },
                    label = {
                        Text("Minimum sleep hours")
                    },
                    singleLine = true,
                    isError = parsedMinimumMinutes == null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alarmWindowMinutes,
                    onValueChange = { value ->
                        alarmWindowMinutes = value
                    },
                    label = {
                        Text("Alarm match window minutes")
                    },
                    singleLine = true,
                    isError = parsedAlarmWindowMinutes == null,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = interruptionMinutes,
                    onValueChange = { value ->
                        interruptionMinutes = value
                    },
                    label = {
                        Text("Brief screen-use tolerance minutes")
                    },
                    singleLine = true,
                    isError = parsedInterruptionMinutes == null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(
                        SleepDetectionSettings(
                            enabled = enabled,
                            minimumSleepMinutes = parsedMinimumMinutes
                                ?: settings.minimumSleepMinutes,
                            alarmMatchWindowMinutes = parsedAlarmWindowMinutes
                                ?: settings.alarmMatchWindowMinutes,
                            interruptionToleranceMinutes = parsedInterruptionMinutes
                                ?: settings.interruptionToleranceMinutes
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
}
