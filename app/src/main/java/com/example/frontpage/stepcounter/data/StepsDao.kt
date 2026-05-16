package com.example.frontpage.stepcounter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.frontpage.stepcounter.model.StepsEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDao {

    @Query(
        """
        SELECT * FROM steps_entries
        WHERE userId = :userId
        ORDER BY dayStartMillis ASC
        """
    )
    fun getEntriesForUser(userId: Long): Flow<List<StepsEntry>>

    @Query(
        """
        SELECT * FROM steps_entries
        WHERE userId = :userId
        ORDER BY dayStartMillis DESC
        LIMIT 1
        """
    )
    fun getLatestEntryForUser(userId: Long): Flow<StepsEntry?>

    @Query(
        """
        SELECT * FROM steps_entries
        WHERE userId = :userId
        ORDER BY dayStartMillis DESC
        LIMIT 1
        """
    )
    suspend fun getLatestEntryForUserOnce(userId: Long): StepsEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntry(entry: StepsEntry)

    @Update
    suspend fun updateEntry(entry: StepsEntry)

    @Query(
        """
        DELETE FROM steps_entries
        WHERE id = :id AND userId = :userId
        """
    )
    suspend fun deleteEntryForUser(
        id: Long,
        userId: Long
    )

    @Query(
        """
        DELETE FROM steps_entries
        WHERE userId = :userId
        """
    )
    suspend fun clearEntriesForUser(userId: Long)

    @Query(
        """
    SELECT * FROM steps_entries
    WHERE userId = :userId AND dayStartMillis = :dayStartMillis
    LIMIT 1
    """
    )
    suspend fun getEntryForDay(
        userId: Long,
        dayStartMillis: Long
    ): StepsEntry?
}