package com.example.frontpage.mood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
internal fun MoodCalendarDayCell(
    day: CalendarDayMood?,
    isSelected: Boolean,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    val colorScheme = MaterialTheme.colorScheme
    val progress = day?.progress?.coerceIn(0f, 1f) ?: 0f
    val fillColor = moodProgressColor(progress)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(colorScheme.surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colorScheme.primary else colorScheme.outlineVariant,
                shape = shape
            )
            .then(
                if (day == null) {
                    Modifier
                } else {
                    Modifier.clickable {
                        onDaySelected(day.dateMillis)
                    }
                }
            )
    ) {
        if (progress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(progress)
                    .align(Alignment.BottomCenter)
                    .background(fillColor)
            )
        }

        Text(
            text = day?.dayOfMonth?.toString().orEmpty(),
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun moodProgressColor(progress: Float): Color {
    val red = Color(0xFFE57373)
    val yellow = Color(0xFFFFD54F)
    val green = Color(0xFF43A047)
    val clampedProgress = progress.coerceIn(0f, 1f)

    return when {
        progress <= 0f -> Color.Transparent
        clampedProgress < 0.5f -> lerp(
            start = red,
            stop = yellow,
            fraction = clampedProgress / 0.5f
        )

        else -> lerp(
            start = yellow,
            stop = green,
            fraction = (clampedProgress - 0.5f) / 0.5f
        )
    }
}

