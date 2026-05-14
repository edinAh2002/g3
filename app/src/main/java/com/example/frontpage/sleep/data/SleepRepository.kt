package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepDao: SleepDao
) : SleepLogDataSource {
    override fun getSleepLogsForUser(userId: Long): Flow<List<SleepEntry>> {
        return sleepDao.getSleepLogsForUser(userId)
    }

    override fun getLatestSleepForUser(userId: Long): Flow<SleepEntry?> {
        return sleepDao.getLatestSleepForUser(userId)
    }

    override suspend fun addSleep(
        userId: Long,
        entry: SleepEntry
    ) {
        sleepDao.addSleep(
            entry.copy(userId = userId)
        )
    }

    override suspend fun updateSleep(
        userId: Long,
        entry: SleepEntry
    ) {
        sleepDao.updateSleep(
            entry.copy(userId = userId)
        )
    }

    override suspend fun deleteSleep(
        userId: Long,
        id: Long
    ) {
        sleepDao.deleteSleepForUser(
            id = id,
            userId = userId
        )
    }

    override suspend fun hasSleepLogForWakeDate(
        userId: Long,
        wakeDateStartMillis: Long,
        wakeDateEndMillis: Long
    ): Boolean {
        return sleepDao.countSleepLogsForWakeDate(
            userId = userId,
            wakeDateStartMillis = wakeDateStartMillis,
            wakeDateEndMillis = wakeDateEndMillis
        ) > 0
    }

    override suspend fun clearAllLogs(userId: Long) {
        sleepDao.clearSleepLogsForUser(userId)
    }
}
