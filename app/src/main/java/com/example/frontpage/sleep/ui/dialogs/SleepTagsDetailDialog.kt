package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepTag
import com.example.frontpage.sleep.model.SleepTagOption

@Composable
internal fun SleepTagsDetailDialog(
    initialTags: List<SleepTagOption>,
    customTags: List<SleepCustomTag>,
    onDismiss: () -> Unit,
    onSave: (List<SleepTagOption>) -> Unit
) {
    var draftTags by remember(initialTags) {
        mutableStateOf(initialTags)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Tags")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                SleepTagSelector(
                    selectedTags = draftTags,
                    customTags = customTags,
                    onTagToggled = { tag ->
                        draftTags = if (draftTags.any { selectedTag -> selectedTag.storageValue == tag.storageValue }) {
                            draftTags.filterNot { selectedTag -> selectedTag.storageValue == tag.storageValue }
                        } else {
                            draftTags + tag
                        }
                    },
                    showTitle = false
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftTags) }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SleepTagSelector(
    selectedTags: List<SleepTagOption>,
    customTags: List<SleepCustomTag>,
    onTagToggled: (SleepTagOption) -> Unit,
    showTitle: Boolean = true
) {
    val availableTags = SleepTag.builtInOptions() + customTags.map { customTag ->
        SleepTag.customOption(customTag)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showTitle) {
            Text(
                text = "Sleep Tags",
                style = MaterialTheme.typography.titleSmall
            )
        }

        if (showTitle) {
            Text(
                text = if (selectedTags.isEmpty()) {
                    "Optional context for patterns"
                } else {
                    "${selectedTags.size} selected"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }

        availableTags.groupBy { it.category }.forEach { (category, tags) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                tags.chunked(2).forEach { rowTags ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowTags.forEach { tag ->
                            SleepOptionButton(
                                text = tag.label,
                                selected = selectedTags.any { selectedTag ->
                                    selectedTag.storageValue == tag.storageValue
                                },
                                onClick = { onTagToggled(tag) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (rowTags.size == 1) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
