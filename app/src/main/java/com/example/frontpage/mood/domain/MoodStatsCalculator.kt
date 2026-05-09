package com.example.frontpage.mood.domain

import com.example.frontpage.mood.model.MoodEntry

object MoodStatsCalculator {

    fun getAverageMood(entries: List<MoodEntry>): Double? {
        if (entries.isEmpty()) return null
        return entries.map { entry -> entry.moodValue }.average()
    }

    fun getBestMood(entries: List<MoodEntry>): MoodEntry? {
        return entries.maxByOrNull { entry -> entry.moodValue }
    }

    fun getLowestMood(entries: List<MoodEntry>): MoodEntry? {
        return entries.minByOrNull { entry -> entry.moodValue }
    }

    fun getEntryCount(entries: List<MoodEntry>): Int {
        return entries.size
    }

    fun getTodayEntries(entries: List<MoodEntry>): List<MoodEntry> {
        val today = MoodDateUtils.getTodayDate()
        return entries.filter { entry -> entry.date == today }
    }

    fun getTodayAverageMood(entries: List<MoodEntry>): Double? {
        return getAverageMood(getTodayEntries(entries))
    }

    fun getAverageMoodForDate(
        entries: List<MoodEntry>,
        date: String
    ): Double? {
        return getAverageMood(
            entries.filter { entry -> entry.date == date }
        )
    }

    fun getPositiveMoodCount(entries: List<MoodEntry>): Int {
        return entries.count { entry -> entry.moodValue >= 4 }
    }

    fun getLowMoodCount(entries: List<MoodEntry>): Int {
        return entries.count { entry -> entry.moodValue <= 2 }
    }

    fun getMostCommonMoodValue(entries: List<MoodEntry>): Int? {
        return entries
            .groupingBy { entry -> entry.moodValue }
            .eachCount()
            .maxWithOrNull(
                compareBy<Map.Entry<Int, Int>> { entry -> entry.value }
                    .thenBy { entry -> entry.key }
            )
            ?.key
    }
}

