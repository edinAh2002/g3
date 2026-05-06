package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHistoryFilter
import com.example.frontpage.sleep.ui.components.SleepHistoryCard
import com.example.frontpage.sleep.ui.components.SleepHistoryFilterRow
import androidx.compose.ui.unit.*

@Composable
fun SleepHistoryPage(
    sleepLogs: List<SleepEntry>,
    filteredSleepLogs: List<SleepEntry>,
    selectedHistoryFilter: SleepHistoryFilter,
    onFilterSelected: (SleepHistoryFilter) -> Unit,
    onEditEntry: (SleepEntry) -> Unit,
    onDeleteEntry: (SleepEntry) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sleep History",
            style = MaterialTheme.typography.titleMedium
        )

        SleepHistoryFilterRow(
            selectedFilter = selectedHistoryFilter,
            onFilterSelected = onFilterSelected
        )

        if (sleepLogs.isEmpty()) {
            Text("Your sleep history will appear here.")
        } else if (filteredSleepLogs.isEmpty()) {
            Text("No sleep logs found for this filter.")
        } else {
            filteredSleepLogs.reversed().forEach { entry ->
                SleepHistoryCard(
                    entry = entry,
                    onEdit = {
                        onEditEntry(entry)
                    },
                    onDelete = {
                        onDeleteEntry(entry)
                    }
                )
            }
        }
    }
}