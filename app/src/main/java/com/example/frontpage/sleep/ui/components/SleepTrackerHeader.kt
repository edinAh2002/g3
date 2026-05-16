package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.ui.theme.SleepTheme

@Composable
internal fun SleepTrackerHeader(
    canCustomizeSelectedPage: Boolean,
    isEditingPage: Boolean,
    onToggleEdit: () -> Unit
) {
    val colors = SleepTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        colors.headerGradientStart,
                        colors.headerGradientEnd
                    )
                )
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sleep Tracker",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.onHeader
                )

                Text(
                    text = "Rest and recover",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onHeader.copy(alpha = 0.78f)
                )
            }

            if (canCustomizeSelectedPage) {
                OutlinedButton(
                    onClick = onToggleEdit,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.onHeader
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = colors.onHeader.copy(alpha = 0.52f)
                    )
                ) {
                    Text(if (isEditingPage) "Done" else "Edit")
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .width(88.dp)
                        .height(40.dp)
                )
            }
        }
    }
}
