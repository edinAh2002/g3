package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import com.example.frontpage.sleep.ui.components.SleepFeedbackCard
import com.example.frontpage.sleep.ui.components.SleepStatCard
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
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LatestSleepCard(
            latestSleep = latestSleep,
            goalMinutes = goalMinutes,
            onEditGoalClick = onEditGoalClick
        )

        Button(
            onClick = onLogSleepClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Sleep")
        }

        Text(
            text = "Sleep Statistics",
            style = MaterialTheme.typography.titleMedium
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
                title = "Logs",
                value = totalLogs.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepStatCard(
                title = "Longest",
                value = SleepCalculator.formatDuration(longestSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepStatCard(
                title = "Shortest",
                value = SleepCalculator.formatDuration(shortestSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Weekly Sleep Chart",
            style = MaterialTheme.typography.titleMedium
        )

        WeeklySleepChart(
            chartData = weeklyChartData,
            goalMinutes = goalMinutes
        )
    }
}

@Composable
private fun LatestSleepCard(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    onEditGoalClick: () -> Unit
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
            Text("Latest Sleep", style = MaterialTheme.typography.titleMedium)

            if (latestSleep == null) {
                Text("No sleep logged yet.")
                Text("Tap Log Sleep to add your first sleep entry.")

            } else {
                Text(
                    text = SleepDateUtils.formatHistoryDate(latestSleep.dateMillis),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = SleepCalculator.formatDuration(latestSleep.durationMinutes),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "From ${SleepCalculator.formatTime(latestSleep.sleepHour, latestSleep.sleepMinute)} to ${SleepCalculator.formatTime(latestSleep.wakeHour, latestSleep.wakeMinute)}"
                )

                Text("Quality: ${latestSleep.quality}")

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

                SleepFeedbackCard(
                    durationMinutes = latestSleep.durationMinutes,
                    goalMinutes = goalMinutes
                )
            }
        }
    }
}