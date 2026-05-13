package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.ui.components.SettingsEditRow

@Composable
internal fun CustomTagsDialog(
    customTags: List<SleepCustomTag>,
    newCustomTagLabel: String,
    onNewCustomTagLabelChange: (String) -> Unit,
    onAddCustomTag: () -> Unit,
    onDeleteCustomTag: (SleepCustomTag) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Custom Tags")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Custom tags appear under Custom when logging sleep.",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCustomTagLabel,
                        onValueChange = onNewCustomTagLabelChange,
                        label = { Text("New tag") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedButton(
                        onClick = onAddCustomTag,
                        enabled = newCustomTagLabel.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }

                if (customTags.isEmpty()) {
                    Text("No custom tags yet.")
                } else {
                    customTags.forEach { tag ->
                        SettingsEditRow(
                            title = tag.label,
                            value = "Custom",
                            editText = "Delete",
                            onEdit = {
                                onDeleteCustomTag(tag)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
internal fun DeleteCustomTagDialog(
    tag: SleepCustomTag,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete custom tag?")
        },
        text = {
            Text("The tag will disappear from future selection. Old logs keep showing \"${tag.label}\".")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmDelete
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
