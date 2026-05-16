package com.example.frontpage.reminders.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.frontpage.reminders.ReminderEntry


@Dao
interface ReminderDao {

    @Insert
    suspend fun insertReminder(reminder: ReminderEntry)

    @Update
    suspend fun updateReminder(reminder: ReminderEntry)

    @Query("""
        DELETE FROM reminder_entries
        WHERE id = :id AND userId = :userId
    """)
    suspend fun deleteReminderForUser(
        id: Int,
        userId: Long
    )

    @Query("""
        SELECT * FROM reminder_entries
        WHERE userId = :userId
        ORDER BY triggerTime ASC
    """)
    suspend fun getAllRemindersForUser(
        userId: Long
    ): List<ReminderEntry>
}