package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.frontpage.sleep.domain.SleepTagInsight
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepConsistencyCard
import com.example.frontpage.sleep.ui.components.SleepGoalBalanceCard
import com.example.frontpage.sleep.ui.components.SleepMetricTile
import com.example.frontpage.sleep.ui.components.SleepMoodInsightCard
import com.example.frontpage.sleep.ui.components.SleepRecommendationCard
import com.example.frontpage.sleep.ui.components.SleepScoreCard
import com.example.frontpage.sleep.ui.components.SleepSectionHeader
import com.example.frontpage.sleep.ui.components.SleepStreakCard
import com.example.frontpage.sleep.ui.components.SleepTagInsightCard
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
    sleepMoodInsight: SleepMoodInsight?,
    sleepTagInsight: SleepTagInsight?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SleepSectionHeader(
            title = "Sleep Insights",
            subtitle = "The useful patterns first, then the details."
        )

        SleepRecommendationCard(
            recommendation = primaryRecommendation
        )

        SleepSectionHeader(
            title = "Performance",
            subtitle = "How your recent sleep compares with your goals."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            SleepScoreCard(
                scoreSummary = sleepScoreSummary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            SleepGoalBalanceCard(
                goalBalance = sleepGoalBalance,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        SleepStreakCard(
            streakSummary = streakSummary
        )

        SleepSectionHeader(
            title = "Snapshot",
            subtitle = "Basic numbers from all saved sleep logs."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Average",
                value = SleepCalculator.formatDuration(averageSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Logs",
                value = sleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Longest",
                value = SleepCalculator.formatDuration(longestSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Shortest",
                value = SleepCalculator.formatDuration(shortestSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        SleepSectionHeader(
            title = "Patterns",
            subtitle = "Timing and consistency trends from your sleep routine."
        )

        SleepTrendsCard(
            sleepLogs = sleepLogs,
            averageBedtimeMinutes = averageBedtimeMinutes,
            averageWakeTimeMinutes = averageWakeTimeMinutes,
            averageDurationMinutes = averageSleepMinutes
        )

        SleepConsistencyCard(
            variationMinutes = sleepConsistencyVariationMinutes,
            durationRangeMinutes = sleepDurationRangeMinutes,
            logCount = consistencyLogCount
        )

        if (sleepMoodInsight != null || sleepTagInsight != null) {
            SleepSectionHeader(
                title = "Connections",
                subtitle = "Mood, tags, snoring, dreams, and other context."
            )

            if (sleepMoodInsight != null) {
                SleepMoodInsightCard(
                    sleepMoodInsight = sleepMoodInsight
                )
            }

            if (sleepTagInsight != null) {
                SleepTagInsightCard(
                    sleepTagInsight = sleepTagInsight
                )
            }
        }
    }
}
