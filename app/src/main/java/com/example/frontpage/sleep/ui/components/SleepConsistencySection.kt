package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator

@Composable
fun SleepConsistencyCard(
    variationMinutes: Int?,
    durationRangeMinutes: Int?,
    logCount: Int,
    modifier: Modifier = Modifier
) {
    val rating = SleepCalculator.getSleepConsistencyRating(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    val description = SleepCalculator.getSleepConsistencyDescription(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    val progress = SleepCalculator.calculateSleepConsistencyProgress(
        variationMinutes = variationMinutes,
        durationRangeMinutes = durationRangeMinutes
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = rating,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Based on $logCount logs from the last 7 days",
                style = MaterialTheme.typography.bodySmall
            )

            if (variationMinutes != null && durationRangeMinutes != null) {
                Text(
                    text = "Average consistency variation: ${SleepCalculator.formatDuration(variationMinutes)}"
                )

                Text(
                    text = "Sleep duration changed by up to ${SleepCalculator.formatDuration(durationRangeMinutes)} between logs."
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}