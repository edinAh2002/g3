package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.WeeklyMoodChartItem

@Composable
fun WeeklyMoodChart(
    chartData: List<WeeklyMoodChartItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 196.dp),
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

            if (chartData.all { item -> item.averageMood == null }) {
                Text(
                    text = "No mood logged this week yet.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            chartData.forEach { item ->
                WeeklyMoodChartRow(item = item)
            }
        }
    }
}

@Composable
fun WeeklyMoodChartRow(
    item: WeeklyMoodChartItem
) {
    val progress = ((item.averageMood ?: 0.0) / 5.0).toFloat()

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
            text = item.averageMood?.let { average ->
                MoodLabelUtils.formatMoodAverage(average)
            } ?: "--",
            modifier = Modifier.width(72.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

