package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.Flow

interface SleepLogDataSource {
    fun getSleepLogsForUser(userId: Long): Flow<List<SleepEntry>>

    fun getLatestSleepForUser(userId: Long): Flow<SleepEntry?>

    suspend fun addSleep(
        userId: Long,
        entry: SleepEntry
    )

    suspend fun updateSleep(
        userId: Long,
        entry: SleepEntry
    )

    suspend fun deleteSleep(
        userId: Long,
        id: Long
    )

    suspend fun clearAllLogs(userId: Long)
}
