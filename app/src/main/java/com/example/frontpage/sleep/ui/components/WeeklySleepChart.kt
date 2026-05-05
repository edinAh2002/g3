package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.WeeklySleepChartItem

@Composable
fun WeeklySleepChart(
    chartData: List<WeeklySleepChartItem>,
    goalMinutes: Int,
    modifier: Modifier = Modifier
) {
    val maxDuration = maxOf(
        goalMinutes,
        chartData.maxOfOrNull { it.durationMinutes } ?: 0,
        1
    )

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
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.titleSmall
            )

            if (chartData.all { it.durationMinutes == 0 }) {
                Text(
                    text = "No sleep logged this week yet.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            chartData.forEach { item ->
                WeeklySleepChartRow(
                    item = item,
                    maxDuration = maxDuration
                )
            }
        }
    }
}

@Composable
fun WeeklySleepChartRow(
    item: WeeklySleepChartItem,
    maxDuration: Int
) {
    val progress = if (maxDuration > 0) {
        item.durationMinutes.toFloat() / maxDuration.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = item.dayLabel,
            modifier = Modifier.width(40.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        )

        Text(
            text = if (item.durationMinutes > 0) {
                SleepCalculator.formatDuration(item.durationMinutes)
            } else {
                "--"
            },
            modifier = Modifier.width(60.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}