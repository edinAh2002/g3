package com.example.frontpage.mood.model

import com.example.frontpage.mood.domain.MoodDateUtils

data class MoodLogDraft(
    val moodValue: Int,
    val note: String,
    val date: String
) {
    fun toNewEntry(
        time: String = MoodDateUtils.getCurrentTime()
    ): MoodEntry {
        return MoodEntry(
            date = date,
            time = time,
            moodValue = moodValue,
            note = note.trim()
        )
    }

    fun applyTo(entry: MoodEntry): MoodEntry {
        return entry.copy(
            date = date,
            moodValue = moodValue,
            note = note.trim()
        )
    }
}
