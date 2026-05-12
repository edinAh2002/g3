package com.example.frontpage.mood.ui.cards

import androidx.compose.foundation.layout.Arrangement
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
    val averageMood by viewModel.averageMood.collectAsState()
    val defaultScalePreset by viewModel.defaultScalePreset.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshCurrentUser()
    }

    val latestMoodText = latestMood?.let { moodEntry ->
        MoodLabelUtils.getMoodLabel(
            moodValue = moodEntry.moodValue,
            preset = defaultScalePreset
        )
    } ?: "Not logged"

    val detailText = averageMood?.let { average ->
        "Average ${MoodLabelUtils.formatMoodAverage(average)}"
    } ?: "Track how you feel"

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("com/example/frontpage/reminders/notifiers")
            Text(
                text = latestMoodText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = detailText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
