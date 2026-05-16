package com.example.frontpage.stepcounter.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.frontpage.sleep.model.HealthConnectAvailability
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.example.frontpage.stepcounter.model.StepsHealthConnectState

class StepsHealthConnectManager(
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

    suspend fun getState(): StepsHealthConnectState {
        val availability = getAvailability()

        val hasPermission = if (availability == HealthConnectAvailability.Available) {
            hasRequiredPermissions()
        } else {
            false
        }

        return StepsHealthConnectState(
            availability = availability,
            hasStepsPermission = hasPermission
        )
    }

    suspend fun hasRequiredPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        val grantedPermissions = client.permissionController.getGrantedPermissions()

        return grantedPermissions.containsAll(PERMISSIONS)
    }

    suspend fun readTodaySteps(): Long {
        val client = healthConnectClient ?: return 0L

        if (!hasRequiredPermissions()) {
            return 0L
        }

        val zoneId = ZoneId.systemDefault()

        val startTime: Instant = LocalDate
            .now(zoneId)
            .atStartOfDay(zoneId)
            .toInstant()

        val endTime: Instant = Instant.now()

        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(
                    startTime,
                    endTime
                )
            )
        )

        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    private fun getAvailability(): HealthConnectAvailability {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE ->
                HealthConnectAvailability.Available

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                HealthConnectAvailability.ProviderUpdateRequired

            else ->
                HealthConnectAvailability.Unavailable
        }
    }

    companion object {
        val PERMISSIONS: Set<String> = setOf(
            HealthPermission.getReadPermission(StepsRecord::class)
        )
    }
}