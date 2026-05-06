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
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.domain.buildWeeklySleepChartData
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.ui.pages.SleepHistoryPage
import com.example.frontpage.sleep.ui.pages.SleepInsightsPage
import com.example.frontpage.sleep.ui.pages.SleepOverviewPage
import com.example.frontpage.sleep.ui.pages.SleepSettingsPage
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.frontpage.sleep.SleepViewModel

private enum class SleepPage(
    val label: String
) {
    Overview("Overview"),
    History("History"),
    Insights("Insights"),
    Settings("Settings")
}

@Composable
fun SleepScreen(
    modifier: Modifier = Modifier,
    sleepViewModel: SleepViewModel = viewModel()
) {
    var selectedPage by remember { mutableStateOf(SleepPage.Overview) }

    var showSleepLogDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    val sleepLogs by sleepViewModel.sleepLogs.collectAsState()
    var editingEntry by remember { mutableStateOf<SleepEntry?>(null) }
    var selectedHistoryFilter by remember { mutableStateOf(SleepHistoryFilter.All) }

    var goalMinutes by remember { mutableStateOf(SleepSettingsRepository.sleepGoalMinutes) }

    val latestSleep = sleepLogs.lastOrNull()

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
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Tracker",
            style = MaterialTheme.typography.headlineSmall
        )

        SleepPageNavigation(
            selectedPage = selectedPage,
            onPageSelected = { selectedPage = it }
        )

        when (selectedPage) {
            SleepPage.Overview -> {
                SleepOverviewPage(
                    latestSleep = latestSleep,
                    goalMinutes = goalMinutes,
                    averageSleepMinutes = averageSleepMinutes,
                    longestSleepMinutes = longestSleepMinutes,
                    shortestSleepMinutes = shortestSleepMinutes,
                    totalLogs = sleepLogs.size,
                    weeklyChartData = weeklyChartData,
                    onLogSleepClick = {
                        editingEntry = null
                        showSleepLogDialog = true
                    },
                    onEditGoalClick = {
                        showGoalDialog = true
                    }
                )
            }

            SleepPage.History -> {
                SleepHistoryPage(
                    sleepLogs = sleepLogs,
                    filteredSleepLogs = filteredSleepLogs,
                    selectedHistoryFilter = selectedHistoryFilter,
                    onFilterSelected = { selectedHistoryFilter = it },
                    onEditEntry = { entry ->
                        editingEntry = entry
                        showSleepLogDialog = true
                    },
                    onDeleteEntry = { entry ->
                        sleepViewModel.deleteSleep(entry.id)
                    }
                )
            }

            SleepPage.Insights -> {
                SleepInsightsPage(
                    sleepLogs = sleepLogs,
                    averageSleepMinutes = averageSleepMinutes,
                    longestSleepMinutes = longestSleepMinutes,
                    shortestSleepMinutes = shortestSleepMinutes,
                    averageBedtimeMinutes = averageBedtimeMinutes,
                    averageWakeTimeMinutes = averageWakeTimeMinutes,
                    sleepConsistencyVariationMinutes = sleepConsistencyVariationMinutes,
                    sleepDurationRangeMinutes = sleepDurationRangeMinutes,
                    consistencyLogCount = last7DaysSleepLogs.size
                )
            }

            SleepPage.Settings -> {
                SleepSettingsPage(
                    goalMinutes = goalMinutes,
                    onEditGoalClick = {
                        showGoalDialog = true
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

                    sleepViewModel.addSleep(
                        SleepEntry(
                            id = now,
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
                    sleepViewModel.updateSleep(
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
private fun SleepPageNavigation(
    selectedPage: SleepPage,
    onPageSelected: (SleepPage) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepPageButton(
                page = SleepPage.Overview,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            SleepPageButton(
                page = SleepPage.History,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepPageButton(
                page = SleepPage.Insights,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            SleepPageButton(
                page = SleepPage.Settings,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SleepPageButton(
    page: SleepPage,
    selectedPage: SleepPage,
    onPageSelected: (SleepPage) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = page == selectedPage

    if (isSelected) {
        Button(
            onClick = { onPageSelected(page) },
            modifier = modifier
        ) {
            Text(page.label)
        }
    } else {
        OutlinedButton(
            onClick = { onPageSelected(page) },
            modifier = modifier
        ) {
            Text(page.label)
        }
    }
}