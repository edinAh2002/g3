package com.example.frontpage.stepcounter.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps_entries")
data class StepsEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val dayStartMillis: Long,

    val steps: Long,

    val goal: Long
)
