package com.example.frontpage.sleep.ui.cards

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

@Composable
fun SleepHomeSummaryCard(
    modifier: Modifier = Modifier,
    viewModel: SleepViewModel = viewModel()
) {
    val sleepLogs by viewModel.sleepLogs.collectAsState()

    val latestSleep = sleepLogs.lastOrNull()

    val sleepText = latestSleep?.let { entry ->
        SleepCalculator.formatDuration(entry.durationMinutes)
    } ?: "Not logged"

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sleep")
            Text(
                text = sleepText,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}