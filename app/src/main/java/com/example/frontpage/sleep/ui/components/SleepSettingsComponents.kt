package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.model.HealthConnectAvailability
import com.example.frontpage.sleep.model.SleepDefaults
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

@Composable
internal fun SettingsSummaryCard(
    title: String,
    value: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 116.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
internal fun SettingsEditRow(
    title: String,
    value: String,
    editText: String = "Edit",
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(title)
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedButton(onClick = onEdit) {
            Text(editText)
        }
    }
}

internal fun settingsOrDefaults(
    weekdaySettings: List<WeekdaySleepSettings>
): List<WeekdaySleepSettings> {
    if (weekdaySettings.isNotEmpty()) return weekdaySettings

    return SleepWeekday.entries.map { weekday ->
        WeekdaySleepSettings(
            weekday = weekday,
            goalMinutes = SleepDefaults.SLEEP_GOAL_MINUTES,
            bedtimeMinutes = SleepDefaults.BEDTIME_MINUTES,
            wakeMinutes = SleepDefaults.WAKE_MINUTES
        )
    }
}

internal fun healthConnectSummary(
    healthConnectState: SleepHealthConnectState
): String {
    return when {
        healthConnectState.availability == HealthConnectAvailability.Unavailable ->
            "Health Connect is not available on this device."

        healthConnectState.availability == HealthConnectAvailability.ProviderUpdateRequired ->
            "Install or update Health Connect, then come back to import wearable sleep."

        healthConnectState.hasSleepPermission ->
            "Sleep read access is granted."

        else ->
            "Grant sleep read access to import wearable sessions."
    }
}
