package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.domain.SleepCalculator

@Composable
fun SleepTrendsCard(
    sleepLogs: List<SleepEntry>,
    averageBedtimeMinutes: Int?,
    averageWakeTimeMinutes: Int?,
    averageDurationMinutes: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (sleepLogs.isEmpty()) {
                Text(
                    text = "No trend data yet",
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "Log sleep for a few days to see your usual bedtime and wake-up time.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = SleepCalculator.getSleepTrendSummary(sleepLogs),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SleepTrendItem(
                        title = "Avg Bedtime",
                        value = averageBedtimeMinutes?.let {
                            SleepCalculator.formatClockMinutes(it)
                        } ?: "--",
                        modifier = Modifier.weight(1f)
                    )

                    SleepTrendItem(
                        title = "Avg Wake",
                        value = averageWakeTimeMinutes?.let {
                            SleepCalculator.formatClockMinutes(it)
                        } ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                }

                SleepTrendItem(
                    title = "Avg Duration",
                    value = SleepCalculator.formatDuration(averageDurationMinutes),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SleepTrendItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}