package com.example.frontpage.stepcounter.data

import com.example.frontpage.stepcounter.model.StepsEntry
import kotlinx.coroutines.flow.Flow

class StepsRepository(
    private val stepsDao: StepsDao
) {
    fun getEntriesForUser(userId: Long): Flow<List<StepsEntry>> {
        return stepsDao.getEntriesForUser(userId)
    }

    fun getLatestEntryForUser(userId: Long): Flow<StepsEntry?> {
        return stepsDao.getLatestEntryForUser(userId)
    }

    suspend fun getEntryForDay(
        userId: Long,
        dayStartMillis: Long
    ): StepsEntry? {
        return stepsDao.getEntryForDay(
            userId = userId,
            dayStartMillis = dayStartMillis
        )
    }

    suspend fun addEntry(
        userId: Long,
        entry: StepsEntry
    ) {
        stepsDao.addEntry(
            entry.copy(userId = userId)
        )
    }

    suspend fun updateEntry(
        userId: Long,
        entry: StepsEntry
    ) {
        stepsDao.updateEntry(
            entry.copy(userId = userId)
        )
    }

    suspend fun deleteEntry(
        userId: Long,
        id: Long
    ) {
        stepsDao.deleteEntryForUser(
            id = id,
            userId = userId
        )
    }

    suspend fun clearAllEntries(userId: Long) {
        stepsDao.clearEntriesForUser(userId)
    }
}