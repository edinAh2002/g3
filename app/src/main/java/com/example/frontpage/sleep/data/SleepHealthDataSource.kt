package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepHealthConnectState

interface SleepHealthDataSource {
    val requiredPermissions: Set<String>

    suspend fun getState(): SleepHealthConnectState

    suspend fun hasRequiredPermissions(): Boolean

    suspend fun readSleepSessionsFromLast30Days(): List<SleepEntry>
}
