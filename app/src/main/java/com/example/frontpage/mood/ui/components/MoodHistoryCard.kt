package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodEntry

@Composable
fun MoodHistoryCard(
    entry: MoodEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${entry.date} at ${entry.time}",
                style = MaterialTheme.typography.titleMedium
            )

            Text("Mood: ${MoodLabelUtils.getMoodLabel(entry.moodValue)}")

            Text(
                text = if (entry.note.isBlank()) {
                    "No note added"
                } else {
                    "Note: ${entry.note}"
                }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}