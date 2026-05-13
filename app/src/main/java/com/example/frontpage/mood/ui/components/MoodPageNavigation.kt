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
import com.example.frontpage.mood.ui.MoodPage

@Composable
internal fun MoodPageNavigation(
    selectedPage: MoodPage,
    onPageSelected: (MoodPage) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodPageButton(
                page = MoodPage.Overview,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            MoodPageButton(
                page = MoodPage.History,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodPageButton(
                page = MoodPage.Insights,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            MoodPageButton(
                page = MoodPage.Settings,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MoodPageButton(
    page: MoodPage,
    selectedPage: MoodPage,
    onPageSelected: (MoodPage) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = page == selectedPage

    if (isSelected) {
        Button(
            onClick = { onPageSelected(page) },
            modifier = modifier
        ) {
            Text(page.label)
        }
    } else {
        OutlinedButton(
            onClick = { onPageSelected(page) },
            modifier = modifier
        ) {
            Text(page.label)
        }
    }
}

