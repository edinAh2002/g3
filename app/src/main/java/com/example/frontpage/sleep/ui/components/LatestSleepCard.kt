package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepTag

@Composable
fun LatestSleepCard(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit,
    showTitle: Boolean = true
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
            if (showTitle) {
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
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onEditGoalClick) {
                        Text("Goal")
                    }
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

                GradientSleepProgressBar(
                    progress =
                        SleepCalculator.calculateGoalProgress(
                            durationMinutes = latestSleep.durationMinutes,
                            goalMinutes = goalMinutes
                        ),
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
