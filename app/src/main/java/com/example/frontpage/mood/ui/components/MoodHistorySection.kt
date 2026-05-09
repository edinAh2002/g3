package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodDateUtils
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState
import com.example.frontpage.mood.model.MoodScalePreset

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoodHistoryCard(
    entry: MoodEntry,
    scalePreset: MoodScalePreset,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 168.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
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
                        text = MoodDateUtils.formatDisplayDate(entry.date),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = entry.time,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (isSelected) {
                Text(
                    text = "Selected",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            MoodDetailRow(
                label = "Score",
                value = "${entry.moodValue} / 5"
            )

            MoodDetailRow(
                label = "Day",
                value = MoodDateUtils.getDayNameFromDate(entry.date)
            )

            if (entry.note.isNotBlank()) {
                Text(
                    text = "Note: ${entry.note}",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "No note added.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (!selectionMode) {
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
}

@Composable
fun MoodHistoryFilterRow(
    filterState: MoodLogFilterState,
    onFeelingFilterSelected: (MoodFeelingFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    var showMoodFilterDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                showMoodFilterDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mood: ${filterState.feelingFilter.label}")
        }

        if (filterState.feelingFilter != MoodFeelingFilter.All) {
            OutlinedButton(
                onClick = onClearFilters,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Mood Filter")
            }
        }
    }

    if (showMoodFilterDialog) {
        AlertDialog(
            onDismissRequest = {
                showMoodFilterDialog = false
            },
            title = {
                Text("Choose Mood")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MoodFeelingFilter.entries.forEach { filter ->
                        MoodFeelingFilterButton(
                            filter = filter,
                            selectedFilter = filterState.feelingFilter,
                            onFilterSelected = { selectedFilter ->
                                onFeelingFilterSelected(selectedFilter)
                                showMoodFilterDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMoodFilterDialog = false
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun MoodFeelingFilterButton(
    filter: MoodFeelingFilter,
    selectedFilter: MoodFeelingFilter,
    onFilterSelected: (MoodFeelingFilter) -> Unit
) {
    val isSelected = filter == selectedFilter

    if (isSelected) {
        Button(
            onClick = { onFilterSelected(filter) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(filter.label)
        }
    } else {
        OutlinedButton(
            onClick = { onFilterSelected(filter) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(filter.label)
        }
    }
}
