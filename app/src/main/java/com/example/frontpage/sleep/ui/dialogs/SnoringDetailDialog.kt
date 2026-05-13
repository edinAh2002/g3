package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.frontpage.sleep.model.SnoringLevel

@Composable
internal fun SnoringDetailDialog(
    initialSnoringLevel: SnoringLevel,
    onDismiss: () -> Unit,
    onSave: (SnoringLevel) -> Unit
) {
    var draftSnoringLevel by remember(initialSnoringLevel) {
        mutableStateOf(initialSnoringLevel)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Snoring")
        },
        text = {
            SnoringSelector(
                selectedSnoringLevel = draftSnoringLevel,
                onSnoringLevelSelected = { draftSnoringLevel = it },
                showTitle = false
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftSnoringLevel) }
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
private fun SnoringSelector(
    selectedSnoringLevel: SnoringLevel,
    onSnoringLevelSelected: (SnoringLevel) -> Unit,
    showTitle: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showTitle) {
            Text(
                text = "Snoring",
                style = MaterialTheme.typography.titleSmall
            )
        }

        SnoringLevel.entries.chunked(2).forEach { rowLevels ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowLevels.forEach { snoringLevel ->
                    SleepOptionButton(
                        text = snoringLevel.label,
                        selected = selectedSnoringLevel == snoringLevel,
                        onClick = { onSnoringLevelSelected(snoringLevel) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
