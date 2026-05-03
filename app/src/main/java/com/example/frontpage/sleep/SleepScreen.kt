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

@Composable
fun SleepScreen() {
    var showSleepLogDialog by remember { mutableStateOf(false) }
    var sleepLogs by remember { mutableStateOf(SleepRepository.getAllSleepLogs().toList()) }

    val latestSleep = sleepLogs.lastOrNull()
    val goalMinutes = 8 * 60

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
                    Text(SleepCalculator.getSleepFeedback(latestSleep.durationMinutes))
                }
            }
        }

        Button(
            onClick = { showSleepLogDialog = true },
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
            onDismiss = {
                showSleepLogDialog = false
            },
            onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, quality, durationMinutes, notes ->
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

                sleepLogs = SleepRepository.getAllSleepLogs().toList()
                showSleepLogDialog = false
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

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete")
            }
        }
    }
}