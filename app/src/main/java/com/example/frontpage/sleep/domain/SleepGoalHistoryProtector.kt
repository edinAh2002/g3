package com.example.frontpage.sleep.domain

import com.example.frontpage.sleep.data.SleepSettingsDataSource
import com.example.frontpage.sleep.model.SleepEntry
import java.util.Calendar

class SleepGoalHistoryProtector(
    private val settingsDataSource: SleepSettingsDataSource,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() }
) {
    fun snapshotPastGoalDates(
        userId: Long?,
        sleepLogs: List<SleepEntry>,
        shouldSnapshot: (Long) -> Boolean
    ) {
        val todayStartMillis = startOfLocalDayMillis(currentTimeMillis())

        sleepLogs
            .asSequence()
            .map { entry -> entry.dateMillis }
            .filter { dateMillis ->
                startOfLocalDayMillis(dateMillis) < todayStartMillis && shouldSnapshot(dateMillis)
            }
            .map { dateMillis -> startOfLocalDayMillis(dateMillis) }
            .distinct()
            .forEach { dateMillis ->
                settingsDataSource.snapshotSleepGoalMinutesForDate(
                    userId = userId,
                    dateMillis = dateMillis
                )
            }
    }

    private fun startOfLocalDayMillis(dateMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
