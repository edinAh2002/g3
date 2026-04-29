package com.example.fit_tastic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mood_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "mood_value")
    val moodValue: Int,

    @ColumnInfo(name = "note")
    val note: String
)