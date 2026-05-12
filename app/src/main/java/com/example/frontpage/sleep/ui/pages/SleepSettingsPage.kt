package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepThemePresetId
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings
import com.example.frontpage.sleep.ui.components.SettingsSummaryCard
import com.example.frontpage.sleep.ui.components.healthConnectSummary
import com.example.frontpage.sleep.ui.components.settingsOrDefaults
import com.example.frontpage.sleep.ui.dialogs.CustomTagsDialog
import com.example.frontpage.sleep.ui.dialogs.DeleteCustomTagDialog
import com.example.frontpage.sleep.ui.dialogs.EditGoalDialog
import com.example.frontpage.sleep.ui.dialogs.EditScheduleTargetsDialog
import com.example.frontpage.sleep.ui.dialogs.HealthConnectSettingsDialog
import com.example.frontpage.sleep.ui.dialogs.SleepGoalsDialog
import com.example.frontpage.sleep.ui.dialogs.SleepHistorySettingsDialog
import com.example.frontpage.sleep.ui.dialogs.SleepScheduleTargetsDialog
import com.example.frontpage.sleep.ui.dialogs.SleepThemeDialog
import com.example.frontpage.sleep.ui.theme.SleepThemePresetCatalog

private enum class SleepSettingsDialog {
    Goals,
    ScheduleTargets,
    CustomTags,
    SleepHistory,
    Theme,
    HealthConnect
}

@Composable
fun SleepSettingsPage(
    goalMinutes: Int,
    weekdaySettings: List<WeekdaySleepSettings>,
    customTags: List<SleepCustomTag>,
    totalLogs: Int,
    healthConnectState: SleepHealthConnectState,
    selectedThemePresetId: SleepThemePresetId,
    onUpdateAllWeekdayGoals: (Int) -> Unit,
    onClearSleepHistoryClick: () -> Unit,
    onUpdateWeekdayGoal: (SleepWeekday, Int) -> Unit,
    onUpdateWeekdayScheduleTargets: (SleepWeekday, Int, Int) -> Unit,
    onUpdateAllWeekdayScheduleTargets: (Int, Int) -> Unit,
    onAddCustomTag: (String) -> Unit,
    onDeleteCustomTag: (String) -> Unit,
    onUpdateThemePreset: (SleepThemePresetId) -> Unit,
    onRequestHealthConnectAccessClick: () -> Unit,
    onImportHealthConnectSleepClick: () -> Unit
) {
    val settings = settingsOrDefaults(weekdaySettings)
    val todaySetting = settings.firstOrNull { setting ->
        setting.weekday == SleepWeekday.fromDateMillis(System.currentTimeMillis())
    } ?: settings.first()
    val selectedThemeDescriptor = SleepThemePresetCatalog.descriptorFor(selectedThemePresetId)

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
            title = "Sleep Theme",
            value = selectedThemeDescriptor.displayName,
            description = selectedThemeDescriptor.description,
            onClick = {
                activeDialog = SleepSettingsDialog.Theme
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

        SleepSettingsDialog.Theme -> {
            SleepThemeDialog(
                selectedThemePresetId = selectedThemePresetId,
                onSelectThemePreset = onUpdateThemePreset,
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
        DeleteCustomTagDialog(
            tag = tag,
            onDismiss = {
                deletingCustomTag = null
            },
            onConfirmDelete = {
                onDeleteCustomTag(tag.id)
                deletingCustomTag = null
            }
        )
    }
}
