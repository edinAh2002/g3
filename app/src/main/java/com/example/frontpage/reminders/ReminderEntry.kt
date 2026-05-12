package com.example.frontpage.reminders

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_entries")
data class ReminderEntry(

    @PrimaryKey
    val id: Int,

    val userId: Long,

    val title: String,

    val description: String,

    val date: String,

    val time: String,

    val triggerTime: Long
)