package com.example.frontpage.mood.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "time")
    val time: String,

    @ColumnInfo(name = "mood_value")
    val moodValue: Int,

    @ColumnInfo(name = "note")
    val note: String
)