package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.model.SleepTag

@Composable
fun SleepHistoryCard(
    entry: SleepEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp),
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = SleepDateUtils.formatHistoryDate(entry.dateMillis),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${SleepCalculator.formatTime(entry.sleepHour, entry.sleepMinute)} to ${SleepCalculator.formatTime(entry.wakeHour, entry.wakeMinute)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = SleepCalculator.formatDuration(entry.durationMinutes),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            SleepDetailRow(
                label = "Quality",
                value = entry.quality.toString()
            )

            SleepDetailRow(
                label = "Source",
                value = entry.source.label
            )

            SleepDetailRow(
                label = "Snoring",
                value = entry.snoringLevel.label
            )

            val tags = SleepTag.optionsFromStorage(entry.tags)
            if (tags.isNotEmpty()) {
                Text(
                    text = "Tags: ${tags.take(4).joinToString { it.label }}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (entry.dreamJournal.isNotBlank()) {
                Text(
                    text = "Dreams: ${entry.dreamJournal}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (entry.notes.isNotBlank()) {
                Text(
                    text = "Notes: ${entry.notes}",
                    style = MaterialTheme.typography.bodySmall
                )
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
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
            horizontalArrangement = Arrangement.spacedBy(6.dp),
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
