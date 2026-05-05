package com.example.frontpage.mood.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.frontpage.mood.model.MoodEntry

@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(moodEntry: MoodEntry)

    @Update
    suspend fun updateMood(moodEntry: MoodEntry)

    @Delete
    suspend fun deleteMood(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY id DESC")
    suspend fun getAllMoodEntries(): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY id DESC")
    suspend fun getMoodEntriesBetweenDates(
        startDate: String,
        endDate: String
    ): List<MoodEntry>

    @Query("SELECT AVG(mood_value) FROM mood_entries")
    suspend fun getAverageMood(): Double?

    @Query("SELECT AVG(mood_value) FROM mood_entries WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getAverageMoodBetweenDates(
        startDate: String,
        endDate: String
    ): Double?

    @Query("SELECT * FROM mood_entries ORDER BY id DESC LIMIT 1")
    suspend fun getLatestMood(): MoodEntry?
}