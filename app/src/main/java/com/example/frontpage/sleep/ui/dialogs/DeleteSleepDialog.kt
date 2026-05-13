package com.example.frontpage.sleep.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry

@Composable
internal fun DeleteSleepDialog(
    entry: SleepEntry,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete Sleep Log?")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Are you sure you want to delete this sleep log?")
                Text(SleepDateUtils.formatHistoryDate(entry.dateMillis))
                Text(
                    "${SleepCalculator.formatTime(entry.sleepHour, entry.sleepMinute)} to " +
                            SleepCalculator.formatTime(entry.wakeHour, entry.wakeMinute)
                )
                Text("Duration: ${SleepCalculator.formatDuration(entry.durationMinutes)}")
                Text("Quality: ${entry.quality}")
            }
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
