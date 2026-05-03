package com.example.frontpage.sleep

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.foundation.layout.width

@Composable
fun SleepScreen() {
    var showSleepLogDialog by remember { mutableStateOf(false) }
    var sleepLogs by remember { mutableStateOf(SleepRepository.getAllSleepLogs().toList()) }
    var editingEntry by remember { mutableStateOf<SleepEntry?>(null) }
    var selectedHistoryFilter by remember { mutableStateOf(SleepHistoryFilter.All) }

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
    val weeklyChartData = buildWeeklySleepChartData(sleepLogs)

    val averageBedtimeMinutes = SleepCalculator.calculateAverageBedtimeMinutes(sleepLogs)
    val averageWakeTimeMinutes = SleepCalculator.calculateAverageWakeTimeMinutes(sleepLogs)

    val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

    val last7DaysSleepLogs = sleepLogs.filter {
        it.dateMillis >= sevenDaysAgo
    }

    val sleepConsistencyVariationMinutes =
        SleepCalculator.calculateSleepConsistencyVariationMinutes(last7DaysSleepLogs)

    val sleepDurationRangeMinutes =
        SleepCalculator.calculateSleepDurationRangeMinutes(last7DaysSleepLogs)

    val filteredSleepLogs = when (selectedHistoryFilter) {
        SleepHistoryFilter.All -> sleepLogs
        SleepHistoryFilter.Today -> sleepLogs.filter {
            SleepDateUtils.isToday(it.dateMillis)
        }
        SleepHistoryFilter.ThisWeek -> sleepLogs.filter {
            SleepDateUtils.isThisWeek(it.dateMillis)
        }
        SleepHistoryFilter.ThisMonth -> sleepLogs.filter {
            SleepDateUtils.isThisMonth(it.dateMillis)
        }
    }

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
                        text = SleepDateUtils.formatHistoryDate(latestSleep.dateMillis),
                        style = MaterialTheme.typography.bodySmall
                    )

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
            text = "Weekly Sleep Chart",
            style = MaterialTheme.typography.titleMedium
        )

        WeeklySleepChart(
            chartData = weeklyChartData,
            goalMinutes = goalMinutes
        )

        Text(
            text = "Bedtime & Wake-Up Trends",
            style = MaterialTheme.typography.titleMedium
        )

        SleepTrendsCard(
            sleepLogs = sleepLogs,
            averageBedtimeMinutes = averageBedtimeMinutes,
            averageWakeTimeMinutes = averageWakeTimeMinutes,
            averageDurationMinutes = averageSleepMinutes
        )

        Text(
            text = "Sleep Consistency - Last 7 Days",
            style = MaterialTheme.typography.titleMedium
        )

        SleepConsistencyCard(
            variationMinutes = sleepConsistencyVariationMinutes,
            durationRangeMinutes = sleepDurationRangeMinutes,
            logCount = last7DaysSleepLogs.size
        )

        Text(
            text = "Sleep History",
            style = MaterialTheme.typography.titleMedium
        )

        SleepHistoryFilterRow(
            selectedFilter = selectedHistoryFilter,
            onFilterSelected = { selectedHistoryFilter = it }
        )

        if (sleepLogs.isEmpty()) {
            Text("Your sleep history will appear here.")
        } else if (filteredSleepLogs.isEmpty()) {
            Text("No sleep logs found for this filter.")
        } else {
            filteredSleepLogs.reversed().forEach { entry ->
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
                    val now = System.currentTimeMillis()

                    SleepRepository.addSleep(
                        SleepEntry(
                            id = now.toInt(),
                            date = SleepDateUtils.formatHistoryDate(now),
                            sleepHour = sleepHour,
                            sleepMinute = sleepMinute,
                            wakeHour = wakeHour,
                            wakeMinute = wakeMinute,
                            durationMinutes = durationMinutes,
                            quality = quality,
                            notes = notes,
                            dateMillis = now
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
                text = SleepDateUtils.formatHistoryDate(entry.dateMillis),
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

            Text("You slept ${SleepCalculator.formatDuration(durationMinutes)}.")

            Text("Your goal is ${SleepCalculator.formatDuration(goalMinutes)}.")

            Text("Progress: $progressPercent%")

            Text(differenceText)

            Text(
                text = suggestionText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SleepHistoryFilterRow(
    selectedFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.All,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )

            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.Today,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.ThisWeek,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )

            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.ThisMonth,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SleepHistoryFilterButton(
    filter: SleepHistoryFilter,
    selectedFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = filter == selectedFilter

    if (isSelected) {
        Button(
            onClick = { onFilterSelected(filter) },
            modifier = modifier
        ) {
            Text(filter.label)
        }
    } else {
        OutlinedButton(
            onClick = { onFilterSelected(filter) },
            modifier = modifier
        ) {
            Text(filter.label)
        }
    }
}

@Composable
fun WeeklySleepChart(
    chartData: List<WeeklySleepChartItem>,
    goalMinutes: Int,
    modifier: Modifier = Modifier
) {
    val maxDuration = maxOf(
        goalMinutes,
        chartData.maxOfOrNull { it.durationMinutes } ?: 0,
        1
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleSmall
            )

            if (chartData.all { it.durationMinutes == 0 }) {
                Text(
                    text = "No sleep logged this week yet.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            chartData.forEach { item ->
                WeeklySleepChartRow(
                    item = item,
                    maxDuration = maxDuration
                )
            }
        }
    }
}

@Composable
fun WeeklySleepChartRow(
    item: WeeklySleepChartItem,
    maxDuration: Int
) {
    val progress = if (maxDuration > 0) {
        item.durationMinutes.toFloat() / maxDuration.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.dayLabel,
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        )

        Text(
            text = if (item.durationMinutes > 0) {
                SleepCalculator.formatDuration(item.durationMinutes)
            } else {
                "--"
            },
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SleepTrendsCard(
    sleepLogs: List<SleepEntry>,
    averageBedtimeMinutes: Int?,
    averageWakeTimeMinutes: Int?,
    averageDurationMinutes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (sleepLogs.isEmpty()) {
                Text(
                    text = "No trend data yet",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "Log sleep for a few days to see your usual bedtime and wake-up time.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = SleepCalculator.getSleepTrendSummary(sleepLogs),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SleepTrendItem(
                        title = "Avg Bedtime",
                        value = averageBedtimeMinutes?.let {
                            SleepCalculator.formatClockMinutes(it)
                        } ?: "--",
                        modifier = Modifier.weight(1f)
                    )

                    SleepTrendItem(
                        title = "Avg Wake",
                        value = averageWakeTimeMinutes?.let {
                            SleepCalculator.formatClockMinutes(it)
                        } ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                }

                SleepTrendItem(
                    title = "Avg Duration",
                    value = SleepCalculator.formatDuration(averageDurationMinutes),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SleepTrendItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SleepConsistencyCard(
    variationMinutes: Int?,
    durationRangeMinutes: Int?,
    logCount: Int,
    modifier: Modifier = Modifier
) {
    val rating = SleepCalculator.getSleepConsistencyRating(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    val description = SleepCalculator.getSleepConsistencyDescription(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    val progress = SleepCalculator.calculateSleepConsistencyProgress(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = rating,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Based on $logCount logs from the last 7 days",
                style = MaterialTheme.typography.bodySmall
            )

            if (variationMinutes != null && durationRangeMinutes != null) {
                Text(
                    text = "Average consistency variation: ${SleepCalculator.formatDuration(variationMinutes)}"
                )

                Text(
                    text = "Sleep duration changed by up to ${SleepCalculator.formatDuration(durationRangeMinutes)} between logs."
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

data class WeeklySleepChartItem(
    val dayLabel: String,
    val durationMinutes: Int,
    val dateMillis: Long
)

fun buildWeeklySleepChartData(
    sleepLogs: List<SleepEntry>
): List<WeeklySleepChartItem> {
    val calendar = java.util.Calendar.getInstance()
    val items = mutableListOf<WeeklySleepChartItem>()

    for (daysAgo in 6 downTo 0) {
        val dayCalendar = calendar.clone() as java.util.Calendar
        dayCalendar.add(java.util.Calendar.DAY_OF_YEAR, -daysAgo)

        val dayMillis = dayCalendar.timeInMillis

        val latestLogForDay = sleepLogs
            .filter { SleepDateUtils.isSameDay(it.dateMillis, dayMillis) }
            .maxByOrNull { it.dateMillis }

        items.add(
            WeeklySleepChartItem(
                dayLabel = SleepDateUtils.formatDayName(dayMillis),
                durationMinutes = latestLogForDay?.durationMinutes ?: 0,
                dateMillis = dayMillis
            )
        )
    }

    return items
}

enum class SleepHistoryFilter(
    val label: String
) {
    All("All"),
    Today("Today"),
    ThisWeek("This Week"),
    ThisMonth("This Month")
}