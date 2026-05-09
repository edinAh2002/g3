package com.example.frontpage.mood.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.domain.MoodDashboardStateBuilder
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.ui.components.MoodPageNavigation
import com.example.frontpage.mood.ui.pages.MoodHistoryPage
import com.example.frontpage.mood.ui.pages.MoodInsightsPage
import com.example.frontpage.mood.ui.pages.MoodOverviewPage
import com.example.frontpage.mood.ui.pages.MoodSettingsPage

@Composable
fun MoodScreen(
    modifier: Modifier = Modifier,
    onLogMoodClick: () -> Unit,
    onEditMoodEntry: (MoodEntry) -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    var selectedPage by remember { mutableStateOf(MoodPage.Overview) }

    val allMoodEntries by viewModel.allMoodEntries.collectAsState()
    val filteredMoodEntries by viewModel.filteredMoodEntries.collectAsState()
    val filteredAverageMood by viewModel.filteredAverageMood.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val defaultScalePreset by viewModel.defaultScalePreset.collectAsState()
    val dashboardStateBuilder = remember { MoodDashboardStateBuilder() }

    LaunchedEffect(viewModel) {
        viewModel.refreshCurrentUser()
    }

    val dashboardState = dashboardStateBuilder.build(
        moodEntries = allMoodEntries,
        scalePreset = defaultScalePreset
    )

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood Tracker",
            style = MaterialTheme.typography.headlineSmall
        )

        MoodPageNavigation(
            selectedPage = selectedPage,
            onPageSelected = { page -> selectedPage = page }
        )

        when (selectedPage) {
            MoodPage.Overview -> {
                MoodOverviewPage(
                    latestMood = dashboardState.latestMood,
                    averageMood = dashboardState.averageMood,
                    todayAverageMood = dashboardState.todayAverageMood,
                    bestMood = dashboardState.bestMood,
                    lowestMood = dashboardState.lowestMood,
                    totalLogs = dashboardState.totalLogs,
                    weeklyChartData = dashboardState.weeklyChartData,
                    moodScoreSummary = dashboardState.moodScoreSummary,
                    moodMomentumSummary = dashboardState.moodMomentumSummary,
                    streakSummary = dashboardState.streakSummary,
                    scalePreset = defaultScalePreset,
                    onLogMoodClick = onLogMoodClick,
                    onViewScaleClick = {
                        selectedPage = MoodPage.Settings
                    }
                )
            }

            MoodPage.History -> {
                MoodHistoryPage(
                    moodEntries = allMoodEntries,
                    filteredMoodEntries = filteredMoodEntries,
                    filteredAverageMood = filteredAverageMood,
                    filterState = filterState,
                    scalePreset = defaultScalePreset,
                    onFeelingFilterSelected = { filter ->
                        viewModel.setFeelingFilter(filter)
                    },
                    onClearFilters = {
                        viewModel.clearFilters()
                    },
                    onEditEntry = { entry ->
                        onEditMoodEntry(entry)
                    },
                    onDeleteEntry = { entry ->
                        viewModel.deleteMood(entry)
                    },
                    onDeleteEntries = { entries ->
                        viewModel.deleteMoods(entries)
                    }
                )
            }

            MoodPage.Insights -> {
                MoodInsightsPage(
                    moodEntries = allMoodEntries,
                    averageMood = dashboardState.averageMood,
                    todayAverageMood = dashboardState.todayAverageMood,
                    bestMood = dashboardState.bestMood,
                    lowestMood = dashboardState.lowestMood,
                    moodScoreSummary = dashboardState.moodScoreSummary,
                    moodMomentumSummary = dashboardState.moodMomentumSummary,
                    streakSummary = dashboardState.streakSummary,
                    primaryRecommendation = dashboardState.primaryRecommendation,
                    moodPatternInsight = dashboardState.moodPatternInsight,
                    moodNoteInsight = dashboardState.moodNoteInsight,
                    scalePreset = defaultScalePreset
                )
            }

            MoodPage.Settings -> {
                MoodSettingsPage(
                    totalLogs = dashboardState.totalLogs,
                    defaultScalePreset = defaultScalePreset,
                    onDefaultScalePresetSelected = { preset ->
                        viewModel.updateDefaultScalePreset(preset)
                    },
                    onClearMoodHistoryClick = {
                        viewModel.clearAllLogs()
                    }
                )
            }
        }
    }
}
