package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset

@Composable
fun LatestMoodCard(
    latestMood: MoodEntry?,
    scalePreset: MoodScalePreset,
    onLogMoodClick: () -> Unit,
    onViewScaleClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Latest Mood",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(onClick = onViewScaleClick) {
                    Text("Scale")
                }
            }

            if (latestMood == null) {
                Text("No mood logged yet.")
                Text("Tap Log Mood to add your first mood entry.")

                Button(
                    onClick = onLogMoodClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Mood")
                }
            } else {
                Text(
                    text = "${MoodDateUtils.formatDisplayDate(latestMood.date)} at ${latestMood.time}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = MoodLabelUtils.getMoodLabel(latestMood.moodValue, scalePreset),
                    style = MaterialTheme.typography.headlineMedium
                )

                MoodDetailRow(
                    label = "Score",
                    value = "${latestMood.moodValue} / 5"
                )

                MoodDetailRow(
                    label = "Note",
                    value = if (latestMood.note.isBlank()) "No note added" else latestMood.note
                )

                LinearProgressIndicator(
                    progress = { latestMood.moodValue.toFloat() / 5f },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = MoodLabelUtils.getMoodDescription(latestMood.moodValue, scalePreset),
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = onLogMoodClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Mood")
                }
            }
        }
    }
}
