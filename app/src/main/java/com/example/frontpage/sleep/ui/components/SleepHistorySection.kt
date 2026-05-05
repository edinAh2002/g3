package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.domain.SleepCalculator

@Composable
fun SleepHistoryCard(
    entry: SleepEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = SleepDateUtils.formatHistoryDate(entry.dateMillis),
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = SleepCalculator.formatDuration(entry.durationMinutes),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "${SleepCalculator.formatTime(entry.sleepHour, entry.sleepMinute)} → ${SleepCalculator.formatTime(entry.wakeHour, entry.wakeMinute)}"
            )

            Text("Quality: ${entry.quality}")

            if (entry.notes.isNotBlank()) {
                Text("Notes: ${entry.notes}")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
fun SleepHistoryFilterRow(
    selectedFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.All,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )

            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.Today,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.ThisWeek,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )

            SleepHistoryFilterButton(
                filter = SleepHistoryFilter.ThisMonth,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SleepHistoryFilterButton(
    filter: SleepHistoryFilter,
    selectedFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = filter == selectedFilter

    if (isSelected) {
        Button(
            onClick = { onFilterSelected(filter) },
            modifier = modifier
        ) {
            Text(filter.label)
        }
    } else {
        OutlinedButton(
            onClick = { onFilterSelected(filter) },
            modifier = modifier
        ) {
            Text(filter.label)
        }
    }
}