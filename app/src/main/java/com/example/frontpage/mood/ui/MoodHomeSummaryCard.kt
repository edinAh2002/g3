package com.example.frontpage.mood.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.domain.MoodLabelUtils

@Composable
fun MoodHomeSummaryCard(
    modifier: Modifier = Modifier,
    viewModel: MoodViewModel = viewModel()
) {
    val latestMood by viewModel.latestMood.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMoods()
    }

    val latestMoodText = latestMood?.let { moodEntry ->
        MoodLabelUtils.getMoodLabel(moodEntry.moodValue)
    } ?: "Not logged"

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mood")
            Text(
                text = latestMoodText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}