package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator

@Composable
fun SleepSettingsPage(
    goalMinutes: Int,
    onEditGoalClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep Settings",
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Sleep Goal",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = SleepCalculator.formatDuration(goalMinutes),
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "This goal is used for your progress bar and feedback cards.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = onEditGoalClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Sleep Goal")
                }
            }
        }

    }
}