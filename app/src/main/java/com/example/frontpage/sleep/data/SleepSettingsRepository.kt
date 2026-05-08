package com.example.frontpage.sleep.data

import android.net.Uri
import android.content.Context
import androidx.core.content.edit
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepDefaults
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

object SleepSettingsRepository {

    const val DEFAULT_SLEEP_GOAL_MINUTES: Int = SleepDefaults.SLEEP_GOAL_MINUTES
    const val DEFAULT_BEDTIME_MINUTES: Int = SleepDefaults.BEDTIME_MINUTES
    const val DEFAULT_WAKE_MINUTES: Int = SleepDefaults.WAKE_MINUTES

    private const val PREFERENCES_NAME = "sleep_settings"
    private const val GOAL_KEY_PREFIX = "sleep_goal_minutes_user_"
    private const val DATE_GOAL_KEY_PREFIX = "sleep_date_goal_minutes_user_"
    private const val WEEKDAY_GOAL_KEY_PREFIX = "sleep_weekday_goal_minutes_user_"
    private const val WEEKDAY_BEDTIME_KEY_PREFIX = "sleep_weekday_bedtime_minutes_user_"
    private const val WEEKDAY_WAKE_KEY_PREFIX = "sleep_weekday_wake_minutes_user_"
    private const val CUSTOM_TAGS_KEY_PREFIX = "sleep_custom_tags_user_"
    private const val CUSTOM_TAG_SEPARATOR = "|"

    fun getSleepGoalMinutes(
        context: Context,
        userId: Long?
    ): Int {
        return getSleepGoalMinutesForWeekday(
            context = context,
            userId = userId,
            weekday = SleepWeekday.fromDateMillis(System.currentTimeMillis())
        )
    }

    fun getSleepGoalMinutesForDate(
        context: Context,
        userId: Long?,
        dateMillis: Long
    ): Int {
        if (userId != null) {
            val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val dateGoalKey = dateGoalKey(
                userId = userId,
                dateMillis = dateMillis
            )

            if (preferences.contains(dateGoalKey)) {
                return preferences.getInt(
                    dateGoalKey,
                    DEFAULT_SLEEP_GOAL_MINUTES
                )
            }
        }

        return getSleepGoalMinutesForWeekday(
            context = context,
            userId = userId,
            weekday = SleepWeekday.fromDateMillis(dateMillis)
        )
    }

    fun getSleepGoalMinutesForWeekday(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday
    ): Int {
        if (userId == null) return DEFAULT_SLEEP_GOAL_MINUTES

        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val defaultGoal = preferences.getInt(
            goalKey(userId),
            DEFAULT_SLEEP_GOAL_MINUTES
        )

        return preferences.getInt(
            weekdayGoalKey(userId, weekday),
            defaultGoal
        )
    }

    fun updateSleepGoalMinutes(
        context: Context,
        userId: Long?,
        newGoalMinutes: Int
    ): Int {
        val goalMinutes = newGoalMinutes.coerceIn(
            minimumValue = 4 * 60,
            maximumValue = 12 * 60
        )

        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    putInt(
                        goalKey(userId),
                        goalMinutes
                    )

                    SleepWeekday.entries.forEach { weekday ->
                        putInt(
                            weekdayGoalKey(userId, weekday),
                            goalMinutes
                        )
                    }
                }
        }

        return goalMinutes
    }

    fun updateSleepGoalMinutesForDate(
        context: Context,
        userId: Long?,
        dateMillis: Long,
        newGoalMinutes: Int
    ): Int {
        val goalMinutes = newGoalMinutes.coerceIn(
            minimumValue = 4 * 60,
            maximumValue = 12 * 60
        )

        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    putInt(
                        dateGoalKey(
                            userId = userId,
                            dateMillis = dateMillis
                        ),
                        goalMinutes
                    )
                }
        }

        return goalMinutes
    }

    fun snapshotSleepGoalMinutesForDate(
        context: Context,
        userId: Long?,
        dateMillis: Long
    ) {
        if (userId == null) return

        val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val dateGoalKey = dateGoalKey(
            userId = userId,
            dateMillis = dateMillis
        )

        if (preferences.contains(dateGoalKey)) return

        val goalMinutes = getSleepGoalMinutesForDate(
            context = context,
            userId = userId,
            dateMillis = dateMillis
        )

        preferences.edit {
            putInt(
                dateGoalKey,
                goalMinutes
            )
        }
    }

    fun getWeekdaySleepSettings(
        context: Context,
        userId: Long?
    ): List<WeekdaySleepSettings> {
        return SleepWeekday.entries.map { weekday ->
            WeekdaySleepSettings(
                weekday = weekday,
                goalMinutes = getSleepGoalMinutesForWeekday(
                    context = context,
                    userId = userId,
                    weekday = weekday
                ),
                bedtimeMinutes = getBedtimeMinutesForWeekday(
                    context = context,
                    userId = userId,
                    weekday = weekday
                ),
                wakeMinutes = getWakeMinutesForWeekday(
                    context = context,
                    userId = userId,
                    weekday = weekday
                )
            )
        }
    }

    fun updateWeekdayGoalMinutes(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday,
        newGoalMinutes: Int
    ): List<WeekdaySleepSettings> {
        val goalMinutes = newGoalMinutes.coerceIn(
            minimumValue = 4 * 60,
            maximumValue = 12 * 60
        )

        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    putInt(
                        weekdayGoalKey(userId, weekday),
                        goalMinutes
                    )
                }
        }

        return getWeekdaySleepSettings(
            context = context,
            userId = userId
        )
    }

    fun updateWeekdayScheduleTargets(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings> {
        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    putInt(
                        weekdayBedtimeKey(userId, weekday),
                        normalizeClockMinutes(bedtimeMinutes)
                    )

                    putInt(
                        weekdayWakeKey(userId, weekday),
                        normalizeClockMinutes(wakeMinutes)
                    )
                }
        }

        return getWeekdaySleepSettings(
            context = context,
            userId = userId
        )
    }

    fun updateAllWeekdayScheduleTargets(
        context: Context,
        userId: Long?,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings> {
        if (userId != null) {
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit {
                    SleepWeekday.entries.forEach { weekday ->
                        putInt(
                            weekdayBedtimeKey(userId, weekday),
                            normalizeClockMinutes(bedtimeMinutes)
                        )

                        putInt(
                            weekdayWakeKey(userId, weekday),
                            normalizeClockMinutes(wakeMinutes)
                        )
                    }
                }
        }

        return getWeekdaySleepSettings(
            context = context,
            userId = userId
        )
    }

    fun getCustomTags(
        context: Context,
        userId: Long?
    ): List<SleepCustomTag> {
        if (userId == null) return emptyList()

        val storedTags = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(customTagsKey(userId), "")
            .orEmpty()

        if (storedTags.isBlank()) return emptyList()

        return storedTags.lines()
            .mapNotNull { line -> customTagFromStorageLine(line) }
    }

    fun addCustomTag(
        context: Context,
        userId: Long?,
        label: String
    ): List<SleepCustomTag> {
        if (userId == null) return emptyList()

        val cleanLabel = label.trim()
        if (cleanLabel.isBlank()) {
            return getCustomTags(
                context = context,
                userId = userId
            )
        }

        val existingTags = getCustomTags(
            context = context,
            userId = userId
        )

        if (existingTags.any { tag -> tag.label.equals(cleanLabel, ignoreCase = true) }) {
            return existingTags
        }

        val newTag = SleepCustomTag(
            id = System.currentTimeMillis().toString(36),
            label = cleanLabel
        )

        val updatedTags = existingTags + newTag
        saveCustomTags(
            context = context,
            userId = userId,
            tags = updatedTags
        )

        return updatedTags
    }

    fun deleteCustomTag(
        context: Context,
        userId: Long?,
        tagId: String
    ): List<SleepCustomTag> {
        if (userId == null) return emptyList()

        val updatedTags = getCustomTags(
            context = context,
            userId = userId
        ).filterNot { tag ->
            tag.id == tagId
        }

        saveCustomTags(
            context = context,
            userId = userId,
            tags = updatedTags
        )

        return updatedTags
    }

    private fun goalKey(userId: Long): String {
        return "$GOAL_KEY_PREFIX$userId"
    }

    private fun dateGoalKey(
        userId: Long,
        dateMillis: Long
    ): String {
        return "$DATE_GOAL_KEY_PREFIX${userId}_${SleepDateUtils.formatIsoDate(dateMillis)}"
    }

    private fun weekdayGoalKey(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_GOAL_KEY_PREFIX${userId}_${weekday.name}"
    }

    private fun weekdayBedtimeKey(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_BEDTIME_KEY_PREFIX${userId}_${weekday.name}"
    }

    private fun weekdayWakeKey(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_WAKE_KEY_PREFIX${userId}_${weekday.name}"
    }

    private fun customTagsKey(userId: Long): String {
        return "$CUSTOM_TAGS_KEY_PREFIX$userId"
    }

    private fun getBedtimeMinutesForWeekday(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday
    ): Int {
        if (userId == null) return DEFAULT_BEDTIME_MINUTES

        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getInt(
                weekdayBedtimeKey(userId, weekday),
                DEFAULT_BEDTIME_MINUTES
            )
    }

    private fun getWakeMinutesForWeekday(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday
    ): Int {
        if (userId == null) return DEFAULT_WAKE_MINUTES

        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getInt(
                weekdayWakeKey(userId, weekday),
                DEFAULT_WAKE_MINUTES
            )
    }

    private fun saveCustomTags(
        context: Context,
        userId: Long,
        tags: List<SleepCustomTag>
    ) {
        val storedTags = tags.joinToString("\n") { tag ->
            "${tag.id}$CUSTOM_TAG_SEPARATOR${Uri.encode(tag.label)}"
        }

        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(
                    customTagsKey(userId),
                    storedTags
                )
            }
    }

    private fun customTagFromStorageLine(line: String): SleepCustomTag? {
        val parts = line.split(CUSTOM_TAG_SEPARATOR, limit = 2)
        val id = parts.getOrNull(0)?.takeIf { value -> value.isNotBlank() }
            ?: return null

        val label = parts.getOrNull(1)
            ?.let { encodedLabel -> Uri.decode(encodedLabel) }
            ?.takeIf { decodedLabel -> decodedLabel.isNotBlank() }
            ?: return null

        return SleepCustomTag(
            id = id,
            label = label
        )
    }

    private fun normalizeClockMinutes(minutes: Int): Int {
        val minutesInDay = 24 * 60
        return ((minutes % minutesInDay) + minutesInDay) % minutesInDay
    }
}
