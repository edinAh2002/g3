package com.example.frontpage.mood.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.ui.components.MoodStatCard

@Composable
fun MoodOverviewPage(
    allMoodEntries: List<MoodEntry>,
    latestMood: MoodEntry?,
    averageMood: Double?,
    onLogMoodClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onViewInsightsClick: () -> Unit
) {
    val todayAverageMood = MoodStatsCalculator.getTodayAverageMood(allMoodEntries)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LatestMoodCard(
            latestMood = latestMood
        )

        Button(
            onClick = onLogMoodClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Mood")
        }

        Text(
            text = "Mood Statistics",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodStatCard(
                title = "Today Avg",
                value = todayAverageMood?.let {
                    "${String.format("%.1f", it)} / 5"
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )

            MoodStatCard(
                title = "Overall Avg",
                value = averageMood?.let {
                    "${String.format("%.1f", it)} / 5"
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodStatCard(
                title = "Logs",
                value = allMoodEntries.size.toString(),
                modifier = Modifier.weight(1f)
            )

            MoodStatCard(
                title = "Best",
                value = MoodStatsCalculator.getBestMood(allMoodEntries)?.let {
                    MoodLabelUtils.getMoodLabel(it.moodValue)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onViewHistoryClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("View History")
            }

            OutlinedButton(
                onClick = onViewInsightsClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("View Insights")
            }
        }
    }
}

@Composable
private fun LatestMoodCard(
    latestMood: MoodEntry?
) {
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
            Text("Latest Mood", style = MaterialTheme.typography.titleMedium)

            if (latestMood == null) {
                Text("No mood logged yet.")
                Text("Tap Log Mood to add your first mood entry.")
            } else {
                Text(
                    text = "${latestMood.date} at ${latestMood.time}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = MoodLabelUtils.getMoodLabel(latestMood.moodValue),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = if (latestMood.note.isBlank()) {
                        "No note added."
                    } else {
                        latestMood.note
                    }
                )
            }
        }
    }
}