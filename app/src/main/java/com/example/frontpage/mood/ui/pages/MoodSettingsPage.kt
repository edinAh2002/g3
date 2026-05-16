package com.example.frontpage.mood.ui.pages

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
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.ui.components.MoodSettingsSummaryCard
import com.example.frontpage.mood.ui.dialogs.MoodHistorySettingsDialog
import com.example.frontpage.mood.ui.dialogs.MoodInfoDialog
import com.example.frontpage.mood.ui.dialogs.MoodScaleDialog

private enum class MoodSettingsDialog {
    Scale,
    Logging,
    MoodHistory,
    Insights
}

@Composable
fun MoodSettingsPage(
    totalLogs: Int,
    defaultScalePreset: MoodScalePreset,
    onDefaultScalePresetSelected: (MoodScalePreset) -> Unit,
    onClearMoodHistoryClick: () -> Unit
) {
    var activeDialog by remember { mutableStateOf<MoodSettingsDialog?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood Settings",
            style = MaterialTheme.typography.titleMedium
        )

        MoodSettingsSummaryCard(
            title = "Mood Scale",
            value = defaultScalePreset.label,
            description = "Choose the default labels shown when logging mood.",
            onClick = {
                activeDialog = MoodSettingsDialog.Scale
            }
        )

        MoodSettingsSummaryCard(
            title = "Mood Logging",
            value = "Mood plus note",
            description = "Mood logs store a score, date, time, and optional note.",
            onClick = {
                activeDialog = MoodSettingsDialog.Logging
            }
        )

        MoodSettingsSummaryCard(
            title = "Mood History",
            value = "$totalLogs saved logs",
            description = "Clear mood logs for the current user.",
            onClick = {
                activeDialog = MoodSettingsDialog.MoodHistory
            }
        )

        MoodSettingsSummaryCard(
            title = "Mood Insights",
            value = "Score and trends",
            description = "Insights compare recent averages, streaks, and note coverage.",
            onClick = {
                activeDialog = MoodSettingsDialog.Insights
            }
        )
    }

    when (activeDialog) {
        MoodSettingsDialog.Scale -> {
            MoodScaleDialog(
                selectedPreset = defaultScalePreset,
                onPresetSelected = onDefaultScalePresetSelected,
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        MoodSettingsDialog.Logging -> {
            MoodInfoDialog(
                title = "Mood Logging",
                lines = listOf(
                    "Each log keeps the selected mood, date, time, and note.",
                    "The log popup defaults to today's date and your default scale."
                ),
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        MoodSettingsDialog.MoodHistory -> {
            MoodHistorySettingsDialog(
                totalLogs = totalLogs,
                onClearMoodHistoryClick = onClearMoodHistoryClick,
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        MoodSettingsDialog.Insights -> {
            MoodInfoDialog(
                title = "Mood Insights",
                lines = listOf(
                    "Mood score blends latest mood, recent average, and positive-log ratio.",
                    "Momentum compares the last 7 days with the previous 7 days."
                ),
                onDismiss = {
                    activeDialog = null
                }
            )
        }

        null -> Unit
    }
}
