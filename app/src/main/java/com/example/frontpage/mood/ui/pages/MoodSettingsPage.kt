package com.example.frontpage.mood.ui.pages

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

@Composable
fun MoodSettingsPage() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mood Settings",
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
                    text = "Mood Scale",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "1 to 5",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "1 means very bad, while 5 means great. This scale is used for mood averages and insights.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Mood Scale Coming Soon")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Mood Data",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Mood logs are saved locally on this device.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Data Options Coming Soon")
                }
            }
        }
    }
}