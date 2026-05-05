package com.example.frontpage.sleep.ui

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
import com.example.frontpage.sleep.ui.components.SleepConsistencyCard
import com.example.frontpage.sleep.ui.components.SleepFeedbackCard
import com.example.frontpage.sleep.ui.components.SleepHistoryCard
import com.example.frontpage.sleep.ui.components.SleepHistoryFilterRow
import com.example.frontpage.sleep.ui.components.SleepStatCard
import com.example.frontpage.sleep.ui.components.SleepTrendsCard
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.ui.components.WeeklySleepChart
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.buildWeeklySleepChartData

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
