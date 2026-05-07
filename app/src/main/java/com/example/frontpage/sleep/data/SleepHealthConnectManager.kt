package com.example.frontpage.sleep.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.HealthConnectAvailability
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHealthConnectState
import com.example.frontpage.sleep.model.SleepQuality
import com.example.frontpage.sleep.model.SleepSource
import java.time.Duration
import java.time.Instant
import java.util.Calendar

class SleepHealthConnectManager(
    private val context: Context
) {
    private val healthConnectClient: HealthConnectClient?
        get() {
            return if (getAvailability() == HealthConnectAvailability.Available) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        }

    suspend fun getState(): SleepHealthConnectState {
        val availability = getAvailability()
        val hasPermission = if (availability == HealthConnectAvailability.Available) {
            hasRequiredPermissions()
        } else {
            false
        }

        return SleepHealthConnectState(
            availability = availability,
            hasSleepPermission = hasPermission
        )
    }

    suspend fun hasRequiredPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        val grantedPermissions = client.permissionController.getGrantedPermissions()

        return grantedPermissions.containsAll(PERMISSIONS)
    }

    suspend fun readSleepSessionsFromLast30Days(): List<SleepEntry> {
        val client = healthConnectClient ?: return emptyList()
        if (!hasRequiredPermissions()) return emptyList()

        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofDays(30))

        val response = client.readRecords(
            ReadRecordsRequest<SleepSessionRecord>(
                timeRangeFilter = TimeRangeFilter.between(
                    startTime,
                    endTime
                )
            )
        )

        return response.records.mapNotNull { record ->
            record.toSleepEntry()
        }
    }

    private fun getAvailability(): HealthConnectAvailability {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectAvailability.Available
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                HealthConnectAvailability.ProviderUpdateRequired

            else -> HealthConnectAvailability.Unavailable
        }
    }

    private fun SleepSessionRecord.toSleepEntry(): SleepEntry? {
        val startMillis = startTime.toEpochMilli()
        val endMillis = endTime.toEpochMilli()
        val durationMinutes = ((endMillis - startMillis) / (60 * 1000)).toInt()

        if (durationMinutes <= 0) return null

        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = startMillis
        }

        val endCalendar = Calendar.getInstance().apply {
            timeInMillis = endMillis
        }

        return SleepEntry(
            id = endMillis,
            date = SleepDateUtils.formatHistoryDate(endMillis),
            sleepHour = startCalendar.get(Calendar.HOUR_OF_DAY),
            sleepMinute = startCalendar.get(Calendar.MINUTE),
            wakeHour = endCalendar.get(Calendar.HOUR_OF_DAY),
            wakeMinute = endCalendar.get(Calendar.MINUTE),
            durationMinutes = durationMinutes,
            quality = SleepQuality.Good,
            notes = notes.orEmpty().ifBlank { "Imported from Health Connect." },
            dateMillis = endMillis,
            source = SleepSource.HealthConnect
        )
    }

    companion object {
        val PERMISSIONS: Set<String> = setOf(
            HealthPermission.getReadPermission(SleepSessionRecord::class)
        )
    }
}
