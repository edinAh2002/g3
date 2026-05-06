package com.example.frontpage.mood.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.domain.MoodStatsCalculator
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.ui.components.MoodStatCard

@Composable
fun MoodInsightsPage(
    moodEntries: List<MoodEntry>,
    latestMood: MoodEntry?,
    averageMood: Double?
) {
    val bestMood = MoodStatsCalculator.getBestMood(moodEntries)
    val lowestMood = MoodStatsCalculator.getLowestMood(moodEntries)
    val todayAverage = MoodStatsCalculator.getTodayAverageMood(moodEntries)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood Insights",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodStatCard(
                title = "Average",
                value = averageMood?.let {
                    "${String.format("%.1f", it)} / 5"
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )

            MoodStatCard(
                title = "Logs",
                value = moodEntries.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodStatCard(
                title = "Best",
                value = bestMood?.let {
                    MoodLabelUtils.getMoodLabel(it.moodValue)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )

            MoodStatCard(
                title = "Lowest",
                value = lowestMood?.let {
                    MoodLabelUtils.getMoodLabel(it.moodValue)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Mood Trends",
            style = MaterialTheme.typography.titleMedium
        )

        MoodStatCard(
            title = "Latest Mood",
            value = latestMood?.let {
                "${MoodLabelUtils.getMoodLabel(it.moodValue)} · ${it.date}"
            } ?: "Not logged yet",
            modifier = Modifier.fillMaxWidth()
        )

        MoodStatCard(
            title = "Today's Average",
            value = todayAverage?.let {
                "${String.format("%.1f", it)} / 5"
            } ?: "No moods logged today",
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Coming Soon",
            style = MaterialTheme.typography.titleMedium
        )

        MoodStatCard(
            title = "Mood Analysis",
            value = "Soon this page can connect mood with sleep, calories, and workouts.",
            modifier = Modifier.fillMaxWidth()
        )
    }
}