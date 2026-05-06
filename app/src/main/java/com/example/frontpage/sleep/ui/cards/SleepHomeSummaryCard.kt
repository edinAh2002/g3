package com.example.frontpage.sleep.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.sleep.SleepViewModel
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.buildSleepScoreSummary

@Composable
fun SleepHomeSummaryCard(
    modifier: Modifier = Modifier,
    viewModel: SleepViewModel = viewModel()
) {
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val goalMinutes by viewModel.goalMinutes.collectAsState()

    val latestSleep = sleepLogs.lastOrNull()
    val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
    val last7DaysSleepLogs = sleepLogs.filter { it.dateMillis >= sevenDaysAgo }

    val variationMinutes = SleepCalculator.calculateSleepConsistencyVariationMinutes(last7DaysSleepLogs)
    val durationRangeMinutes = SleepCalculator.calculateSleepDurationRangeMinutes(last7DaysSleepLogs)
    val sleepScoreSummary = buildSleepScoreSummary(
        latestSleep = latestSleep,
        goalMinutes = goalMinutes,
        consistencyVariationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    val sleepText = latestSleep?.let { entry ->
        SleepCalculator.formatDuration(entry.durationMinutes)
    } ?: "Not logged"

    val detailText = sleepScoreSummary?.let { scoreSummary ->
        "Score ${scoreSummary.score}/100"
    } ?: "Goal ${SleepCalculator.formatDuration(goalMinutes)}"

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Sleep")
            Text(
                text = sleepText,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = detailText,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
