package com.example.frontpage.mood.data

import com.example.frontpage.mood.model.MoodEntry

class MoodRepository(
    private val moodDao: MoodDao
) {
    suspend fun addMood(moodEntry: MoodEntry) {
        moodDao.insertMood(moodEntry)
    }

    suspend fun updateMood(moodEntry: MoodEntry) {
        moodDao.updateMood(moodEntry)
    }

    suspend fun deleteMood(moodEntry: MoodEntry) {
        moodDao.deleteMood(moodEntry)
    }

    suspend fun getAllMoods(): List<MoodEntry> {
        return moodDao.getAllMoodEntries()
    }

    suspend fun getMoodsBetweenDates(
        startDate: String,
        endDate: String
    ): List<MoodEntry> {
        return moodDao.getMoodEntriesBetweenDates(
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getAverageMood(): Double? {
        return moodDao.getAverageMood()
    }

    suspend fun getAverageMoodBetweenDates(
        startDate: String,
        endDate: String
    ): Double? {
        return moodDao.getAverageMoodBetweenDates(
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getLatestMood(): MoodEntry? {
        return moodDao.getLatestMood()
    }
}