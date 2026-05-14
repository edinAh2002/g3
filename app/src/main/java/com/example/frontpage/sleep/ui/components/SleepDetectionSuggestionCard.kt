package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.frontpage.sleep.model.SleepDetectionCandidate
import java.util.Calendar

@Composable
fun SleepDetectionSuggestionCard(
    candidate: SleepDetectionCandidate,
    onReview: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Detected Sleep",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${formatTime(candidate.startMillis)} to ${formatTime(candidate.endMillis)} - ${formatDuration(candidate)}",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${SleepDateUtils.formatHistoryDate(candidate.wakeDateMillis)} - ${candidate.confidence}% confidence",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = candidate.signalSummary,
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onReview,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Review")
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dismiss")
                }
            }
        }
    }
}

private fun formatDuration(candidate: SleepDetectionCandidate): String {
    val durationMinutes = ((candidate.endMillis - candidate.startMillis) / 60000L)
        .toInt()
        .coerceAtLeast(0)
    return SleepCalculator.formatDuration(durationMinutes)
}

private fun formatTime(timeMillis: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
    }

    return SleepCalculator.formatTime(
        hour = calendar.get(Calendar.HOUR_OF_DAY),
        minute = calendar.get(Calendar.MINUTE)
    )
}
