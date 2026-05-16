package com.example.frontpage.sleep.ui.components

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
import com.example.frontpage.sleep.ui.SleepPage

@Composable
internal fun SleepPageNavigation(
    selectedPage: SleepPage,
    onPageSelected: (SleepPage) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepPageButton(
                page = SleepPage.Overview,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            SleepPageButton(
                page = SleepPage.History,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepPageButton(
                page = SleepPage.Insights,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )

            SleepPageButton(
                page = SleepPage.Settings,
                selectedPage = selectedPage,
                onPageSelected = onPageSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SleepPageButton(
    page: SleepPage,
    selectedPage: SleepPage,
    onPageSelected: (SleepPage) -> Unit,
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
