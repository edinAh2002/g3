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
internal fun SleepGoalsDialog(
    todayGoalMinutes: Int,
    weekdaySettings: List<WeekdaySleepSettings>,
    onDismiss: () -> Unit,
    onEditGoal: (WeekdaySleepSettings) -> Unit,
    onEditAllGoals: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Goals")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Today: ${SleepCalculator.formatDuration(todayGoalMinutes)}")
                Text(
                    text = "Each sleep log uses the goal for its wake day.",
                    style = MaterialTheme.typography.bodySmall
                )

                weekdaySettings.forEach { setting ->
                    SettingsEditRow(
                        title = setting.weekday.label,
                        value = SleepCalculator.formatDuration(setting.goalMinutes),
                        onEdit = {
                            onEditGoal(setting)
                        }
                    )
                }

                OutlinedButton(
                    onClick = onEditAllGoals,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Same Goal For All Days")
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
