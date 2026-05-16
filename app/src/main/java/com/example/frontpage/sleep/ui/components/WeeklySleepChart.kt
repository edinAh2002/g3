package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import com.example.frontpage.sleep.ui.theme.SleepTheme
import com.example.frontpage.sleep.ui.theme.sleepCardColors

@Composable
fun WeeklySleepChart(
    chartData: List<WeeklySleepChartItem>,
    goalMinutes: Int,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true
) {
    val maxDuration = maxOf(
        goalMinutes,
        chartData.maxOfOrNull { it.durationMinutes } ?: 0,
        1
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 196.dp),
        colors = sleepCardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showTitle) {
                Text(
                    text = "Last 7 Days",
                    style = MaterialTheme.typography.titleSmall
                )
            }

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

        GradientSleepProgressBar(
            progress = progress,
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

@Composable
fun GradientSleepProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    val clampedProgress = progress.coerceIn(0f, 1f)
    val colors = SleepTheme.colors

    Box(
        modifier = modifier
            .height(8.dp)
            .clip(shape)
            .background(colors.progressTrack)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clampedProgress)
                .height(8.dp)
                .clip(shape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colors.negative,
                            colors.warning,
                            colors.positive
                        )
                    )
                )
        )
    }
}
