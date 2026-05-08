package com.example.frontpage.sleep.ui.components

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
import com.example.frontpage.sleep.domain.SleepGoalBalance
import com.example.frontpage.sleep.domain.SleepMoodInsight
import com.example.frontpage.sleep.domain.SleepScoreSummary
import com.example.frontpage.sleep.domain.SleepStreakSummary
import com.example.frontpage.sleep.domain.SleepTagInsight

@Composable
fun SleepScoreCard(
    scoreSummary: SleepScoreSummary?,
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
            Text("Sleep Score")

            if (scoreSummary == null) {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Log sleep to calculate your score.",
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
fun SleepRecommendationCard(
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
fun SleepGoalBalanceCard(
    goalBalance: SleepGoalBalance,
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
            Text("7-Day Goal Balance")
            Text(
                text = goalBalance.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = goalBalance.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SleepStreakCard(
    streakSummary: SleepStreakSummary,
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
                text = "${streakSummary.nearGoalStreak} near-goal days",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun SleepMoodInsightCard(
    sleepMoodInsight: SleepMoodInsight,
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
                text = "Sleep & Mood",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = sleepMoodInsight.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = sleepMoodInsight.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun SleepTagInsightCard(
    sleepTagInsight: SleepTagInsight,
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
                text = "Dreams, Snoring & Tags",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = sleepTagInsight.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = sleepTagInsight.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
