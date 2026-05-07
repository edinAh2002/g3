package com.example.frontpage.sleep.ui

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.health.connect.client.PermissionController
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.sleep.SleepViewModel
import com.example.frontpage.sleep.data.SleepHealthConnectManager
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.domain.buildPrimarySleepRecommendation
import com.example.frontpage.sleep.domain.buildSleepGoalBalance
import com.example.frontpage.sleep.domain.buildSleepMoodInsight
import com.example.frontpage.sleep.domain.buildSleepScoreSummary
import com.example.frontpage.sleep.domain.buildSleepStreakSummary
import com.example.frontpage.sleep.domain.buildSleepTagInsight
import com.example.frontpage.sleep.domain.buildWeeklySleepChartData
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.ui.dialogs.SleepGoalDialog
import com.example.frontpage.sleep.ui.pages.SleepHistoryPage
import com.example.frontpage.sleep.ui.pages.SleepInsightsPage
import com.example.frontpage.sleep.ui.pages.SleepOverviewPage
import com.example.frontpage.sleep.ui.pages.SleepSettingsPage

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
    onLogSleepClick: () -> Unit,
    onEditSleepEntry: (SleepEntry) -> Unit,
    viewModel: SleepViewModel = viewModel(),
    moodViewModel: MoodViewModel = viewModel()
) {
    var selectedPage by remember { mutableStateOf(SleepPage.Overview) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var selectedHistoryFilter by remember { mutableStateOf(SleepHistoryFilter.All) }

    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val goalMinutes by viewModel.goalMinutes.collectAsState()
    val healthConnectState by viewModel.healthConnectState.collectAsState()
    val moodEntries by moodViewModel.allMoodEntries.collectAsState()

    val healthConnectPermissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        viewModel.onHealthConnectPermissionsChanged(grantedPermissions)
    }

    LaunchedEffect(moodViewModel) {
        moodViewModel.refreshCurrentUser()
    }

    LaunchedEffect(viewModel) {
        viewModel.refreshCurrentUser()
        viewModel.refreshHealthConnectState()
    }

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

    val sleepScoreSummary = buildSleepScoreSummary(
        latestSleep = latestSleep,
        goalMinutes = goalMinutes,
        consistencyVariationMinutes = sleepConsistencyVariationMinutes,
        durationRangeMinutes = sleepDurationRangeMinutes
    )

    val sleepGoalBalance = buildSleepGoalBalance(
        sleepLogs = last7DaysSleepLogs,
        goalMinutes = goalMinutes
    )

    val streakSummary = buildSleepStreakSummary(
        sleepLogs = sleepLogs,
        goalMinutes = goalMinutes
    )

    val primaryRecommendation = buildPrimarySleepRecommendation(
        latestSleep = latestSleep,
        goalMinutes = goalMinutes,
        sleepGoalBalance = sleepGoalBalance,
        streakSummary = streakSummary,
        consistencyVariationMinutes = sleepConsistencyVariationMinutes,
        durationRangeMinutes = sleepDurationRangeMinutes
    )

    val sleepMoodInsight = buildSleepMoodInsight(
        sleepLogs = sleepLogs,
        moodEntries = moodEntries
    )

    val sleepTagInsight = buildSleepTagInsight(sleepLogs)

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
                    sleepScoreSummary = sleepScoreSummary,
                    sleepGoalBalance = sleepGoalBalance,
                    streakSummary = streakSummary,
                    onLogSleepClick = onLogSleepClick,
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
                        onEditSleepEntry(entry)
                    },
                    onDeleteEntry = { entry ->
                        viewModel.deleteSleep(entry.id)
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
                    consistencyLogCount = last7DaysSleepLogs.size,
                    sleepScoreSummary = sleepScoreSummary,
                    sleepGoalBalance = sleepGoalBalance,
                    streakSummary = streakSummary,
                    primaryRecommendation = primaryRecommendation,
                    sleepMoodInsight = sleepMoodInsight,
                    sleepTagInsight = sleepTagInsight
                )
            }

            SleepPage.Settings -> {
                SleepSettingsPage(
                    goalMinutes = goalMinutes,
                    totalLogs = sleepLogs.size,
                    healthConnectState = healthConnectState,
                    onEditGoalClick = {
                        showGoalDialog = true
                    },
                    onClearSleepHistoryClick = {
                        viewModel.clearAllLogs()
                    },
                    onRequestHealthConnectAccessClick = {
                        viewModel.onHealthConnectPermissionRequestStarted()

                        try {
                            healthConnectPermissionLauncher.launch(SleepHealthConnectManager.PERMISSIONS)
                        } catch (_: Exception) {
                            viewModel.onHealthConnectPermissionRequestFailed()
                        }
                    },
                    onImportHealthConnectSleepClick = {
                        viewModel.importHealthConnectSleep()
                    }
                )
            }
        }
    }

    if (showGoalDialog) {
        SleepGoalDialog(
            currentGoalMinutes = goalMinutes,
            onDismiss = {
                showGoalDialog = false
            },
            onSave = { newGoalMinutes ->
                viewModel.updateSleepGoalMinutes(newGoalMinutes)
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
