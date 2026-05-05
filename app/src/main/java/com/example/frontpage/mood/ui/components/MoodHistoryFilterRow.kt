package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.model.MoodDateFilter
import com.example.frontpage.mood.model.MoodFeelingFilter
import com.example.frontpage.mood.model.MoodLogFilterState

@Composable
fun MoodHistoryFilterRow(
    filterState: MoodLogFilterState,
    onFeelingFilterSelected: (MoodFeelingFilter) -> Unit,
    onDateFilterSelected: (MoodDateFilter) -> Unit,
    onClearFilters: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodDateFilterButton(
                filter = MoodDateFilter.All,
                selectedFilter = filterState.dateFilter,
                onFilterSelected = onDateFilterSelected,
                modifier = Modifier.weight(1f)
            )

            MoodDateFilterButton(
                filter = MoodDateFilter.Today,
                selectedFilter = filterState.dateFilter,
                onFilterSelected = onDateFilterSelected,
                modifier = Modifier.weight(1f)
            )

            MoodDateFilterButton(
                filter = MoodDateFilter.ThisWeek,
                selectedFilter = filterState.dateFilter,
                onFilterSelected = onDateFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodFeelingFilterButton(
                filter = MoodFeelingFilter.All,
                selectedFilter = filterState.feelingFilter,
                onFilterSelected = onFeelingFilterSelected,
                modifier = Modifier.weight(1f)
            )

            MoodFeelingFilterButton(
                filter = MoodFeelingFilter.Bad,
                selectedFilter = filterState.feelingFilter,
                onFilterSelected = onFeelingFilterSelected,
                modifier = Modifier.weight(1f)
            )

            MoodFeelingFilterButton(
                filter = MoodFeelingFilter.Good,
                selectedFilter = filterState.feelingFilter,
                onFilterSelected = onFeelingFilterSelected,
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedButton(
            onClick = onClearFilters,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear Filters")
        }
    }
}

@Composable
private fun MoodDateFilterButton(
    filter: MoodDateFilter,
    selectedFilter: MoodDateFilter,
    onFilterSelected: (MoodDateFilter) -> Unit,
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

@Composable
private fun MoodFeelingFilterButton(
    filter: MoodFeelingFilter,
    selectedFilter: MoodFeelingFilter,
    onFilterSelected: (MoodFeelingFilter) -> Unit,
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