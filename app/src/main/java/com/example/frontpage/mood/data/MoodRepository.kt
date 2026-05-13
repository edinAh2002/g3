package com.example.frontpage.mood.data

import com.example.frontpage.mood.model.MoodEntry

class MoodRepository(
    private val moodDao: MoodDao
) {
    suspend fun addMood(
        userId: Long,
        moodEntry: MoodEntry
    ) {
        moodDao.insertMood(
            moodEntry.copy(userId = userId)
        )
    }

    suspend fun updateMood(
        userId: Long,
        moodEntry: MoodEntry
    ) {
        moodDao.updateMood(
            moodEntry.copy(userId = userId)
        )
    }

    suspend fun deleteMood(
        userId: Long,
        moodEntry: MoodEntry
    ) {
        moodDao.deleteMoodForUser(
            id = moodEntry.id,
            userId = userId
        )
    }

    suspend fun deleteMoods(
        userId: Long,
        moodIds: List<Int>
    ) {
        if (moodIds.isEmpty()) return

        moodDao.deleteMoodsForUser(
            ids = moodIds,
            userId = userId
        )
    }

    suspend fun clearAllMoods(userId: Long) {
        moodDao.deleteAllMoodEntriesForUser(userId)
    }

    suspend fun getAllMoods(userId: Long): List<MoodEntry> {
        return moodDao.getAllMoodEntriesForUser(userId)
    }

    suspend fun getMoodsBetweenDates(
        userId: Long,
        startDate: String,
        endDate: String
    ): List<MoodEntry> {
        return moodDao.getMoodEntriesBetweenDatesForUser(
            userId = userId,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getAverageMood(userId: Long): Double? {
        return moodDao.getAverageMoodForUser(userId)
    }

    suspend fun getAverageMoodBetweenDates(
        userId: Long,
        startDate: String,
        endDate: String
    ): Double? {
        return moodDao.getAverageMoodBetweenDatesForUser(
            userId = userId,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getLatestMood(userId: Long): MoodEntry? {
        return moodDao.getLatestMoodForUser(userId)
    }
}
