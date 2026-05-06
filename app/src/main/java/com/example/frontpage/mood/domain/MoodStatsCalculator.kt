package com.example.frontpage.mood.domain

import com.example.frontpage.mood.model.MoodEntry

object MoodStatsCalculator {

    fun getAverageMood(entries: List<MoodEntry>): Double? {
        if (entries.isEmpty()) return null
        return entries.map { it.moodValue }.average()
    }

    fun getBestMood(entries: List<MoodEntry>): MoodEntry? {
        return entries.maxByOrNull { it.moodValue }
    }

    fun getLowestMood(entries: List<MoodEntry>): MoodEntry? {
        return entries.minByOrNull { it.moodValue }
    }

    fun getEntryCount(entries: List<MoodEntry>): Int {
        return entries.size
    }

    fun getTodayEntries(entries: List<MoodEntry>): List<MoodEntry> {
        val today = MoodDateUtils.getTodayDate()
        return entries.filter { it.date == today }
    }

    fun getTodayAverageMood(entries: List<MoodEntry>): Double? {
        return getAverageMood(getTodayEntries(entries))
    }
}