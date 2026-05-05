package com.example.frontpage.sleep.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep_entries ORDER BY dateMillis ASC")
    fun getAllSleepLogs(): Flow<List<SleepEntry>>

    @Query("SELECT * FROM sleep_entries ORDER BY dateMillis DESC LIMIT 1")
    fun getLatestSleep(): Flow<SleepEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSleep(entry: SleepEntry)

    @Update
    suspend fun updateSleep(entry: SleepEntry)

    @Query("DELETE FROM sleep_entries WHERE id = :id")
    suspend fun deleteSleep(id: Long)

    @Query("DELETE FROM sleep_entries")
    suspend fun clearAllLogs()
}