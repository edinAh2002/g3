package com.example.frontpage.reminders.data

import com.example.frontpage.reminders.ReminderEntry

class ReminderRepository(
    private val dao: ReminderDao
) {

    suspend fun addReminder(
        userId: Long,
        reminder: ReminderEntry
    ) {
        dao.insertReminder(
            reminder.copy(userId = userId)
        )
    }

    suspend fun deleteReminder(
        userId: Long,
        reminderId: Int
    ) {
        dao.deleteReminderForUser(
            reminderId,
            userId
        )
    }

    suspend fun getAllReminders(
        userId: Long
    ): List<ReminderEntry> {

        return dao.getAllRemindersForUser(userId)
    }
}