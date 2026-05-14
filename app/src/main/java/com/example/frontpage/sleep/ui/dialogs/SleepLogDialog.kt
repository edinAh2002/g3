package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepDefaults
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepLogDraft
import com.example.frontpage.sleep.model.SleepQuality
import com.example.frontpage.sleep.model.SleepSource
import com.example.frontpage.sleep.model.SleepTag
import com.example.frontpage.sleep.model.SnoringLevel
import com.example.frontpage.sleep.model.WeekdaySleepSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepLogDialog(
    existingEntry: SleepEntry? = null,
    initialDraft: SleepLogDraft? = null,
    goalMinutes: Int,
    weekdaySettings: List<WeekdaySleepSettings> = emptyList(),
    customTags: List<SleepCustomTag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (SleepLogDraft) -> Unit
) {
    val initialWakeDateMillis = existingEntry?.dateMillis
        ?: initialDraft?.wakeDateMillis
        ?: System.currentTimeMillis()
    val initialSettings = weekdaySettings.settingsForDate(initialWakeDateMillis)

    var wakeDateMillis by remember(existingEntry?.id) {
        mutableStateOf(initialWakeDateMillis)
    }

    var selectedQuality by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.quality ?: initialDraft?.quality ?: SleepQuality.Good)
    }

    var notes by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.notes ?: initialDraft?.notes ?: "")
    }

    var dreamJournal by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.dreamJournal ?: initialDraft?.dreamJournal ?: "")
    }

    var selectedSnoringLevel by remember(existingEntry?.id) {
        mutableStateOf(
            existingEntry?.snoringLevel
                ?: initialDraft?.snoringLevel
                ?: SnoringLevel.None
        )
    }

    var selectedTags by remember(existingEntry?.id) {
        mutableStateOf(
            SleepTag.optionsFromStorage(
                existingEntry?.tags ?: initialDraft?.tags.orEmpty()
            )
        )
    }

    val source = existingEntry?.source
        ?: initialDraft?.source
        ?: SleepSource.Manual

    var selectedTimePicker by remember(existingEntry?.id) {
        mutableStateOf(TimePickerTarget.SleepTime)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var activeDetailDialog by remember { mutableStateOf<SleepDetailDialog?>(null) }

    val sleepTimePickerState = rememberTimePickerState(
        initialHour = existingEntry?.sleepHour
            ?: initialDraft?.sleepHour
            ?: ((initialSettings?.bedtimeMinutes ?: SleepDefaults.BEDTIME_MINUTES) / 60),
        initialMinute = existingEntry?.sleepMinute
            ?: initialDraft?.sleepMinute
            ?: ((initialSettings?.bedtimeMinutes ?: SleepDefaults.BEDTIME_MINUTES) % 60),
        is24Hour = true
    )

    val wakeTimePickerState = rememberTimePickerState(
        initialHour = existingEntry?.wakeHour
            ?: initialDraft?.wakeHour
            ?: ((initialSettings?.wakeMinutes ?: SleepDefaults.WAKE_MINUTES) / 60),
        initialMinute = existingEntry?.wakeMinute
            ?: initialDraft?.wakeMinute
            ?: ((initialSettings?.wakeMinutes ?: SleepDefaults.WAKE_MINUTES) % 60),
        is24Hour = true
    )

    val durationMinutes = SleepCalculator.calculateDurationMinutes(
        sleepHour = sleepTimePickerState.hour,
        sleepMinute = sleepTimePickerState.minute,
        wakeHour = wakeTimePickerState.hour,
        wakeMinute = wakeTimePickerState.minute
    )

    val activeGoalMinutes = weekdaySettings.settingsForDate(wakeDateMillis)?.goalMinutes
        ?: goalMinutes

    val durationValidationMessage = SleepCalculator.getDurationValidationMessage(durationMinutes)
    val isDurationValid = SleepCalculator.isRealisticDuration(durationMinutes)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when {
                    existingEntry != null -> "Edit Sleep"
                    initialDraft != null -> "Review Detected Sleep"
                    else -> "Log Sleep"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SleepTimePickerSection(
                    selectedTimePicker = selectedTimePicker,
                    onTimePickerSelected = { target ->
                        selectedTimePicker = target
                    },
                    sleepTimePickerState = sleepTimePickerState,
                    wakeTimePickerState = wakeTimePickerState
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wake Date: ${SleepDateUtils.formatHistoryDate(wakeDateMillis)}")
                }

                SleepLogDetailsSection(
                    selectedQuality = selectedQuality,
                    selectedSnoringLevel = selectedSnoringLevel,
                    selectedTags = selectedTags,
                    dreamJournal = dreamJournal,
                    notes = notes,
                    onDetailSelected = { detailDialog ->
                        activeDetailDialog = detailDialog
                    }
                )

                SleepDurationSummaryCard(
                    durationText = SleepCalculator.formatDuration(durationMinutes),
                    activeGoalMinutes = activeGoalMinutes,
                    statusTitle = SleepCalculator.getGoalStatusTitle(
                        durationMinutes = durationMinutes,
                        goalMinutes = activeGoalMinutes
                    ),
                    feedbackText = SleepCalculator.getGoalDifferenceText(
                        durationMinutes = durationMinutes,
                        goalMinutes = activeGoalMinutes
                    ),
                    suggestionText = SleepCalculator.getImprovementSuggestion(
                        durationMinutes = durationMinutes,
                        goalMinutes = activeGoalMinutes
                    ),
                    durationValidationMessage = durationValidationMessage
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isDurationValid,
                onClick = {
                    onSave(
                        SleepLogDraft(
                            sleepHour = sleepTimePickerState.hour,
                            sleepMinute = sleepTimePickerState.minute,
                            wakeHour = wakeTimePickerState.hour,
                            wakeMinute = wakeTimePickerState.minute,
                            wakeDateMillis = wakeDateMillis,
                            quality = selectedQuality,
                            durationMinutes = durationMinutes,
                            notes = notes.trim(),
                            dreamJournal = dreamJournal.trim(),
                            snoringLevel = selectedSnoringLevel,
                            tags = SleepTag.toStorageOptions(selectedTags),
                            source = source
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

    when (activeDetailDialog) {
        SleepDetailDialog.Quality -> {
            SleepQualityDetailDialog(
                initialQuality = selectedQuality,
                onDismiss = { activeDetailDialog = null },
                onSave = { quality ->
                    selectedQuality = quality
                    activeDetailDialog = null
                }
            )
        }

        SleepDetailDialog.Snoring -> {
            SnoringDetailDialog(
                initialSnoringLevel = selectedSnoringLevel,
                onDismiss = { activeDetailDialog = null },
                onSave = { snoringLevel ->
                    selectedSnoringLevel = snoringLevel
                    activeDetailDialog = null
                }
            )
        }

        SleepDetailDialog.Tags -> {
            SleepTagsDetailDialog(
                initialTags = selectedTags,
                customTags = customTags,
                onDismiss = { activeDetailDialog = null },
                onSave = { tags ->
                    selectedTags = tags
                    activeDetailDialog = null
                }
            )
        }

        SleepDetailDialog.DreamJournal -> {
            SleepTextDetailDialog(
                title = "Dream Journal",
                value = dreamJournal,
                label = "Dream Journal",
                placeholder = "Optional: dreams, nightmares, or anything you remember",
                onDismiss = { activeDetailDialog = null },
                onSave = { text ->
                    dreamJournal = text
                    activeDetailDialog = null
                }
            )
        }

        SleepDetailDialog.Notes -> {
            SleepTextDetailDialog(
                title = "Notes",
                value = notes,
                label = "Notes",
                placeholder = "Example: Woke up twice, felt rested, had caffeine late...",
                onDismiss = { activeDetailDialog = null },
                onSave = { text ->
                    notes = text
                    activeDetailDialog = null
                }
            )
        }

        null -> Unit
    }

    if (showDatePicker) {
        WakeDatePickerDialog(
            initialDateMillis = wakeDateMillis,
            onDismiss = {
                showDatePicker = false
            },
            onConfirm = { selectedDateMillis ->
                wakeDateMillis = selectedDateMillis
                showDatePicker = false
            }
        )
    }
}
