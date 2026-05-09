package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.domain.MoodMomentumSummary
import com.example.frontpage.mood.domain.MoodNoteInsight
import com.example.frontpage.mood.domain.MoodPatternInsight
import com.example.frontpage.mood.domain.MoodScoreSummary
import com.example.frontpage.mood.domain.MoodStreakSummary

@Composable
fun MoodScoreCard(
    scoreSummary: MoodScoreSummary?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Mood Score")

            if (scoreSummary == null) {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Log mood to calculate your score.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "${scoreSummary.score}/100",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = scoreSummary.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = scoreSummary.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MoodRecommendationCard(
    recommendation: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Main Recommendation",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = recommendation,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MoodMomentumCard(
    moodMomentumSummary: MoodMomentumSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("7-Day Momentum")
            Text(
                text = moodMomentumSummary.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Change: ${MoodLabelUtils.formatMoodChange(moodMomentumSummary.change)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = moodMomentumSummary.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MoodStreakCard(
    streakSummary: MoodStreakSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Streaks")

            Text(
                text = "${streakSummary.loggedDayStreak} logged days",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${streakSummary.positiveMoodStreak} positive days",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MoodPatternInsightCard(
    moodPatternInsight: MoodPatternInsight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Mood Pattern",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = moodPatternInsight.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = moodPatternInsight.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun MoodNoteInsightCard(
    moodNoteInsight: MoodNoteInsight,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 124.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = moodNoteInsight.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = moodNoteInsight.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

