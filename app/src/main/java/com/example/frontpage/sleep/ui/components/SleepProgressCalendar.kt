package com.example.frontpage.sleep.ui.components

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
import com.example.frontpage.sleep.model.SleepEntry

@Composable
internal fun SleepProgressCalendar(
    monthState: SleepCalendarMonthState,
    sleepLogs: List<SleepEntry>,
    selectedDateMillis: Long,
    goalMinutesForDate: (Long) -> Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDaySelected: (Long) -> Unit
) {
    val dayProgress = buildMonthProgress(
        monthState = monthState,
        sleepLogs = sleepLogs,
        goalMinutesForDate = goalMinutesForDate
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

            CalendarWeekHeader()

            dayProgress.chunked(7).forEach { week ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        SleepCalendarDayCell(
                            day = day,
                            isSelected = day?.dateMillis?.let { dateMillis ->
                                startOfSleepCalendarDayMillis(dateMillis) ==
                                        startOfSleepCalendarDayMillis(selectedDateMillis)
                            } == true,
                            onDaySelected = onDaySelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Text(
                text = "Filled days show progress toward that day's sleep goal.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
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
