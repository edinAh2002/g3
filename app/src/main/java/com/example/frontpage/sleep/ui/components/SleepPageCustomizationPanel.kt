package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageSectionId

@Composable
internal fun SleepPageCustomizationPanel(
    pageKey: SleepPageKey,
    layout: SleepPageLayout,
    onAddSection: (SleepPageSectionId) -> Unit,
    onResetLayout: () -> Unit
) {
    val availableSections = SleepPageSectionRegistry.sectionsFor(pageKey)
    val visibleSectionIds = layout.sectionIds.toSet()
    val hiddenSections = availableSections.filterNot { section ->
        section.id in visibleSectionIds
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Customize Page",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Add hidden sections here. Reorder or remove visible sections below.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(onClick = onResetLayout) {
                    Text("Reset")
                }
            }

            if (availableSections.isEmpty()) {
                Text(
                    text = "This page does not have configurable sections yet.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (hiddenSections.isEmpty()) {
                Text(
                    text = "Every available section is already shown.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                hiddenSections.forEach { section ->
                    SleepPageCustomizationAddRow(
                        section = section,
                        onAddSection = {
                            onAddSection(section.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SleepPageCustomizationAddRow(
    section: SleepPageSectionDefinition,
    onAddSection: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = section.description,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedButton(onClick = onAddSection) {
            Text("Add")
        }
    }
}
