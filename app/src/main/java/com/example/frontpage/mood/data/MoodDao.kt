package com.example.frontpage.mood.data

import androidx.room.Dao
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

    @Query(
        """
        DELETE FROM mood_entries
        WHERE id = :id AND userId = :userId
        """
    )
    suspend fun deleteMoodForUser(
        id: Int,
        userId: Long
    )

    @Query(
        """
        DELETE FROM mood_entries
        WHERE userId = :userId
        AND id IN (:ids)
        """
    )
    suspend fun deleteMoodsForUser(
        ids: List<Int>,
        userId: Long
    )

    @Query(
        """
        DELETE FROM mood_entries
        WHERE userId = :userId
        """
    )
    suspend fun deleteAllMoodEntriesForUser(userId: Long)

    @Query(
        """
        SELECT * FROM mood_entries
        WHERE userId = :userId
        ORDER BY id DESC
        """
    )
    suspend fun getAllMoodEntriesForUser(userId: Long): List<MoodEntry>

    @Query(
        """
        SELECT * FROM mood_entries
        WHERE userId = :userId
        AND date BETWEEN :startDate AND :endDate
        ORDER BY id DESC
        """
    )
    suspend fun getMoodEntriesBetweenDatesForUser(
        userId: Long,
        startDate: String,
        endDate: String
    ): List<MoodEntry>

    @Query(
        """
        SELECT AVG(mood_value) FROM mood_entries
        WHERE userId = :userId
        """
    )
    suspend fun getAverageMoodForUser(userId: Long): Double?

    @Query(
        """
        SELECT AVG(mood_value) FROM mood_entries
        WHERE userId = :userId
        AND date BETWEEN :startDate AND :endDate
        """
    )
    suspend fun getAverageMoodBetweenDatesForUser(
        userId: Long,
        startDate: String,
        endDate: String
    ): Double?

    @Query(
        """
        SELECT * FROM mood_entries
        WHERE userId = :userId
        ORDER BY id DESC
        LIMIT 1
        """
    )
    suspend fun getLatestMoodForUser(userId: Long): MoodEntry?
}
