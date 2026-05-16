package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.SleepQuality
import com.example.frontpage.sleep.model.SleepTagOption
import com.example.frontpage.sleep.model.SnoringLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SleepTimePickerSection(
    selectedTimePicker: TimePickerTarget,
    onTimePickerSelected: (TimePickerTarget) -> Unit,
    sleepTimePickerState: TimePickerState,
    wakeTimePickerState: TimePickerState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TimePickerTabButton(
                text = "Sleep\n${
                    SleepCalculator.formatTime(
                        sleepTimePickerState.hour,
                        sleepTimePickerState.minute
                    )
                }",
                selected = selectedTimePicker == TimePickerTarget.SleepTime,
                onClick = {
                    onTimePickerSelected(TimePickerTarget.SleepTime)
                },
                modifier = Modifier.weight(1f)
            )

            TimePickerTabButton(
                text = "Wake\n${
                    SleepCalculator.formatTime(
                        wakeTimePickerState.hour,
                        wakeTimePickerState.minute
                    )
                }",
                selected = selectedTimePicker == TimePickerTarget.WakeTime,
                onClick = {
                    onTimePickerSelected(TimePickerTarget.WakeTime)
                },
                modifier = Modifier.weight(1f)
            )
        }

        TimePicker(
            state = if (selectedTimePicker == TimePickerTarget.SleepTime) {
                sleepTimePickerState
            } else {
                wakeTimePickerState
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Tap Sleep or Wake above, then choose the time on the clock.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun SleepLogDetailsSection(
    selectedQuality: SleepQuality,
    selectedSnoringLevel: SnoringLevel,
    selectedTags: List<SleepTagOption>,
    dreamJournal: String,
    notes: String,
    onDetailSelected: (SleepDetailDialog) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SleepDetailRow(
            title = "Sleep Quality",
            value = selectedQuality.name,
            onClick = {
                onDetailSelected(SleepDetailDialog.Quality)
            }
        )

        SleepDetailRow(
            title = "Snoring",
            value = selectedSnoringLevel.label,
            onClick = {
                onDetailSelected(SleepDetailDialog.Snoring)
            }
        )

        SleepDetailRow(
            title = "Sleep Tags",
            value = sleepTagSummary(selectedTags),
            onClick = {
                onDetailSelected(SleepDetailDialog.Tags)
            }
        )

        SleepDetailRow(
            title = "Dream Journal",
            value = sleepTextSummary(dreamJournal),
            onClick = {
                onDetailSelected(SleepDetailDialog.DreamJournal)
            }
        )

        SleepDetailRow(
            title = "Notes",
            value = sleepTextSummary(notes),
            onClick = {
                onDetailSelected(SleepDetailDialog.Notes)
            }
        )
    }
}

@Composable
internal fun SleepDurationSummaryCard(
    durationText: String,
    activeGoalMinutes: Int,
    statusTitle: String,
    feedbackText: String,
    suggestionText: String,
    durationValidationMessage: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Sleep Duration")

            Text(
                text = durationText,
                style = MaterialTheme.typography.headlineSmall
            )

            Text("Goal: ${SleepCalculator.formatDuration(activeGoalMinutes)}")

            Text(
                text = statusTitle,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = feedbackText,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = suggestionText,
                style = MaterialTheme.typography.bodySmall
            )

            if (durationValidationMessage != null) {
                Text(
                    text = durationValidationMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TimePickerTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text)
        }
    }
}
