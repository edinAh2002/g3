package com.example.frontpage.sleep.domain

import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.WeeklySleepChartItem

data class SleepDashboardState(
    val latestSleep: SleepEntry?,
    val latestGoalMinutes: Int,
    val averageSleepMinutes: Int,
    val longestSleepMinutes: Int,
    val shortestSleepMinutes: Int,
    val weeklyChartData: List<WeeklySleepChartItem>,
    val averageBedtimeMinutes: Int?,
    val averageWakeTimeMinutes: Int?,
    val last7DaysSleepLogs: List<SleepEntry>,
    val sleepConsistencyVariationMinutes: Int?,
    val sleepDurationRangeMinutes: Int?,
    val sleepScoreSummary: SleepScoreSummary?,
    val sleepGoalBalance: SleepGoalBalance,
    val streakSummary: SleepStreakSummary,
    val primaryRecommendation: String,
    val sleepMoodInsight: SleepMoodInsight?,
    val sleepTagInsight: SleepTagInsight?
)

class SleepDashboardStateBuilder {

    fun build(
        sleepLogs: List<SleepEntry>,
        fallbackGoalMinutes: Int,
        moodEntries: List<MoodEntry>,
        goalMinutesForDate: (Long) -> Int
    ): SleepDashboardState {
        val latestSleep = sleepLogs.lastOrNull()
        val latestGoalMinutes = latestSleep?.let { sleepEntry ->
            goalMinutesForDate(sleepEntry.dateMillis)
        } ?: fallbackGoalMinutes

        val averageSleepMinutes = if (sleepLogs.isEmpty()) {
            0
        } else {
            sleepLogs.map { sleepEntry -> sleepEntry.durationMinutes }.average().toInt()
        }

        val longestSleepMinutes = sleepLogs.maxOfOrNull { sleepEntry ->
            sleepEntry.durationMinutes
        } ?: 0

        val shortestSleepMinutes = sleepLogs.minOfOrNull { sleepEntry ->
            sleepEntry.durationMinutes
        } ?: 0

        val weeklyChartData = SleepAnalytics.buildWeeklySleepChartData(sleepLogs)
        val averageBedtimeMinutes = SleepCalculator.calculateAverageBedtimeMinutes(sleepLogs)
        val averageWakeTimeMinutes = SleepCalculator.calculateAverageWakeTimeMinutes(sleepLogs)
        val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

        val last7DaysSleepLogs = sleepLogs.filter { sleepEntry ->
            sleepEntry.dateMillis >= sevenDaysAgo
        }

        val sleepConsistencyVariationMinutes =
            SleepCalculator.calculateSleepConsistencyVariationMinutes(last7DaysSleepLogs)

        val sleepDurationRangeMinutes =
            SleepCalculator.calculateSleepDurationRangeMinutes(last7DaysSleepLogs)

        val sleepScoreSummary = SleepAnalytics.buildSleepScoreSummary(
            latestSleep = latestSleep,
            goalMinutes = latestGoalMinutes,
            consistencyVariationMinutes = sleepConsistencyVariationMinutes,
            durationRangeMinutes = sleepDurationRangeMinutes
        )

        val sleepGoalBalance = SleepAnalytics.buildSleepGoalBalance(
            sleepLogs = last7DaysSleepLogs,
            goalMinutesForDate = goalMinutesForDate
        )

        val streakSummary = SleepAnalytics.buildSleepStreakSummary(
            sleepLogs = sleepLogs,
            goalMinutesForDate = goalMinutesForDate
        )

        val primaryRecommendation = SleepAnalytics.buildPrimarySleepRecommendation(
            latestSleep = latestSleep,
            goalMinutes = latestGoalMinutes,
            sleepGoalBalance = sleepGoalBalance,
            streakSummary = streakSummary,
            consistencyVariationMinutes = sleepConsistencyVariationMinutes,
            durationRangeMinutes = sleepDurationRangeMinutes
        )

        val sleepMoodInsight = SleepAnalytics.buildSleepMoodInsight(
            sleepLogs = sleepLogs,
            moodEntries = moodEntries
        )

        val sleepTagInsight = SleepAnalytics.buildSleepTagInsight(sleepLogs)

        return SleepDashboardState(
            latestSleep = latestSleep,
            latestGoalMinutes = latestGoalMinutes,
            averageSleepMinutes = averageSleepMinutes,
            longestSleepMinutes = longestSleepMinutes,
            shortestSleepMinutes = shortestSleepMinutes,
            weeklyChartData = weeklyChartData,
            averageBedtimeMinutes = averageBedtimeMinutes,
            averageWakeTimeMinutes = averageWakeTimeMinutes,
            last7DaysSleepLogs = last7DaysSleepLogs,
            sleepConsistencyVariationMinutes = sleepConsistencyVariationMinutes,
            sleepDurationRangeMinutes = sleepDurationRangeMinutes,
            sleepScoreSummary = sleepScoreSummary,
            sleepGoalBalance = sleepGoalBalance,
            streakSummary = streakSummary,
            primaryRecommendation = primaryRecommendation,
            sleepMoodInsight = sleepMoodInsight,
            sleepTagInsight = sleepTagInsight
        )
    }
}
