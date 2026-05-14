package com.example.frontpage.sleep.data

import android.content.Context
import androidx.core.content.edit
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepDefaults
import com.example.frontpage.sleep.model.SleepDetectionSettings
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageLayoutDefaults
import com.example.frontpage.sleep.model.SleepPageSectionId
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

object SleepSettingsRepository {

    const val DEFAULT_SLEEP_GOAL_MINUTES: Int = SleepDefaults.SLEEP_GOAL_MINUTES
    const val DEFAULT_BEDTIME_MINUTES: Int = SleepDefaults.BEDTIME_MINUTES
    const val DEFAULT_WAKE_MINUTES: Int = SleepDefaults.WAKE_MINUTES

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
            val preferences = preferences(context)
            val dateGoalKey = SleepSettingsStorageKeys.dateGoal(
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

        val preferences = preferences(context)
        val defaultGoal = preferences.getInt(
            SleepSettingsStorageKeys.goal(userId),
            DEFAULT_SLEEP_GOAL_MINUTES
        )

        return preferences.getInt(
            SleepSettingsStorageKeys.weekdayGoal(userId, weekday),
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
            preferences(context)
                .edit {
                    putInt(
                        SleepSettingsStorageKeys.goal(userId),
                        goalMinutes
                    )

                    SleepWeekday.entries.forEach { weekday ->
                        putInt(
                            SleepSettingsStorageKeys.weekdayGoal(userId, weekday),
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
            preferences(context)
                .edit {
                    putInt(
                        SleepSettingsStorageKeys.dateGoal(
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

        val preferences = preferences(context)
        val dateGoalKey = SleepSettingsStorageKeys.dateGoal(
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
            preferences(context)
                .edit {
                    putInt(
                        SleepSettingsStorageKeys.weekdayGoal(userId, weekday),
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
            preferences(context)
                .edit {
                    putInt(
                        SleepSettingsStorageKeys.weekdayBedtime(userId, weekday),
                        normalizeClockMinutes(bedtimeMinutes)
                    )

                    putInt(
                        SleepSettingsStorageKeys.weekdayWake(userId, weekday),
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
            preferences(context)
                .edit {
                    SleepWeekday.entries.forEach { weekday ->
                        putInt(
                            SleepSettingsStorageKeys.weekdayBedtime(userId, weekday),
                            normalizeClockMinutes(bedtimeMinutes)
                        )

                        putInt(
                            SleepSettingsStorageKeys.weekdayWake(userId, weekday),
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

        val storedTags = preferences(context)
            .getString(SleepSettingsStorageKeys.customTags(userId), "")
            .orEmpty()

        return SleepSettingsStorageCodec.decodeCustomTags(storedTags)
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

    fun getSleepPageLayout(
        context: Context,
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout {
        if (userId == null) {
            return SleepPageLayout(
                pageKey = pageKey,
                sectionIds = defaultSectionIds
            )
        }

        val storedLayout = preferences(context)
            .getString(SleepSettingsStorageKeys.pageLayout(userId, pageKey), null)

        val sectionIds = storedLayout?.let { layout ->
            SleepSettingsStorageCodec.decodePageSectionIds(layout)
        } ?: defaultSectionIds

        return SleepPageLayout(
            pageKey = pageKey,
            sectionIds = SleepPageLayoutDefaults.normalizeSectionIds(sectionIds)
        )
    }

    fun updateSleepPageLayout(
        context: Context,
        userId: Long?,
        layout: SleepPageLayout
    ): SleepPageLayout {
        if (userId != null) {
            preferences(context)
                .edit {
                    putString(
                        SleepSettingsStorageKeys.pageLayout(userId, layout.pageKey),
                        SleepSettingsStorageCodec.encodePageSectionIds(
                            SleepPageLayoutDefaults.normalizeSectionIds(layout.sectionIds)
                        )
                    )
                }
        }

        return layout.copy(
            sectionIds = SleepPageLayoutDefaults.normalizeSectionIds(layout.sectionIds)
        )
    }

    fun resetSleepPageLayout(
        context: Context,
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout {
        if (userId != null) {
            preferences(context)
                .edit {
                    remove(SleepSettingsStorageKeys.pageLayout(userId, pageKey))
                }
        }

        return SleepPageLayout(
            pageKey = pageKey,
            sectionIds = defaultSectionIds
        )
    }

    fun getSleepDetectionSettings(
        context: Context,
        userId: Long?
    ): SleepDetectionSettings {
        if (userId == null) return SleepDetectionSettings()

        val preferences = preferences(context)
        return SleepDetectionSettings(
            enabled = preferences.getBoolean(
                SleepSettingsStorageKeys.detectionEnabled(userId),
                false
            ),
            minimumSleepMinutes = preferences.getInt(
                SleepSettingsStorageKeys.detectionMinimumMinutes(userId),
                SleepDetectionSettings.DEFAULT_MINIMUM_SLEEP_MINUTES
            ),
            alarmMatchWindowMinutes = preferences.getInt(
                SleepSettingsStorageKeys.detectionAlarmWindowMinutes(userId),
                SleepDetectionSettings.DEFAULT_ALARM_MATCH_WINDOW_MINUTES
            ),
            interruptionToleranceMinutes = preferences.getInt(
                SleepSettingsStorageKeys.detectionInterruptionMinutes(userId),
                SleepDetectionSettings.DEFAULT_INTERRUPTION_TOLERANCE_MINUTES
            )
        ).normalized()
    }

    fun updateSleepDetectionSettings(
        context: Context,
        userId: Long?,
        settings: SleepDetectionSettings
    ): SleepDetectionSettings {
        val normalizedSettings = settings.normalized()
        if (userId != null) {
            preferences(context)
                .edit {
                    putBoolean(
                        SleepSettingsStorageKeys.detectionEnabled(userId),
                        normalizedSettings.enabled
                    )
                    putInt(
                        SleepSettingsStorageKeys.detectionMinimumMinutes(userId),
                        normalizedSettings.minimumSleepMinutes
                    )
                    putInt(
                        SleepSettingsStorageKeys.detectionAlarmWindowMinutes(userId),
                        normalizedSettings.alarmMatchWindowMinutes
                    )
                    putInt(
                        SleepSettingsStorageKeys.detectionInterruptionMinutes(userId),
                        normalizedSettings.interruptionToleranceMinutes
                    )
                }
        }

        return normalizedSettings
    }

    private fun getBedtimeMinutesForWeekday(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday
    ): Int {
        if (userId == null) return DEFAULT_BEDTIME_MINUTES

        return preferences(context)
            .getInt(
                SleepSettingsStorageKeys.weekdayBedtime(userId, weekday),
                DEFAULT_BEDTIME_MINUTES
            )
    }

    private fun getWakeMinutesForWeekday(
        context: Context,
        userId: Long?,
        weekday: SleepWeekday
    ): Int {
        if (userId == null) return DEFAULT_WAKE_MINUTES

        return preferences(context)
            .getInt(
                SleepSettingsStorageKeys.weekdayWake(userId, weekday),
                DEFAULT_WAKE_MINUTES
            )
    }

    private fun saveCustomTags(
        context: Context,
        userId: Long,
        tags: List<SleepCustomTag>
    ) {
        preferences(context)
            .edit {
                putString(
                    SleepSettingsStorageKeys.customTags(userId),
                    SleepSettingsStorageCodec.encodeCustomTags(tags)
                )
            }
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(
            SleepSettingsStorageKeys.PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    private fun normalizeClockMinutes(minutes: Int): Int {
        val minutesInDay = 24 * 60
        return ((minutes % minutesInDay) + minutesInDay) % minutesInDay
    }

    private fun SleepDetectionSettings.normalized(): SleepDetectionSettings {
        return copy(
            minimumSleepMinutes = minimumSleepMinutes.coerceIn(2 * 60, 12 * 60),
            alarmMatchWindowMinutes = alarmMatchWindowMinutes.coerceIn(15, 180),
            interruptionToleranceMinutes = interruptionToleranceMinutes.coerceIn(0, 90)
        )
    }
}


