package com.example.frontpage.mood.domain

import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.model.WeeklyMoodChartItem

data class MoodDashboardState(
    val latestMood: MoodEntry?,
    val averageMood: Double?,
    val todayAverageMood: Double?,
    val bestMood: MoodEntry?,
    val lowestMood: MoodEntry?,
    val totalLogs: Int,
    val weeklyChartData: List<WeeklyMoodChartItem>,
    val last7DaysMoodEntries: List<MoodEntry>,
    val moodScoreSummary: MoodScoreSummary?,
    val moodMomentumSummary: MoodMomentumSummary,
    val streakSummary: MoodStreakSummary,
    val primaryRecommendation: String,
    val moodPatternInsight: MoodPatternInsight?,
    val moodNoteInsight: MoodNoteInsight?
)

class MoodDashboardStateBuilder {

    fun build(
        moodEntries: List<MoodEntry>,
        scalePreset: MoodScalePreset = MoodScalePreset.Default
    ): MoodDashboardState {
        val latestMood = moodEntries.firstOrNull()
        val averageMood = MoodStatsCalculator.getAverageMood(moodEntries)
        val todayAverageMood = MoodStatsCalculator.getTodayAverageMood(moodEntries)
        val bestMood = MoodStatsCalculator.getBestMood(moodEntries)
        val lowestMood = MoodStatsCalculator.getLowestMood(moodEntries)
        val weeklyChartData = buildWeeklyMoodChartData(moodEntries)

        val last14Dates = MoodDateUtils.getLastNDates(dayCount = 14)
        val previous7Dates = last14Dates.take(7).toSet()
        val last7Dates = last14Dates.takeLast(7).toSet()

        val last7DaysMoodEntries = moodEntries.filter { entry ->
            entry.date in last7Dates
        }

        val previous7DaysMoodEntries = moodEntries.filter { entry ->
            entry.date in previous7Dates
        }

        val last7Average = MoodStatsCalculator.getAverageMood(last7DaysMoodEntries)
        val previous7Average = MoodStatsCalculator.getAverageMood(previous7DaysMoodEntries)
        val moodMomentumSummary = buildMoodMomentumSummary(
            last7DayAverage = last7Average,
            previous7DayAverage = previous7Average
        )

        val moodScoreSummary = buildMoodScoreSummary(
            latestMood = latestMood,
            last7DayAverage = last7Average,
            positiveRatio = positiveMoodRatio(last7DaysMoodEntries)
        )

        val streakSummary = buildMoodStreakSummary(moodEntries)

        val primaryRecommendation = buildPrimaryMoodRecommendation(
            latestMood = latestMood,
            moodScoreSummary = moodScoreSummary,
            moodMomentumSummary = moodMomentumSummary,
            streakSummary = streakSummary
        )

        return MoodDashboardState(
            latestMood = latestMood,
            averageMood = averageMood,
            todayAverageMood = todayAverageMood,
            bestMood = bestMood,
            lowestMood = lowestMood,
            totalLogs = moodEntries.size,
            weeklyChartData = weeklyChartData,
            last7DaysMoodEntries = last7DaysMoodEntries,
            moodScoreSummary = moodScoreSummary,
            moodMomentumSummary = moodMomentumSummary,
            streakSummary = streakSummary,
            primaryRecommendation = primaryRecommendation,
            moodPatternInsight = buildMoodPatternInsight(
                moodEntries = moodEntries,
                scalePreset = scalePreset
            ),
            moodNoteInsight = buildMoodNoteInsight(moodEntries)
        )
    }
}
