package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.HealthConnectAvailability
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

private enum class SleepSettingsDialog {
    Goals,
    ScheduleTargets,
    CustomTags,
    SleepHistory,
    HealthConnect
}

private enum class ScheduleTargetPicker {
    Bedtime,
    Wake
}

@Composable
fun SleepSettingsPage(
    goalMinutes: Int,
    weekdaySettings: List<WeekdaySleepSettings>,
    customTags: List<SleepCustomTag>,
    totalLogs: Int,
    healthConnectState: SleepHealthConnectState,
    onUpdateAllWeekdayGoals: (Int) -> Unit,
    onClearSleepHistoryClick: () -> Unit,
    onUpdateWeekdayGoal: (SleepWeekday, Int) -> Unit,
    onUpdateWeekdayScheduleTargets: (SleepWeekday, Int, Int) -> Unit,
    onUpdateAllWeekdayScheduleTargets: (Int, Int) -> Unit,
    onAddCustomTag: (String) -> Unit,
    onDeleteCustomTag: (String) -> Unit,
    onRequestHealthConnectAccessClick: () -> Unit,
    onImportHealthConnectSleepClick: () -> Unit
) {
    val settings = settingsOrDefaults(weekdaySettings)
    val todaySetting = settings.firstOrNull { setting ->
        setting.weekday == SleepWeekday.fromDateMillis(System.currentTimeMillis())
    } ?: settings.first()

    var activeDialog by remember { mutableStateOf<SleepSettingsDialog?>(null) }
    var editingGoal by remember { mutableStateOf<WeekdaySleepSettings?>(null) }
    var editingAllGoals by remember { mutableStateOf(false) }
    var editingTargets by remember { mutableStateOf<WeekdaySleepSettings?>(null) }
    var editingAllTargets by remember { mutableStateOf(false) }
    var deletingCustomTag by remember { mutableStateOf<SleepCustomTag?>(null) }
    var newCustomTagLabel by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Settings",
            style = MaterialTheme.typography.titleMedium
        )

        SettingsSummaryCard(
            title = "Sleep Goals",
            value = "Today: ${SleepCalculator.formatDuration(todaySetting.goalMinutes)}",
            description = "Set different sleep goals for each wake day.",
            onClick = {
                activeDialog = SleepSettingsDialog.Goals
            }
        )

        SettingsSummaryCard(
            title = "Sleep Schedule Targets",
            value = "Today: ${SleepCalculator.formatClockMinutes(todaySetting.bedtimeMinutes)} to ${SleepCalculator.formatClockMinutes(todaySetting.wakeMinutes)}",
            description = "Set bedtime and wake-up targets for each day.",
            onClick = {
                activeDialog = SleepSettingsDialog.ScheduleTargets
            }
        )

        SettingsSummaryCard(
            title = "Custom Tags",
            value = "${customTags.size} custom tags",
            description = "Create tags that appear when logging sleep.",
            onClick = {
                activeDialog = SleepSettingsDialog.CustomTags
            }
        )

        SettingsSummaryCard(
            title = "Sleep History",
            value = "$totalLogs saved logs",
            description = "Clear sleep logs for the current user.",
            onClick = {
                activeDialog = SleepSettingsDialog.SleepHistory
            }
        )

        SettingsSummaryCard(
            title = "Wearables & Health Connect",
            value = healthConnectState.availability.label,
            description = healthConnectSummary(healthConnectState),
            onClick = {
                activeDialog = SleepSettingsDialog.HealthConnect
            }
        )
    }

    when (activeDialog) {
        SleepSettingsDialog.Goals -> {
            SleepGoalsDialog(
                todayGoalMinutes = todaySetting.goalMinutes,
                weekdaySettings = settings,
                onDismiss = {
                    activeDialog = null
                },
                onEditGoal = { setting ->
                    editingGoal = setting
                },
                onEditAllGoals = {
                    editingAllGoals = true
                }
            )
        }

        SleepSettingsDialog.ScheduleTargets -> {
            SleepScheduleTargetsDialog(
                weekdaySettings = settings,
                onDismiss = {
                    activeDialog = null
                },
                onEditTargets = { setting ->
                    editingTargets = setting
                },
                onEditAllTargets = {
                    editingAllTargets = true
                }
            )
        }

        SleepSettingsDialog.CustomTags -> {
            CustomTagsDialog(
                customTags = customTags,
                newCustomTagLabel = newCustomTagLabel,
                onNewCustomTagLabelChange = { label ->
                    newCustomTagLabel = label
                },
                onAddCustomTag = {
                    onAddCustomTag(newCustomTagLabel)
                    newCustomTagLabel = ""
                },
                onDeleteCustomTag = { tag ->
                    deletingCustomTag = tag
                },
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        SleepSettingsDialog.SleepHistory -> {
            SleepHistorySettingsDialog(
                totalLogs = totalLogs,
                onClearSleepHistoryClick = onClearSleepHistoryClick,
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        SleepSettingsDialog.HealthConnect -> {
            HealthConnectSettingsDialog(
                healthConnectState = healthConnectState,
                onRequestHealthConnectAccessClick = onRequestHealthConnectAccessClick,
                onImportHealthConnectSleepClick = onImportHealthConnectSleepClick,
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        null -> Unit
    }

    editingGoal?.let { setting ->
        EditGoalDialog(
            title = "${setting.weekday.label} Goal",
            initialGoalMinutes = setting.goalMinutes,
            onDismiss = {
                editingGoal = null
            },
            onSave = { goalMinutes ->
                onUpdateWeekdayGoal(setting.weekday, goalMinutes)
                editingGoal = null
            }
        )
    }

    if (editingAllGoals) {
        EditGoalDialog(
            title = "All Days Goal",
            initialGoalMinutes = todaySetting.goalMinutes,
            onDismiss = {
                editingAllGoals = false
            },
            onSave = { goalMinutes ->
                onUpdateAllWeekdayGoals(goalMinutes)
                editingAllGoals = false
            }
        )
    }

    editingTargets?.let { setting ->
        EditScheduleTargetsDialog(
            title = "${setting.weekday.label} Targets",
            initialBedtimeMinutes = setting.bedtimeMinutes,
            initialWakeMinutes = setting.wakeMinutes,
            onDismiss = {
                editingTargets = null
            },
            onSave = { bedtimeMinutes, wakeMinutes ->
                onUpdateWeekdayScheduleTargets(
                    setting.weekday,
                    bedtimeMinutes,
                    wakeMinutes
                )
                editingTargets = null
            }
        )
    }

    if (editingAllTargets) {
        EditScheduleTargetsDialog(
            title = "All Days Targets",
            initialBedtimeMinutes = todaySetting.bedtimeMinutes,
            initialWakeMinutes = todaySetting.wakeMinutes,
            onDismiss = {
                editingAllTargets = false
            },
            onSave = { bedtimeMinutes, wakeMinutes ->
                onUpdateAllWeekdayScheduleTargets(
                    bedtimeMinutes,
                    wakeMinutes
                )
                editingAllTargets = false
            }
        )
    }

    deletingCustomTag?.let { tag ->
        AlertDialog(
            onDismissRequest = {
                deletingCustomTag = null
            },
            title = {
                Text("Delete custom tag?")
            },
            text = {
                Text("The tag will disappear from future selection. Old logs keep showing \"${tag.label}\".")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCustomTag(tag.id)
                        deletingCustomTag = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deletingCustomTag = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSummaryCard(
    title: String,
    value: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 116.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SleepGoalsDialog(
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

@Composable
private fun SleepScheduleTargetsDialog(
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

@Composable
private fun CustomTagsDialog(
    customTags: List<SleepCustomTag>,
    newCustomTagLabel: String,
    onNewCustomTagLabelChange: (String) -> Unit,
    onAddCustomTag: () -> Unit,
    onDeleteCustomTag: (SleepCustomTag) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Custom Tags")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Custom tags appear under Custom when logging sleep.",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCustomTagLabel,
                        onValueChange = onNewCustomTagLabelChange,
                        label = { Text("New tag") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedButton(
                        onClick = onAddCustomTag,
                        enabled = newCustomTagLabel.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                if (customTags.isEmpty()) {
                    Text("No custom tags yet.")
                } else {
                    customTags.forEach { tag ->
                        SettingsEditRow(
                            title = tag.label,
                            value = "Custom",
                            editText = "Delete",
                            onEdit = {
                                onDeleteCustomTag(tag)
                            }
                        )
                    }
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

@Composable
private fun SleepHistorySettingsDialog(
    totalLogs: Int,
    onClearSleepHistoryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep History")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("$totalLogs saved logs")
                Text(
                    text = "Clearing history removes sleep logs for the current user only. Your sleep settings stay saved.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = {
                        onClearSleepHistoryClick()
                        onDismiss()
                    },
                    enabled = totalLogs > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Sleep History")
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

@Composable
private fun HealthConnectSettingsDialog(
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

@Composable
private fun SettingsEditRow(
    title: String,
    value: String,
    editText: String = "Edit",
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(title)
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedButton(onClick = onEdit) {
            Text(editText)
        }
    }
}

@Composable
private fun EditGoalDialog(
    title: String,
    initialGoalMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var selectedGoalHours by remember(title, initialGoalMinutes) {
        mutableStateOf((initialGoalMinutes / 60).coerceIn(4, 12))
    }

    var expanded by remember { mutableStateOf(false) }
    val selectedGoalMinutes = selectedGoalHours * 60

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Choose your target sleep duration.")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
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
                            onClick = {
                                expanded = true
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$selectedGoalHours hours")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditScheduleTargetsDialog(
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

private fun settingsOrDefaults(
    weekdaySettings: List<WeekdaySleepSettings>
): List<WeekdaySleepSettings> {
    if (weekdaySettings.isNotEmpty()) return weekdaySettings

    return SleepWeekday.entries.map { weekday ->
        WeekdaySleepSettings(
            weekday = weekday,
            goalMinutes = 8 * 60,
            bedtimeMinutes = 23 * 60,
            wakeMinutes = 7 * 60
        )
    }
}

private fun healthConnectSummary(
    healthConnectState: SleepHealthConnectState
): String {
    return when {
        healthConnectState.availability == HealthConnectAvailability.Unavailable ->
            "Health Connect is not available on this device."

        healthConnectState.availability == HealthConnectAvailability.ProviderUpdateRequired ->
            "Install or update Health Connect, then come back to import wearable sleep."

        healthConnectState.hasSleepPermission ->
            "Sleep read access is granted."

        else ->
            "Grant sleep read access to import wearable sessions."
    }
}

private fun parseClockMinutes(value: String): Int? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null

    return hour * 60 + minute
}
