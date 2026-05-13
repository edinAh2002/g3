package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.model.MoodEntry

@Composable
internal fun MoodProgressCalendar(
    monthState: MoodCalendarMonthState,
    moodEntries: List<MoodEntry>,
    selectedDateMillis: Long,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (Long) -> Unit
) {
    val dayProgress = buildMonthMoodProgress(
        monthState = monthState,
        moodEntries = moodEntries
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onPreviousMonth) {
                    Text("Prev")
                }

                Text(
                    text = monthState.label(),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedButton(onClick = onNextMonth) {
                    Text("Next")
                }
            }

            MoodCalendarWeekHeader()

            dayProgress.chunked(7).forEach { week ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        MoodCalendarDayCell(
                            day = day,
                            isSelected = day?.dateMillis?.let { dateMillis ->
                                startOfMoodCalendarDayMillis(dateMillis) ==
                                        startOfMoodCalendarDayMillis(selectedDateMillis)
                            } == true,
                            onDaySelected = onDaySelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Text(
                text = "Filled days show average mood for the selected mood filter.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MoodCalendarWeekHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

