package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.domain.SleepGoalBalance
import com.example.frontpage.sleep.domain.SleepScoreSummary
import com.example.frontpage.sleep.domain.SleepStreakSummary
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepTag
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import com.example.frontpage.sleep.ui.components.SleepDetailRow
import com.example.frontpage.sleep.ui.components.SleepGoalBalanceCard
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

@Composable
private fun LatestSleepCard(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Latest Sleep",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(onClick = onEditGoalClick) {
                    Text("Goal")
                }
            }

            if (latestSleep == null) {
                Text("No sleep logged yet.")
                Text("Tap Log Sleep to add your first sleep entry.")

                Button(
                    onClick = onLogSleepClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Sleep")
                }
            } else {
                Text(
                    text = SleepDateUtils.formatHistoryDate(latestSleep.dateMillis),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = SleepCalculator.formatDuration(latestSleep.durationMinutes),
                    style = MaterialTheme.typography.headlineMedium
                )

                SleepDetailRow(
                    label = "Time",
                    value = "${SleepCalculator.formatTime(latestSleep.sleepHour, latestSleep.sleepMinute)} to ${SleepCalculator.formatTime(latestSleep.wakeHour, latestSleep.wakeMinute)}"
                )

                SleepDetailRow(
                    label = "Quality",
                    value = latestSleep.quality.toString()
                )

                SleepDetailRow(
                    label = "Source",
                    value = latestSleep.source.label
                )

                if (latestSleep.snoringLevel.name != "None") {
                    SleepDetailRow(
                        label = "Snoring",
                        value = latestSleep.snoringLevel.label
                    )
                }

                val tags = SleepTag.optionsFromStorage(latestSleep.tags)
                if (tags.isNotEmpty()) {
                    Text("Tags: ${tags.take(3).joinToString { it.label }}")
                }

                LinearProgressIndicator(
                    progress = {
                        SleepCalculator.calculateGoalProgress(
                            durationMinutes = latestSleep.durationMinutes,
                            goalMinutes = goalMinutes
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Goal: ${SleepCalculator.formatDuration(goalMinutes)}")

                Text(
                    text = SleepCalculator.getGoalStatusTitle(
                        durationMinutes = latestSleep.durationMinutes,
                        goalMinutes = goalMinutes
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = SleepCalculator.getGoalDifferenceText(
                        durationMinutes = latestSleep.durationMinutes,
                        goalMinutes = goalMinutes
                    ),
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = onLogSleepClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Sleep")
                }
            }
        }
    }
}
