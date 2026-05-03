package com.example.frontpage.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState

@Composable
fun SleepScreen() {
    var showSleepLogDialog by remember { mutableStateOf(false) }
    var sleepLogs by remember { mutableStateOf(SleepRepository.getAllSleepLogs().toList()) }
    var editingEntry by remember { mutableStateOf<SleepEntry?>(null) }

    val latestSleep = sleepLogs.lastOrNull()
    var goalMinutes by remember { mutableStateOf(SleepSettingsRepository.sleepGoalMinutes) }
    var showGoalDialog by remember { mutableStateOf(false) }

    val averageSleepMinutes = if (sleepLogs.isEmpty()) {
        0
    } else {
        sleepLogs.map { it.durationMinutes }.average().toInt()
    }

    val longestSleepMinutes = sleepLogs.maxOfOrNull { it.durationMinutes } ?: 0
    val shortestSleepMinutes = sleepLogs.minOfOrNull { it.durationMinutes } ?: 0

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Tracker",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Latest Sleep", style = MaterialTheme.typography.titleMedium)

                if (latestSleep == null) {
                    Text("No sleep logged yet.")
                    Text("Tap Log Sleep to add your first sleep entry.")

                    OutlinedButton(
                        onClick = { showGoalDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Sleep Goal: ${SleepCalculator.formatDuration(goalMinutes)}")
                    }
                } else {
                    Text(
                        text = SleepCalculator.formatDuration(latestSleep.durationMinutes),
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "From ${SleepCalculator.formatTime(latestSleep.sleepHour, latestSleep.sleepMinute)} to ${SleepCalculator.formatTime(latestSleep.wakeHour, latestSleep.wakeMinute)}"
                    )

                    Text("Quality: ${latestSleep.quality}")

                    LinearProgressIndicator(
                        progress = {
                            SleepCalculator.calculateGoalProgress(
                                durationMinutes = latestSleep.durationMinutes,
                                goalMinutes = goalMinutes
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Goal: ${SleepCalculator.formatDuration(goalMinutes)}")

                    SleepFeedbackCard(
                        durationMinutes = latestSleep.durationMinutes,
                        goalMinutes = goalMinutes
                    )

                    OutlinedButton(
                        onClick = { showGoalDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Sleep Goal")
                    }
                }
            }
        }

        Button(
            onClick = {
                editingEntry = null
                showSleepLogDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Sleep")
        }

        Text(
            text = "Sleep Statistics",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepStatCard(
                title = "Average",
                value = SleepCalculator.formatDuration(averageSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepStatCard(
                title = "Logs",
                value = sleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepStatCard(
                title = "Longest",
                value = SleepCalculator.formatDuration(longestSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepStatCard(
                title = "Shortest",
                value = SleepCalculator.formatDuration(shortestSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Sleep History",
            style = MaterialTheme.typography.titleMedium
        )

        if (sleepLogs.isEmpty()) {
            Text("Your sleep history will appear here.")
        } else {
            sleepLogs.reversed().forEach { entry ->
                SleepHistoryCard(
                    entry = entry,
                    onEdit = {
                        editingEntry = entry
                        showSleepLogDialog = true
                    },
                    onDelete = {
                        SleepRepository.deleteSleep(entry.id)
                        sleepLogs = SleepRepository.getAllSleepLogs().toList()
                    }
                )
            }
        }
    }

    if (showSleepLogDialog) {
        SleepLogDialog(
            existingEntry = editingEntry,
            goalMinutes = goalMinutes,
            onDismiss = {
                showSleepLogDialog = false
                editingEntry = null
            },
            onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, quality, durationMinutes, notes ->

                if (editingEntry == null) {
                    SleepRepository.addSleep(
                        SleepEntry(
                            id = System.currentTimeMillis().toInt(),
                            date = "Today",
                            sleepHour = sleepHour,
                            sleepMinute = sleepMinute,
                            wakeHour = wakeHour,
                            wakeMinute = wakeMinute,
                            durationMinutes = durationMinutes,
                            quality = quality,
                            notes = notes
                        )
                    )
                } else {
                    SleepRepository.updateSleep(
                        editingEntry!!.copy(
                            sleepHour = sleepHour,
                            sleepMinute = sleepMinute,
                            wakeHour = wakeHour,
                            wakeMinute = wakeMinute,
                            durationMinutes = durationMinutes,
                            quality = quality,
                            notes = notes
                        )
                    )
                }

                sleepLogs = SleepRepository.getAllSleepLogs().toList()
                showSleepLogDialog = false
                editingEntry = null
            }
        )
    }

    if (showGoalDialog) {
        SleepGoalDialog(
            currentGoalMinutes = goalMinutes,
            onDismiss = {
                showGoalDialog = false
            },
            onSave = { newGoalMinutes ->
                SleepSettingsRepository.updateSleepGoalMinutes(newGoalMinutes)
                goalMinutes = SleepSettingsRepository.sleepGoalMinutes
                showGoalDialog = false
            }
        )
    }
}

@Composable
fun SleepStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SleepHistoryCard(
    entry: SleepEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = entry.date,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = SleepCalculator.formatDuration(entry.durationMinutes),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${SleepCalculator.formatTime(entry.sleepHour, entry.sleepMinute)} → ${SleepCalculator.formatTime(entry.wakeHour, entry.wakeMinute)}"
            )

            Text("Quality: ${entry.quality}")

            if (entry.notes.isNotBlank()) {
                Text("Notes: ${entry.notes}")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepGoalDialog(
    currentGoalMinutes: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    val currentHour = currentGoalMinutes / 60
    val currentMinute = currentGoalMinutes % 60

    val timePickerState = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = true
    )

    val selectedGoalMinutes = timePickerState.hour * 60 + timePickerState.minute

    val isGoalValid = selectedGoalMinutes in (4 * 60)..(12 * 60)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Sleep Goal")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Choose your target sleep duration.")

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Selected goal: ${SleepCalculator.formatDuration(selectedGoalMinutes)}",
                    style = MaterialTheme.typography.titleMedium
                )

                if (!isGoalValid) {
                    Text(
                        text = "Sleep goal must be between 4h and 12h.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "This goal will be used for your progress bar and sleep feedback.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isGoalValid,
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

@Composable
fun SleepFeedbackCard(
    durationMinutes: Int,
    goalMinutes: Int,
    modifier: Modifier = Modifier
) {
    val statusTitle = SleepCalculator.getGoalStatusTitle(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    val progressPercent = SleepCalculator.calculateGoalProgressPercent(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    val differenceText = SleepCalculator.getGoalDifferenceText(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    val suggestionText = SleepCalculator.getImprovementSuggestion(
        durationMinutes = durationMinutes,
        goalMinutes = goalMinutes
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = statusTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "You slept ${SleepCalculator.formatDuration(durationMinutes)}."
            )

            Text(
                text = "Your goal is ${SleepCalculator.formatDuration(goalMinutes)}."
            )

            Text(
                text = "Progress: $progressPercent%"
            )

            Text(
                text = differenceText
            )

            Text(
                text = suggestionText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}