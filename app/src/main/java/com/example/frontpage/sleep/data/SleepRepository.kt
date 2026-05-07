package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepDao: SleepDao
) {
    fun getSleepLogsForUser(userId: Long): Flow<List<SleepEntry>> {
        return sleepDao.getSleepLogsForUser(userId)
    }

    fun getLatestSleepForUser(userId: Long): Flow<SleepEntry?> {
        return sleepDao.getLatestSleepForUser(userId)
    }

    suspend fun addSleep(
        userId: Long,
        entry: SleepEntry
    ) {
        sleepDao.addSleep(
            entry.copy(userId = userId)
        )
    }

    suspend fun updateSleep(
        userId: Long,
        entry: SleepEntry
    ) {
        sleepDao.updateSleep(
            entry.copy(userId = userId)
        )
    }

    suspend fun deleteSleep(
        userId: Long,
        id: Long
    ) {
        sleepDao.deleteSleepForUser(
            id = id,
            userId = userId
        )
    }

    suspend fun clearAllLogs(userId: Long) {
        sleepDao.clearSleepLogsForUser(userId)
    }
}