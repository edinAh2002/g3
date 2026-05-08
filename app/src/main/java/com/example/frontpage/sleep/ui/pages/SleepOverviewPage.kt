package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepGoalBalance
import com.example.frontpage.sleep.domain.SleepScoreSummary
import com.example.frontpage.sleep.domain.SleepStreakSummary
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import com.example.frontpage.sleep.ui.components.SleepGoalBalanceCard
import com.example.frontpage.sleep.ui.components.LatestSleepCard
import com.example.frontpage.sleep.ui.components.SleepMetricTile
import com.example.frontpage.sleep.ui.components.SleepScoreCard
import com.example.frontpage.sleep.ui.components.SleepSectionHeader
import com.example.frontpage.sleep.ui.components.WeeklySleepChart

@Composable
fun SleepOverviewPage(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    averageSleepMinutes: Int,
    longestSleepMinutes: Int,
    shortestSleepMinutes: Int,
    totalLogs: Int,
    weeklyChartData: List<WeeklySleepChartItem>,
    sleepScoreSummary: SleepScoreSummary?,
    sleepGoalBalance: SleepGoalBalance,
    streakSummary: SleepStreakSummary,
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LatestSleepCard(
            latestSleep = latestSleep,
            goalMinutes = goalMinutes,
            onLogSleepClick = onLogSleepClick,
            onEditGoalClick = onEditGoalClick
        )

        SleepSectionHeader(
            title = "This Week",
            subtitle = "Score and goal balance use your current sleep goal."
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

        SleepSectionHeader(
            title = "Key Numbers",
            subtitle = "A quick scan of your sleep log."
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
                value = totalLogs.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Logged Streak",
                value = "${streakSummary.loggedDayStreak}d",
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Goal Streak",
                value = "${streakSummary.nearGoalStreak}d",
                modifier = Modifier.weight(1f)
            )
        }

        SleepSectionHeader(
            title = "Last 7 Days",
            subtitle = "Each bar shows total sleep by wake day."
        )

        WeeklySleepChart(
            chartData = weeklyChartData,
            goalMinutes = goalMinutes
        )

        SleepSectionHeader(
            title = "Records",
            subtitle = "Your longest and shortest saved sleep logs."
        )

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
    }
}
