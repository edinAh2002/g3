package com.example.frontpage.sleep.domain

import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepTag
import com.example.frontpage.sleep.model.WeeklySleepChartItem
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val NEAR_GOAL_TOLERANCE_MINUTES = 60
private const val MIN_MATCHED_MOOD_DAYS = 3

data class SleepScoreSummary(
    val score: Int,
    val title: String,
    val description: String
)

data class SleepGoalBalance(
    val balanceMinutes: Int,
    val title: String,
    val description: String
)

data class SleepStreakSummary(
    val loggedDayStreak: Int,
    val nearGoalStreak: Int
)

data class SleepMoodInsight(
    val matchedDays: Int,
    val correlation: Double,
    val title: String,
    val description: String
)

data class SleepTagInsight(
    val title: String,
    val description: String
)

fun buildWeeklySleepChartData(
    sleepLogs: List<SleepEntry>
): List<WeeklySleepChartItem> {
    val calendar = Calendar.getInstance()
    val items = mutableListOf<WeeklySleepChartItem>()

    for (daysAgo in 6 downTo 0) {
        val dayCalendar = calendar.clone() as Calendar
        dayCalendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        val dayMillis = dayCalendar.timeInMillis

        val latestLogForDay = sleepLogs
            .filter { SleepDateUtils.isSameDay(it.dateMillis, dayMillis) }
            .maxByOrNull { it.dateMillis }

        items.add(
            WeeklySleepChartItem(
                dayLabel = SleepDateUtils.formatDayName(dayMillis),
                durationMinutes = latestLogForDay?.durationMinutes ?: 0,
                dateMillis = dayMillis
            )
        )
    }

    return items
}

fun buildSleepScoreSummary(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    consistencyVariationMinutes: Int?,
    durationRangeMinutes: Int?
): SleepScoreSummary? {
    val score = calculateSleepScore(
        latestSleep = latestSleep,
        goalMinutes = goalMinutes,
        consistencyVariationMinutes = consistencyVariationMinutes,
        durationRangeMinutes = durationRangeMinutes
    ) ?: return null

    return SleepScoreSummary(
        score = score,
        title = when {
            score >= 90 -> "Excellent sleep"
            score >= 75 -> "Strong sleep"
            score >= 60 -> "Fair sleep"
            else -> "Needs attention"
        },
        description = when {
            score >= 90 -> "Your latest sleep is close to your goal and your routine looks steady."
            score >= 75 -> "Your latest sleep is in a good range. Keep the routine consistent."
            score >= 60 -> "You have a useful base. Small bedtime changes can improve the score."
            else -> "Focus on getting closer to your goal and keeping sleep times steadier."
        }
    )
}

fun calculateSleepScore(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    consistencyVariationMinutes: Int?,
    durationRangeMinutes: Int?
): Int? {
    if (latestSleep == null || goalMinutes <= 0) return null

    val durationScore = calculateGoalClosenessScore(
        durationMinutes = latestSleep.durationMinutes,
        goalMinutes = goalMinutes
    )

    val consistencyScore = if (consistencyVariationMinutes == null || durationRangeMinutes == null) {
        60
    } else {
        (SleepCalculator.calculateSleepConsistencyProgress(
            variationMinutes = consistencyVariationMinutes,
            durationRangeMinutes = durationRangeMinutes
        ) * 100).roundToInt()
    }

    val score = (durationScore * 0.45) +
            (SleepCalculator.qualityScore(latestSleep.quality) * 0.35) +
            (consistencyScore * 0.20)

    return score.roundToInt().coerceIn(0, 100)
}

fun buildSleepGoalBalance(
    sleepLogs: List<SleepEntry>,
    goalMinutes: Int
): SleepGoalBalance {
    val balanceMinutes = calculateGoalBalanceMinutes(
        sleepLogs = sleepLogs,
        goalMinutes = goalMinutes
    )

    return SleepGoalBalance(
        balanceMinutes = balanceMinutes,
        title = when {
            sleepLogs.isEmpty() -> "No 7-day balance yet"
            balanceMinutes == 0 -> "Exactly on goal"
            balanceMinutes > 0 -> "${SleepCalculator.formatDuration(balanceMinutes)} above goal"
            else -> "${SleepCalculator.formatDuration(-balanceMinutes)} below goal"
        },
        description = when {
            sleepLogs.isEmpty() -> "Log sleep to see how your week compares with your goal."
            abs(balanceMinutes) <= 60 -> "Your recent sleep is very close to your goal."
            balanceMinutes > 0 -> "You have slept more than your goal across recent logs."
            else -> "You are running short against your goal across recent logs."
        }
    )
}

fun calculateGoalBalanceMinutes(
    sleepLogs: List<SleepEntry>,
    goalMinutes: Int
): Int {
    if (goalMinutes <= 0) return 0

    return sleepLogs.sumOf { entry ->
        entry.durationMinutes - goalMinutes
    }
}

fun buildSleepStreakSummary(
    sleepLogs: List<SleepEntry>,
    goalMinutes: Int
): SleepStreakSummary {
    return SleepStreakSummary(
        loggedDayStreak = calculateLoggedDayStreak(sleepLogs),
        nearGoalStreak = calculateNearGoalStreak(
            sleepLogs = sleepLogs,
            goalMinutes = goalMinutes
        )
    )
}

fun calculateLoggedDayStreak(sleepLogs: List<SleepEntry>): Int {
    val dayStarts = sleepLogs
        .map { startOfLocalDayMillis(it.dateMillis) }
        .distinct()
        .toSet()

    return calculateConsecutiveDayCount(dayStarts)
}

fun calculateNearGoalStreak(
    sleepLogs: List<SleepEntry>,
    goalMinutes: Int
): Int {
    if (goalMinutes <= 0) return 0

    val nearGoalDayStarts = sleepLogs
        .groupBy { startOfLocalDayMillis(it.dateMillis) }
        .filterValues { entries ->
            entries.any { entry ->
                abs(entry.durationMinutes - goalMinutes) <= NEAR_GOAL_TOLERANCE_MINUTES
            }
        }
        .keys
        .toSet()

    return calculateConsecutiveDayCount(nearGoalDayStarts)
}

fun buildPrimarySleepRecommendation(
    latestSleep: SleepEntry?,
    goalMinutes: Int,
    sleepGoalBalance: SleepGoalBalance,
    streakSummary: SleepStreakSummary,
    consistencyVariationMinutes: Int?,
    durationRangeMinutes: Int?
): String {
    if (latestSleep == null) {
        return "Log your next sleep to unlock a clear recommendation."
    }

    val difference = latestSleep.durationMinutes - goalMinutes

    return when {
        difference < -90 -> "Your latest sleep was short. Try moving bedtime 30 minutes earlier tonight."
        difference < -30 -> "You were close to your goal. A 15-20 minute earlier bedtime could help."
        consistencyVariationMinutes != null &&
                durationRangeMinutes != null &&
                SleepCalculator.calculateSleepConsistencyProgress(
                    consistencyVariationMinutes,
                    durationRangeMinutes
                ) < 0.5f -> "Your duration varies a lot. Keep bedtime and wake time steadier this week."

        sleepGoalBalance.balanceMinutes < -120 -> "Your recent logs are below goal. Protect one longer night soon."
        streakSummary.nearGoalStreak >= 3 -> "You are building a strong rhythm. Keep the same window tonight."
        else -> "Your sleep is on track. Keep logging so the trend stays useful."
    }
}

fun buildSleepMoodInsight(
    sleepLogs: List<SleepEntry>,
    moodEntries: List<MoodEntry>
): SleepMoodInsight? {
    val sleepByDate = sleepLogs
        .groupBy { SleepDateUtils.formatIsoDate(it.dateMillis) }
        .mapValues { (_, entries) ->
            entries.maxByOrNull { it.dateMillis }
        }

    val moodAverageByDate = moodEntries
        .groupBy { it.date }
        .mapValues { (_, entries) ->
            entries.map { it.moodValue }.average()
        }

    val matchedPairs = sleepByDate.mapNotNull { (date, sleepEntry) ->
        val moodAverage = moodAverageByDate[date]

        if (sleepEntry == null || moodAverage == null) {
            null
        } else {
            sleepEntry.durationMinutes.toDouble() to moodAverage
        }
    }

    if (matchedPairs.size < MIN_MATCHED_MOOD_DAYS) return null

    val correlation = calculateCorrelation(
        xValues = matchedPairs.map { it.first },
        yValues = matchedPairs.map { it.second }
    )

    return SleepMoodInsight(
        matchedDays = matchedPairs.size,
        correlation = correlation,
        title = when {
            correlation >= 0.35 -> "More sleep lines up with better mood"
            correlation <= -0.35 -> "Mood is not following sleep duration yet"
            else -> "Sleep and mood look steady"
        },
        description = when {
            correlation >= 0.35 -> "Across ${matchedPairs.size} matched days, longer sleep tends to pair with higher mood logs."
            correlation <= -0.35 -> "Across ${matchedPairs.size} matched days, mood does not improve just by sleeping longer."
            else -> "Across ${matchedPairs.size} matched days, mood looks fairly stable across different sleep lengths."
        }
    )
}

fun buildSleepTagInsight(
    sleepLogs: List<SleepEntry>
): SleepTagInsight? {
    val taggedSleepLogs = sleepLogs.filter { it.tags.isNotBlank() }
    if (taggedSleepLogs.isEmpty()) return null

    val topTag = taggedSleepLogs
        .flatMap { SleepTag.fromStorage(it.tags) }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?: return null

    val snoringLogs = sleepLogs.count { it.snoringLevel.name != "None" }
    val dreamLogs = sleepLogs.count { it.dreamJournal.isNotBlank() }

    return SleepTagInsight(
        title = "Most common tag: ${topTag.key.label}",
        description = "This appears in ${topTag.value} logs. You also have $snoringLogs snoring notes and $dreamLogs dream journal entries."
    )
}

private fun calculateGoalClosenessScore(
    durationMinutes: Int,
    goalMinutes: Int
): Int {
    if (goalMinutes <= 0) return 0

    val distanceRatio = abs(durationMinutes - goalMinutes).toDouble() / goalMinutes.toDouble()
    return (100 - (distanceRatio * 120)).roundToInt().coerceIn(0, 100)
}

private fun calculateConsecutiveDayCount(dayStarts: Set<Long>): Int {
    if (dayStarts.isEmpty()) return 0

    var expectedDayStart = dayStarts.maxOrNull() ?: return 0
    var count = 0

    while (dayStarts.contains(expectedDayStart)) {
        count++
        expectedDayStart = addDays(expectedDayStart, -1)
    }

    return count
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

private fun addDays(
    dateMillis: Long,
    days: Int
): Long {
    return Calendar.getInstance().apply {
        timeInMillis = dateMillis
        add(Calendar.DAY_OF_YEAR, days)
    }.timeInMillis
}

private fun calculateCorrelation(
    xValues: List<Double>,
    yValues: List<Double>
): Double {
    if (xValues.size != yValues.size || xValues.isEmpty()) return 0.0

    val averageX = xValues.average()
    val averageY = yValues.average()

    val numerator = xValues.indices.sumOf { index ->
        (xValues[index] - averageX) * (yValues[index] - averageY)
    }

    val xVariance = xValues.sumOf { value ->
        (value - averageX).pow(2)
    }

    val yVariance = yValues.sumOf { value ->
        (value - averageY).pow(2)
    }

    val denominator = sqrt(xVariance * yVariance)

    return if (denominator == 0.0) {
        0.0
    } else {
        numerator / denominator
    }
}
