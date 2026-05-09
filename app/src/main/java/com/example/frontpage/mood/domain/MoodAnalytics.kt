package com.example.frontpage.mood.domain

import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.model.WeeklyMoodChartItem
import kotlin.math.abs
import kotlin.math.roundToInt

private const val POSITIVE_MOOD_VALUE = 4

data class MoodScoreSummary(
    val score: Int,
    val title: String,
    val description: String
)

data class MoodMomentumSummary(
    val change: Double?,
    val title: String,
    val description: String
)

data class MoodStreakSummary(
    val loggedDayStreak: Int,
    val positiveMoodStreak: Int
)

data class MoodPatternInsight(
    val title: String,
    val description: String
)

data class MoodNoteInsight(
    val title: String,
    val description: String
)

fun buildWeeklyMoodChartData(
    moodEntries: List<MoodEntry>
): List<WeeklyMoodChartItem> {
    return MoodDateUtils.getLastNDates(dayCount = 7).map { date ->
        val dateEntries = moodEntries.filter { entry -> entry.date == date }

        WeeklyMoodChartItem(
            dayLabel = MoodDateUtils.formatDayLabel(date),
            averageMood = MoodStatsCalculator.getAverageMood(dateEntries),
            entryCount = dateEntries.size,
            date = date
        )
    }
}

fun buildMoodScoreSummary(
    latestMood: MoodEntry?,
    last7DayAverage: Double?,
    positiveRatio: Double
): MoodScoreSummary? {
    if (latestMood == null) return null

    val latestScore = latestMood.moodValue.toDouble() / 5.0
    val averageScore = (last7DayAverage ?: latestMood.moodValue.toDouble()) / 5.0
    val score = ((latestScore * 55.0) + (averageScore * 30.0) + (positiveRatio * 15.0))
        .roundToInt()
        .coerceIn(0, 100)

    return MoodScoreSummary(
        score = score,
        title = when {
            score >= 85 -> "Bright mood pattern"
            score >= 70 -> "Mostly positive"
            score >= 55 -> "Steady mood"
            else -> "Needs care"
        },
        description = when {
            score >= 85 -> "Your recent mood logs are strongly positive."
            score >= 70 -> "Your mood trend has more good moments than difficult ones."
            score >= 55 -> "Your mood is holding steady. Keep logging the context around shifts."
            else -> "Recent logs show lower mood. Gentle routines and support may help."
        }
    )
}

fun buildMoodMomentumSummary(
    last7DayAverage: Double?,
    previous7DayAverage: Double?
): MoodMomentumSummary {
    val change = if (last7DayAverage == null || previous7DayAverage == null) {
        null
    } else {
        last7DayAverage - previous7DayAverage
    }

    return MoodMomentumSummary(
        change = change,
        title = when {
            change == null -> "More logs needed"
            change >= 0.4 -> "Mood is trending up"
            change <= -0.4 -> "Mood is trending down"
            else -> "Mood is stable"
        },
        description = when {
            change == null -> "Log mood across two weeks to compare recent momentum."
            change >= 0.4 -> "Your last 7 days average is higher than the previous week."
            change <= -0.4 -> "Your last 7 days average is lower than the previous week."
            else -> "Your last 7 days look close to the previous week."
        }
    )
}

fun buildMoodStreakSummary(
    moodEntries: List<MoodEntry>
): MoodStreakSummary {
    return MoodStreakSummary(
        loggedDayStreak = calculateMoodDayStreak(moodEntries),
        positiveMoodStreak = calculatePositiveMoodStreak(moodEntries)
    )
}

fun buildPrimaryMoodRecommendation(
    latestMood: MoodEntry?,
    moodScoreSummary: MoodScoreSummary?,
    moodMomentumSummary: MoodMomentumSummary,
    streakSummary: MoodStreakSummary
): String {
    if (latestMood == null) {
        return "Log your next mood to unlock a clear recommendation."
    }

    return when {
        latestMood.moodValue <= 2 -> "Your latest mood is low. Add a short note so the trigger is easier to spot later."
        moodMomentumSummary.change != null && moodMomentumSummary.change <= -0.4 ->
            "Mood momentum is dipping. Check whether sleep, food, stress, or activity changed this week."

        moodScoreSummary != null && moodScoreSummary.score >= 85 ->
            "Your recent logs look strong. Capture what helped so it is easier to repeat."

        streakSummary.loggedDayStreak >= 4 ->
            "You are building a useful logging streak. Keep the entries short and consistent."

        else -> "Keep logging mood with a note when something clearly influenced it."
    }
}

fun buildMoodPatternInsight(
    moodEntries: List<MoodEntry>,
    scalePreset: MoodScalePreset = MoodScalePreset.Default
): MoodPatternInsight? {
    val commonMoodValue = MoodStatsCalculator.getMostCommonMoodValue(moodEntries) ?: return null
    val commonMoodCount = moodEntries.count { entry -> entry.moodValue == commonMoodValue }

    return MoodPatternInsight(
        title = "Most common mood: ${MoodLabelUtils.getMoodLabel(commonMoodValue, scalePreset)}",
        description = "This appears in $commonMoodCount of your saved mood logs."
    )
}

fun buildMoodNoteInsight(
    moodEntries: List<MoodEntry>
): MoodNoteInsight? {
    if (moodEntries.isEmpty()) return null

    val notedEntries = moodEntries.count { entry -> entry.note.isNotBlank() }
    val notePercent = ((notedEntries.toDouble() / moodEntries.size.toDouble()) * 100).roundToInt()

    return MoodNoteInsight(
        title = "$notePercent% of logs include notes",
        description = if (notedEntries == 0) {
            "Adding short notes can make mood patterns easier to understand."
        } else {
            "Notes help connect mood with routines, stress, sleep, meals, and workouts."
        }
    )
}

private fun calculateMoodDayStreak(
    moodEntries: List<MoodEntry>
): Int {
    val loggedDates = moodEntries
        .map { entry -> entry.date }
        .toSet()

    return calculateDateStreak { date ->
        loggedDates.contains(date)
    }
}

private fun calculatePositiveMoodStreak(
    moodEntries: List<MoodEntry>
): Int {
    val averageByDate = moodEntries
        .groupBy { entry -> entry.date }
        .mapValues { (_, entries) -> entries.map { entry -> entry.moodValue }.average() }

    return calculateDateStreak { date ->
        (averageByDate[date] ?: 0.0) >= POSITIVE_MOOD_VALUE
    }
}

private fun calculateDateStreak(
    dateMatches: (String) -> Boolean
): Int {
    var streak = 0

    for (daysAgo in 0..365) {
        val date = MoodDateUtils.getDateDaysAgo(daysAgo)

        if (!dateMatches(date)) {
            break
        }

        streak++
    }

    return streak
}

internal fun positiveMoodRatio(moodEntries: List<MoodEntry>): Double {
    if (moodEntries.isEmpty()) return 0.0
    return MoodStatsCalculator.getPositiveMoodCount(moodEntries).toDouble() / moodEntries.size.toDouble()
}

internal fun moodAverageDifference(
    firstAverage: Double?,
    secondAverage: Double?
): Double? {
    if (firstAverage == null || secondAverage == null) return null
    return firstAverage - secondAverage
}

internal fun isMoodChangeMeaningful(change: Double?): Boolean {
    return change != null && abs(change) >= 0.4
}
