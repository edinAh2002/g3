package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SleepSectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    editControls: SleepSectionEditControls? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (editControls != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.End
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = editControls.onMoveUp,
                    enabled = editControls.canMoveUp
                ) {
                    Text("↑")
                }

                OutlinedButton(
                    onClick = editControls.onMoveDown,
                    enabled = editControls.canMoveDown
                ) {
                    Text("↓")
                }

                OutlinedButton(onClick = editControls.onRemove) {
                    Text("Remove")
                }
            }
        }
    }
}
