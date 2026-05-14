package com.example.frontpage.sleep.data

import android.content.Context
import com.example.frontpage.sleep.model.SleepCustomTag
import com.example.frontpage.sleep.model.SleepDetectionSettings
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageSectionId
import com.example.frontpage.sleep.model.SleepWeekday
import com.example.frontpage.sleep.model.WeekdaySleepSettings

interface SleepSettingsDataSource {
    val defaultSleepGoalMinutes: Int

    fun getSleepGoalMinutes(userId: Long?): Int

    fun getSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long
    ): Int

    fun updateSleepGoalMinutes(
        userId: Long?,
        newGoalMinutes: Int
    ): Int

    fun updateSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long,
        newGoalMinutes: Int
    ): Int

    fun snapshotSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long
    )

    fun getWeekdaySleepSettings(userId: Long?): List<WeekdaySleepSettings>

    fun updateWeekdayGoalMinutes(
        userId: Long?,
        weekday: SleepWeekday,
        newGoalMinutes: Int
    ): List<WeekdaySleepSettings>

    fun updateWeekdayScheduleTargets(
        userId: Long?,
        weekday: SleepWeekday,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings>

    fun updateAllWeekdayScheduleTargets(
        userId: Long?,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings>

    fun getCustomTags(userId: Long?): List<SleepCustomTag>

    fun addCustomTag(
        userId: Long?,
        label: String
    ): List<SleepCustomTag>

    fun deleteCustomTag(
        userId: Long?,
        tagId: String
    ): List<SleepCustomTag>

    fun getSleepPageLayout(
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout

    fun updateSleepPageLayout(
        userId: Long?,
        layout: SleepPageLayout
    ): SleepPageLayout

    fun resetSleepPageLayout(
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout

    fun getSleepDetectionSettings(userId: Long?): SleepDetectionSettings

    fun updateSleepDetectionSettings(
        userId: Long?,
        settings: SleepDetectionSettings
    ): SleepDetectionSettings

}

class SharedPreferencesSleepSettingsDataSource(
    private val context: Context
) : SleepSettingsDataSource {

    override val defaultSleepGoalMinutes: Int = SleepSettingsRepository.DEFAULT_SLEEP_GOAL_MINUTES

    override fun getSleepGoalMinutes(userId: Long?): Int {
        return SleepSettingsRepository.getSleepGoalMinutes(
            context = context,
            userId = userId
        )
    }

    override fun getSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long
    ): Int {
        return SleepSettingsRepository.getSleepGoalMinutesForDate(
            context = context,
            userId = userId,
            dateMillis = dateMillis
        )
    }

    override fun updateSleepGoalMinutes(
        userId: Long?,
        newGoalMinutes: Int
    ): Int {
        return SleepSettingsRepository.updateSleepGoalMinutes(
            context = context,
            userId = userId,
            newGoalMinutes = newGoalMinutes
        )
    }

    override fun updateSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long,
        newGoalMinutes: Int
    ): Int {
        return SleepSettingsRepository.updateSleepGoalMinutesForDate(
            context = context,
            userId = userId,
            dateMillis = dateMillis,
            newGoalMinutes = newGoalMinutes
        )
    }

    override fun snapshotSleepGoalMinutesForDate(
        userId: Long?,
        dateMillis: Long
    ) {
        SleepSettingsRepository.snapshotSleepGoalMinutesForDate(
            context = context,
            userId = userId,
            dateMillis = dateMillis
        )
    }

    override fun getWeekdaySleepSettings(userId: Long?): List<WeekdaySleepSettings> {
        return SleepSettingsRepository.getWeekdaySleepSettings(
            context = context,
            userId = userId
        )
    }

    override fun updateWeekdayGoalMinutes(
        userId: Long?,
        weekday: SleepWeekday,
        newGoalMinutes: Int
    ): List<WeekdaySleepSettings> {
        return SleepSettingsRepository.updateWeekdayGoalMinutes(
            context = context,
            userId = userId,
            weekday = weekday,
            newGoalMinutes = newGoalMinutes
        )
    }

    override fun updateWeekdayScheduleTargets(
        userId: Long?,
        weekday: SleepWeekday,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings> {
        return SleepSettingsRepository.updateWeekdayScheduleTargets(
            context = context,
            userId = userId,
            weekday = weekday,
            bedtimeMinutes = bedtimeMinutes,
            wakeMinutes = wakeMinutes
        )
    }

    override fun updateAllWeekdayScheduleTargets(
        userId: Long?,
        bedtimeMinutes: Int,
        wakeMinutes: Int
    ): List<WeekdaySleepSettings> {
        return SleepSettingsRepository.updateAllWeekdayScheduleTargets(
            context = context,
            userId = userId,
            bedtimeMinutes = bedtimeMinutes,
            wakeMinutes = wakeMinutes
        )
    }

    override fun getCustomTags(userId: Long?): List<SleepCustomTag> {
        return SleepSettingsRepository.getCustomTags(
            context = context,
            userId = userId
        )
    }

    override fun addCustomTag(
        userId: Long?,
        label: String
    ): List<SleepCustomTag> {
        return SleepSettingsRepository.addCustomTag(
            context = context,
            userId = userId,
            label = label
        )
    }

    override fun deleteCustomTag(
        userId: Long?,
        tagId: String
    ): List<SleepCustomTag> {
        return SleepSettingsRepository.deleteCustomTag(
            context = context,
            userId = userId,
            tagId = tagId
        )
    }

    override fun getSleepPageLayout(
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout {
        return SleepSettingsRepository.getSleepPageLayout(
            context = context,
            userId = userId,
            pageKey = pageKey,
            defaultSectionIds = defaultSectionIds
        )
    }

    override fun updateSleepPageLayout(
        userId: Long?,
        layout: SleepPageLayout
    ): SleepPageLayout {
        return SleepSettingsRepository.updateSleepPageLayout(
            context = context,
            userId = userId,
            layout = layout
        )
    }

    override fun resetSleepPageLayout(
        userId: Long?,
        pageKey: SleepPageKey,
        defaultSectionIds: List<SleepPageSectionId>
    ): SleepPageLayout {
        return SleepSettingsRepository.resetSleepPageLayout(
            context = context,
            userId = userId,
            pageKey = pageKey,
            defaultSectionIds = defaultSectionIds
        )
    }

    override fun getSleepDetectionSettings(userId: Long?): SleepDetectionSettings {
        return SleepSettingsRepository.getSleepDetectionSettings(
            context = context,
            userId = userId
        )
    }

    override fun updateSleepDetectionSettings(
        userId: Long?,
        settings: SleepDetectionSettings
    ): SleepDetectionSettings {
        return SleepSettingsRepository.updateSleepDetectionSettings(
            context = context,
            userId = userId,
            settings = settings
        )
    }

}
