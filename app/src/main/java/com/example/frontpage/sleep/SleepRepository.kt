package com.example.frontpage.sleep

object SleepRepository {

    private val sleepLogs = mutableListOf<SleepEntry>()

    fun addSleep(entry: SleepEntry) {
        sleepLogs.add(entry)
    }

    fun deleteSleep(id: Int) {
        sleepLogs.removeAll { it.id == id }
    }

    fun getAllSleepLogs(): List<SleepEntry> {
        return sleepLogs
    }

    fun getLatestSleep(): SleepEntry? {
        return sleepLogs.lastOrNull()
    }

    fun getAverageSleepMinutes(): Int {
        if (sleepLogs.isEmpty()) return 0

        return sleepLogs
            .map { it.durationMinutes }
            .average()
            .toInt()
    }

    fun getAverageSleep(): Double {
        if (sleepLogs.isEmpty()) return 0.0

        return sleepLogs
            .map { it.durationMinutes }
            .average() / 60.0
    }

    fun getLongestSleepMinutes(): Int {
        return sleepLogs.maxOfOrNull { it.durationMinutes } ?: 0
    }

    fun getShortestSleepMinutes(): Int {
        return sleepLogs.minOfOrNull { it.durationMinutes } ?: 0
    }

    fun getTotalLogs(): Int {
        return sleepLogs.size
    }

    fun clearAllLogs() {
        sleepLogs.clear()
    }
}