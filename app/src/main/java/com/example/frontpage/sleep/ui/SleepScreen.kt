package com.example.frontpage.sleep.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.example.frontpage.sleep.domain.SleepDashboardStateBuilder
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepPageNavigation
import com.example.frontpage.sleep.ui.dialogs.SleepGoalDialog
import com.example.frontpage.sleep.ui.pages.SleepHistoryPage
import com.example.frontpage.sleep.ui.pages.SleepInsightsPage
import com.example.frontpage.sleep.ui.pages.SleepOverviewPage
import com.example.frontpage.sleep.ui.pages.SleepSettingsPage

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

    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val goalMinutes by viewModel.goalMinutes.collectAsState()
    val weekdaySettings by viewModel.weekdaySettings.collectAsState()
    val customTags by viewModel.customTags.collectAsState()
    val healthConnectState by viewModel.healthConnectState.collectAsState()
    val moodEntries by moodViewModel.allMoodEntries.collectAsState()
    val dashboardStateBuilder = remember { SleepDashboardStateBuilder() }

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

    val dashboardState = dashboardStateBuilder.build(
        sleepLogs = sleepLogs,
        fallbackGoalMinutes = goalMinutes,
        moodEntries = moodEntries,
        goalMinutesForDate = viewModel::getGoalMinutesForDate
    )

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
                    latestSleep = dashboardState.latestSleep,
                    goalMinutes = dashboardState.latestGoalMinutes,
                    averageSleepMinutes = dashboardState.averageSleepMinutes,
                    longestSleepMinutes = dashboardState.longestSleepMinutes,
                    shortestSleepMinutes = dashboardState.shortestSleepMinutes,
                    totalLogs = sleepLogs.size,
                    weeklyChartData = dashboardState.weeklyChartData,
                    sleepScoreSummary = dashboardState.sleepScoreSummary,
                    sleepGoalBalance = dashboardState.sleepGoalBalance,
                    streakSummary = dashboardState.streakSummary,
                    onLogSleepClick = onLogSleepClick,
                    onEditGoalClick = {
                        showGoalDialog = true
                    }
                )
            }

            SleepPage.History -> {
                SleepHistoryPage(
                    sleepLogs = sleepLogs,
                    goalMinutesForDate = viewModel::getGoalMinutesForDate,
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
                    averageSleepMinutes = dashboardState.averageSleepMinutes,
                    longestSleepMinutes = dashboardState.longestSleepMinutes,
                    shortestSleepMinutes = dashboardState.shortestSleepMinutes,
                    averageBedtimeMinutes = dashboardState.averageBedtimeMinutes,
                    averageWakeTimeMinutes = dashboardState.averageWakeTimeMinutes,
                    sleepConsistencyVariationMinutes = dashboardState.sleepConsistencyVariationMinutes,
                    sleepDurationRangeMinutes = dashboardState.sleepDurationRangeMinutes,
                    consistencyLogCount = dashboardState.last7DaysSleepLogs.size,
                    sleepScoreSummary = dashboardState.sleepScoreSummary,
                    sleepGoalBalance = dashboardState.sleepGoalBalance,
                    streakSummary = dashboardState.streakSummary,
                    primaryRecommendation = dashboardState.primaryRecommendation,
                    sleepMoodInsight = dashboardState.sleepMoodInsight,
                    sleepTagInsight = dashboardState.sleepTagInsight
                )
            }

            SleepPage.Settings -> {
                SleepSettingsPage(
                    goalMinutes = goalMinutes,
                    weekdaySettings = weekdaySettings,
                    customTags = customTags,
                    totalLogs = sleepLogs.size,
                    healthConnectState = healthConnectState,
                    onUpdateAllWeekdayGoals = { newGoalMinutes ->
                        viewModel.updateSleepGoalMinutes(newGoalMinutes)
                    },
                    onClearSleepHistoryClick = {
                        viewModel.clearAllLogs()
                    },
                    onUpdateWeekdayGoal = { weekday, newGoalMinutes ->
                        viewModel.updateWeekdayGoalMinutes(
                            weekday = weekday,
                            newGoalMinutes = newGoalMinutes
                        )
                    },
                    onUpdateWeekdayScheduleTargets = { weekday, bedtimeMinutes, wakeMinutes ->
                        viewModel.updateWeekdayScheduleTargets(
                            weekday = weekday,
                            bedtimeMinutes = bedtimeMinutes,
                            wakeMinutes = wakeMinutes
                        )
                    },
                    onUpdateAllWeekdayScheduleTargets = { bedtimeMinutes, wakeMinutes ->
                        viewModel.updateAllWeekdayScheduleTargets(
                            bedtimeMinutes = bedtimeMinutes,
                            wakeMinutes = wakeMinutes
                        )
                    },
                    onAddCustomTag = { label ->
                        viewModel.addCustomTag(label)
                    },
                    onDeleteCustomTag = { tagId ->
                        viewModel.deleteCustomTag(tagId)
                    },
                    onRequestHealthConnectAccessClick = {
                        viewModel.onHealthConnectPermissionRequestStarted()

                        try {
                            healthConnectPermissionLauncher.launch(viewModel.sleepHealthPermissions)
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
                viewModel.updateTodaySleepGoalMinutes(newGoalMinutes)
                showGoalDialog = false
            }
        )
    }
}
