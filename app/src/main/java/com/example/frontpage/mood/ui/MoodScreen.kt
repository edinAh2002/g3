package com.example.frontpage.mood.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.model.MoodSection
import com.example.frontpage.mood.ui.pages.MoodHistoryPage
import com.example.frontpage.mood.ui.pages.MoodInsightsPage
import com.example.frontpage.mood.ui.pages.MoodOverviewPage
import com.example.frontpage.mood.ui.pages.MoodSettingsPage

@Composable
fun MoodMainRoute(
    modifier: Modifier = Modifier,
    onLogNewMood: () -> Unit,
    viewModel: MoodViewModel = viewModel()
) {
    val activeSection by viewModel.activeSection.collectAsState()
    val allMoodEntries by viewModel.allMoodEntries.collectAsState()
    val filteredMoodEntries by viewModel.filteredMoodEntries.collectAsState()
    val latestMood by viewModel.latestMood.collectAsState()
    val averageMood by viewModel.averageMood.collectAsState()
    val filteredAverageMood by viewModel.filteredAverageMood.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMoods()
    }

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
            selectedPage = activeSection,
            onPageSelected = { section ->
                viewModel.showSection(section)
            }
        )

        when (activeSection) {
            MoodSection.Overview -> {
                MoodOverviewPage(
                    allMoodEntries = allMoodEntries,
                    latestMood = latestMood,
                    averageMood = averageMood,
                    onLogMoodClick = onLogNewMood,
                    onViewHistoryClick = {
                        viewModel.showSection(MoodSection.History)
                    },
                    onViewInsightsClick = {
                        viewModel.showSection(MoodSection.Insights)
                    }
                )
            }

            MoodSection.History -> {
                MoodHistoryPage(
                    moodEntries = allMoodEntries,
                    filteredMoodEntries = filteredMoodEntries,
                    filteredAverageMood = filteredAverageMood,
                    filterState = filterState,
                    onFeelingFilterSelected = { filter ->
                        viewModel.setFeelingFilter(filter)
                    },
                    onDateFilterSelected = { filter ->
                        viewModel.setDateFilter(filter)
                    },
                    onClearFilters = {
                        viewModel.clearFilters()
                    },
                    onLogMoodClick = onLogNewMood,
                    onEditEntry = { entry, newMoodValue, newNote ->
                        viewModel.updateExistingMood(
                            moodEntry = entry,
                            newMoodValue = newMoodValue,
                            newNote = newNote
                        )
                    },
                    onDeleteEntry = { entry ->
                        viewModel.deleteMood(entry)
                    }
                )
            }

            MoodSection.Insights -> {
                MoodInsightsPage(
                    moodEntries = allMoodEntries,
                    latestMood = latestMood,
                    averageMood = averageMood
                )
            }

            MoodSection.Settings -> {
                MoodSettingsPage()
            }
        }
    }
}

@Composable
private fun MoodPageNavigation(
    selectedPage: MoodSection,
    onPageSelected: (MoodSection) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodPageButton(
                page = MoodSection.Overview,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            MoodPageButton(
                page = MoodSection.History,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodPageButton(
                page = MoodSection.Insights,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            MoodPageButton(
                page = MoodSection.Settings,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MoodPageButton(
    page: MoodSection,
    selectedPage: MoodSection,
    onPageSelected: (MoodSection) -> Unit,
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