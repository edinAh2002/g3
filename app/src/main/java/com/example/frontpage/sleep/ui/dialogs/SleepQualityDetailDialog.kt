package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.SleepQuality

@Composable
internal fun SleepQualityDetailDialog(
    initialQuality: SleepQuality,
    onDismiss: () -> Unit,
    onSave: (SleepQuality) -> Unit
) {
    var draftQuality by remember(initialQuality) {
        mutableStateOf(initialQuality)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Quality")
        },
        text = {
            QualityButtons(
                selectedQuality = draftQuality,
                onQualitySelected = { draftQuality = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(draftQuality) }
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
private fun QualityButtons(
    selectedQuality: SleepQuality,
    onQualitySelected: (SleepQuality) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepOptionButton(
                text = "Poor",
                selected = selectedQuality == SleepQuality.Poor,
                onClick = { onQualitySelected(SleepQuality.Poor) },
                modifier = Modifier.weight(1f)
            )

            SleepOptionButton(
                text = "Okay",
                selected = selectedQuality == SleepQuality.Okay,
                onClick = { onQualitySelected(SleepQuality.Okay) },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepOptionButton(
                text = "Good",
                selected = selectedQuality == SleepQuality.Good,
                onClick = { onQualitySelected(SleepQuality.Good) },
                modifier = Modifier.weight(1f)
            )

            SleepOptionButton(
                text = "Great",
                selected = selectedQuality == SleepQuality.Great,
                onClick = { onQualitySelected(SleepQuality.Great) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
