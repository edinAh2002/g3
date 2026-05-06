package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepDao: SleepDao
) {
    fun getAllSleepLogs(): Flow<List<SleepEntry>> {
        return sleepDao.getAllSleepLogs()
    }

    fun getLatestSleep(): Flow<SleepEntry?> {
        return sleepDao.getLatestSleep()
    }

    suspend fun addSleep(entry: SleepEntry) {
        sleepDao.addSleep(entry)
    }

    suspend fun updateSleep(entry: SleepEntry) {
        sleepDao.updateSleep(entry)
    }

    suspend fun deleteSleep(id: Long) {
        sleepDao.deleteSleep(id)
    }

    suspend fun clearAllLogs() {
        sleepDao.clearAllLogs()
    }
}