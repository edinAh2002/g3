package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.WeekdaySleepSettings
import com.example.frontpage.sleep.ui.components.SettingsEditRow

@Composable
internal fun SleepScheduleTargetsDialog(
    weekdaySettings: List<WeekdaySleepSettings>,
    onDismiss: () -> Unit,
    onEditTargets: (WeekdaySleepSettings) -> Unit,
    onEditAllTargets: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Schedule Targets")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Targets set default log times and help frame your routine.",
                    style = MaterialTheme.typography.bodySmall
                )

                weekdaySettings.forEach { setting ->
                    SettingsEditRow(
                        title = setting.weekday.label,
                        value = "${SleepCalculator.formatClockMinutes(setting.bedtimeMinutes)} to ${SleepCalculator.formatClockMinutes(setting.wakeMinutes)}",
                        onEdit = {
                            onEditTargets(setting)
                        }
                    )
                }

                OutlinedButton(
                    onClick = onEditAllTargets,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Same Schedule For All Days")
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
