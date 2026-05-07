package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepQuality
import com.example.frontpage.sleep.model.SleepTag
import com.example.frontpage.sleep.model.SleepTagOption
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.SnoringLevel
import com.example.frontpage.sleep.model.WeekdaySleepSettings

private enum class TimePickerTarget {
    SleepTime,
    WakeTime
}

private enum class SleepDetailDialog {
    Quality,
    Snoring,
    Tags,
    DreamJournal,
    Notes
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepLogDialog(
    existingEntry: SleepEntry? = null,
    goalMinutes: Int,
    weekdaySettings: List<WeekdaySleepSettings> = emptyList(),
    customTags: List<SleepCustomTag> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        sleepHour: Int,
        sleepMinute: Int,
        wakeHour: Int,
        wakeMinute: Int,
        wakeDateMillis: Long,
        quality: SleepQuality,
        durationMinutes: Int,
        notes: String,
        dreamJournal: String,
        snoringLevel: SnoringLevel,
        tags: String
    ) -> Unit
) {
    val initialWakeDateMillis = existingEntry?.dateMillis ?: System.currentTimeMillis()
    val initialSettings = weekdaySettings.settingsForDate(initialWakeDateMillis)

    var sleepHour by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.sleepHour ?: ((initialSettings?.bedtimeMinutes ?: (23 * 60)) / 60))
    }

    var sleepMinute by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.sleepMinute ?: ((initialSettings?.bedtimeMinutes ?: (23 * 60)) % 60))
    }

    var wakeHour by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.wakeHour ?: ((initialSettings?.wakeMinutes ?: (7 * 60)) / 60))
    }

    var wakeMinute by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.wakeMinute ?: ((initialSettings?.wakeMinutes ?: (7 * 60)) % 60))
    }

    var wakeDateMillis by remember(existingEntry?.id) {
        mutableStateOf(initialWakeDateMillis)
    }

    var selectedQuality by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.quality ?: SleepQuality.Good)
    }

    var notes by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.notes ?: "")
    }

    var dreamJournal by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.dreamJournal ?: "")
    }

    var selectedSnoringLevel by remember(existingEntry?.id) {
        mutableStateOf(existingEntry?.snoringLevel ?: SnoringLevel.None)
    }

    var selectedTags by remember(existingEntry?.id) {
        mutableStateOf(SleepTag.optionsFromStorage(existingEntry?.tags.orEmpty()))
    }

    var selectedTimePicker by remember(existingEntry?.id) {
        mutableStateOf(TimePickerTarget.SleepTime)
    }

    val sleepTimePickerState = rememberTimePickerState(
        initialHour = sleepHour,
        initialMinute = sleepMinute,
        is24Hour = true
    )

    val wakeTimePickerState = rememberTimePickerState(
        initialHour = wakeHour,
        initialMinute = wakeMinute,
        is24Hour = true
    )

    var showDatePicker by remember { mutableStateOf(false) }
    var activeDetailDialog by remember { mutableStateOf<SleepDetailDialog?>(null) }

    val durationMinutes = SleepCalculator.calculateDurationMinutes(
        sleepHour = sleepTimePickerState.hour,
        sleepMinute = sleepTimePickerState.minute,
        wakeHour = wakeTimePickerState.hour,
        wakeMinute = wakeTimePickerState.minute
    )

    val durationText = SleepCalculator.formatDuration(durationMinutes)
    val activeGoalMinutes = weekdaySettings.settingsForDate(wakeDateMillis)?.goalMinutes
        ?: goalMinutes

    val statusTitle = SleepCalculator.getGoalStatusTitle(
        durationMinutes = durationMinutes,
        goalMinutes = activeGoalMinutes
    )

    val feedbackText = SleepCalculator.getGoalDifferenceText(
        durationMinutes = durationMinutes,
        goalMinutes = activeGoalMinutes
    )

    val suggestionText = SleepCalculator.getImprovementSuggestion(
        durationMinutes = durationMinutes,
        goalMinutes = activeGoalMinutes
    )

    val durationValidationMessage = SleepCalculator.getDurationValidationMessage(durationMinutes)
    val isDurationValid = SleepCalculator.isRealisticDuration(durationMinutes)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (existingEntry == null) "Log Sleep" else "Edit Sleep")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (selectedTimePicker == TimePickerTarget.SleepTime) {
                            OutlinedButton(
                                onClick = {
                                    selectedTimePicker = TimePickerTarget.SleepTime
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Sleep\n${
                                        SleepCalculator.formatTime(
                                            sleepTimePickerState.hour,
                                            sleepTimePickerState.minute
                                        )
                                    }"
                                )
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    selectedTimePicker = TimePickerTarget.SleepTime
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Sleep\n${
                                        SleepCalculator.formatTime(
                                            sleepTimePickerState.hour,
                                            sleepTimePickerState.minute
                                        )
                                    }"
                                )
                            }
                        }

                        if (selectedTimePicker == TimePickerTarget.WakeTime) {
                            OutlinedButton(
                                onClick = {
                                    selectedTimePicker = TimePickerTarget.WakeTime
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Wake\n${
                                        SleepCalculator.formatTime(
                                            wakeTimePickerState.hour,
                                            wakeTimePickerState.minute
                                        )
                                    }"
                                )
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    selectedTimePicker = TimePickerTarget.WakeTime
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Wake\n${
                                        SleepCalculator.formatTime(
                                            wakeTimePickerState.hour,
                                            wakeTimePickerState.minute
                                        )
                                    }"
                                )
                            }
                        }
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

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Wake Date: ${SleepDateUtils.formatHistoryDate(wakeDateMillis)}")
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SleepDetailRow(
                        title = "Sleep Quality",
                        value = selectedQuality.name,
                        onClick = { activeDetailDialog = SleepDetailDialog.Quality }
                    )

                    SleepDetailRow(
                        title = "Snoring",
                        value = selectedSnoringLevel.label,
                        onClick = { activeDetailDialog = SleepDetailDialog.Snoring }
                    )

                    SleepDetailRow(
                        title = "Sleep Tags",
                        value = sleepTagSummary(selectedTags),
                        onClick = { activeDetailDialog = SleepDetailDialog.Tags }
                    )

                    SleepDetailRow(
                        title = "Dream Journal",
                        value = sleepTextSummary(dreamJournal),
                        onClick = { activeDetailDialog = SleepDetailDialog.DreamJournal }
                    )

                    SleepDetailRow(
                        title = "Notes",
                        value = sleepTextSummary(notes),
                        onClick = { activeDetailDialog = SleepDetailDialog.Notes }
                    )
                }

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
        },
        confirmButton = {
            TextButton(
                enabled = isDurationValid,
                onClick = {
                    onSave(
                        sleepTimePickerState.hour,
                        sleepTimePickerState.minute,
                        wakeTimePickerState.hour,
                        wakeTimePickerState.minute,
                        wakeDateMillis,
                        selectedQuality,
                        durationMinutes,
                        notes.trim(),
                        dreamJournal.trim(),
                        selectedSnoringLevel,
                        SleepTag.toStorageOptions(selectedTags)
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

@Composable
private fun SleepDetailRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SleepQualityDetailDialog(
    initialQuality: SleepQuality,
    onDismiss: () -> Unit,
    onSave: (SleepQuality) -> Unit
) {
    var draftQuality by remember(initialQuality) {
        mutableStateOf(initialQuality)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Quality")
        },
        text = {
            QualityButtons(
                selectedQuality = draftQuality,
                onQualitySelected = { draftQuality = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftQuality) }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SnoringDetailDialog(
    initialSnoringLevel: SnoringLevel,
    onDismiss: () -> Unit,
    onSave: (SnoringLevel) -> Unit
) {
    var draftSnoringLevel by remember(initialSnoringLevel) {
        mutableStateOf(initialSnoringLevel)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Snoring")
        },
        text = {
            SnoringSelector(
                selectedSnoringLevel = draftSnoringLevel,
                onSnoringLevelSelected = { draftSnoringLevel = it },
                showTitle = false
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftSnoringLevel) }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SleepTagsDetailDialog(
    initialTags: List<SleepTagOption>,
    customTags: List<SleepCustomTag>,
    onDismiss: () -> Unit,
    onSave: (List<SleepTagOption>) -> Unit
) {
    var draftTags by remember(initialTags) {
        mutableStateOf(initialTags)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Tags")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                SleepTagSelector(
                    selectedTags = draftTags,
                    customTags = customTags,
                    onTagToggled = { tag ->
                        draftTags = if (draftTags.any { selectedTag -> selectedTag.storageValue == tag.storageValue }) {
                            draftTags.filterNot { selectedTag -> selectedTag.storageValue == tag.storageValue }
                        } else {
                            draftTags + tag
                        }
                    },
                    showTitle = false
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftTags) }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SleepTextDetailDialog(
    title: String,
    value: String,
    label: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var draftText by remember(title, value) {
        mutableStateOf(value)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            OutlinedTextField(
                value = draftText,
                onValueChange = { draftText = it },
                label = { Text(label) },
                placeholder = { Text(placeholder) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftText.trim()) }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun sleepTagSummary(tags: List<SleepTagOption>): String {
    if (tags.isEmpty()) return "None"

    val visibleTags = tags.take(3).joinToString(", ") { tag ->
        tag.label
    }

    return if (tags.size > 3) {
        "$visibleTags +${tags.size - 3}"
    } else {
        visibleTags
    }
}

private fun sleepTextSummary(text: String): String {
    return text.trim().ifBlank { "Not added" }
}

@Composable
private fun QualityButtons(
    selectedQuality: SleepQuality,
    onQualitySelected: (SleepQuality) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QualityButton(
                text = "Poor",
                selected = selectedQuality == SleepQuality.Poor,
                onClick = { onQualitySelected(SleepQuality.Poor) },
                modifier = Modifier.weight(1f)
            )

            QualityButton(
                text = "Okay",
                selected = selectedQuality == SleepQuality.Okay,
                onClick = { onQualitySelected(SleepQuality.Okay) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QualityButton(
                text = "Good",
                selected = selectedQuality == SleepQuality.Good,
                onClick = { onQualitySelected(SleepQuality.Good) },
                modifier = Modifier.weight(1f)
            )

            QualityButton(
                text = "Great",
                selected = selectedQuality == SleepQuality.Great,
                onClick = { onQualitySelected(SleepQuality.Great) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SnoringSelector(
    selectedSnoringLevel: SnoringLevel,
    onSnoringLevelSelected: (SnoringLevel) -> Unit,
    showTitle: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showTitle) {
            Text(
                text = "Snoring",
                style = MaterialTheme.typography.titleSmall
            )
        }

        SnoringLevel.entries.chunked(2).forEach { rowLevels ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowLevels.forEach { snoringLevel ->
                    QualityButton(
                        text = snoringLevel.label,
                        selected = selectedSnoringLevel == snoringLevel,
                        onClick = { onSnoringLevelSelected(snoringLevel) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepTagSelector(
    selectedTags: List<SleepTagOption>,
    customTags: List<SleepCustomTag>,
    onTagToggled: (SleepTagOption) -> Unit,
    showTitle: Boolean = true
) {
    val availableTags = SleepTag.builtInOptions() + customTags.map { customTag ->
        SleepTag.customOption(customTag)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showTitle) {
            Text(
                text = "Sleep Tags",
                style = MaterialTheme.typography.titleSmall
            )
        }

        if (showTitle) {
            Text(
                text = if (selectedTags.isEmpty()) {
                    "Optional context for patterns"
                } else {
                    "${selectedTags.size} selected"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }

        availableTags.groupBy { it.category }.forEach { (category, tags) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                tags.chunked(2).forEach { rowTags ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowTags.forEach { tag ->
                            QualityButton(
                                text = tag.label,
                                selected = selectedTags.any { selectedTag ->
                                    selectedTag.storageValue == tag.storageValue
                                },
                                onClick = { onTagToggled(tag) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowTags.size == 1) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

private fun List<WeekdaySleepSettings>.settingsForDate(dateMillis: Long): WeekdaySleepSettings? {
    val weekday = SleepWeekday.fromDateMillis(dateMillis)

    return firstOrNull { settings ->
        settings.weekday == weekday
    }
}

@Composable
private fun QualityButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(
                text = text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(
                text = text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WakeDatePickerDialog(
    initialDateMillis: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        datePickerState.selectedDateMillis ?: initialDateMillis
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockPickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        timePickerState.hour,
                        timePickerState.minute
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
