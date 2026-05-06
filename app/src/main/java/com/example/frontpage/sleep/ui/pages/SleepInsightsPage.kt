package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepGoalBalance
import com.example.frontpage.sleep.domain.SleepMoodInsight
import com.example.frontpage.sleep.domain.SleepScoreSummary
import com.example.frontpage.sleep.domain.SleepStreakSummary
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepConsistencyCard
import com.example.frontpage.sleep.ui.components.SleepGoalBalanceCard
import com.example.frontpage.sleep.ui.components.SleepMoodInsightCard
import com.example.frontpage.sleep.ui.components.SleepRecommendationCard
import com.example.frontpage.sleep.ui.components.SleepScoreCard
import com.example.frontpage.sleep.ui.components.SleepStatCard
import com.example.frontpage.sleep.ui.components.SleepStreakCard
import com.example.frontpage.sleep.ui.components.SleepTrendsCard

@Composable
fun SleepInsightsPage(
    sleepLogs: List<SleepEntry>,
    averageSleepMinutes: Int,
    longestSleepMinutes: Int,
    shortestSleepMinutes: Int,
    averageBedtimeMinutes: Int?,
    averageWakeTimeMinutes: Int?,
    sleepConsistencyVariationMinutes: Int?,
    sleepDurationRangeMinutes: Int?,
    consistencyLogCount: Int,
    sleepScoreSummary: SleepScoreSummary?,
    sleepGoalBalance: SleepGoalBalance,
    streakSummary: SleepStreakSummary,
    primaryRecommendation: String,
    sleepMoodInsight: SleepMoodInsight?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Insights",
            style = MaterialTheme.typography.titleMedium
        )

        SleepRecommendationCard(
            recommendation = primaryRecommendation
        )

        SleepScoreCard(
            scoreSummary = sleepScoreSummary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepStatCard(
                title = "Average",
                value = SleepCalculator.formatDuration(averageSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepStatCard(
                title = "Longest",
                value = SleepCalculator.formatDuration(longestSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepStatCard(
                title = "Shortest",
                value = SleepCalculator.formatDuration(shortestSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepStatCard(
                title = "Logs",
                value = sleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        SleepGoalBalanceCard(
            goalBalance = sleepGoalBalance
        )

        SleepStreakCard(
            streakSummary = streakSummary
        )

        if (sleepMoodInsight != null) {
            SleepMoodInsightCard(
                sleepMoodInsight = sleepMoodInsight
            )
        }

        Text(
            text = "Bedtime & Wake-Up Trends",
            style = MaterialTheme.typography.titleMedium
        )

        SleepTrendsCard(
            sleepLogs = sleepLogs,
            averageBedtimeMinutes = averageBedtimeMinutes,
            averageWakeTimeMinutes = averageWakeTimeMinutes,
            averageDurationMinutes = averageSleepMinutes
        )

        Text(
            text = "Sleep Consistency - Last 7 Days",
            style = MaterialTheme.typography.titleMedium
        )

        SleepConsistencyCard(
            variationMinutes = sleepConsistencyVariationMinutes,
            durationRangeMinutes = sleepDurationRangeMinutes,
            logCount = consistencyLogCount
        )
    }
}
