package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepWeekday

internal object SleepSettingsStorageKeys {
    const val PREFERENCES_NAME = "sleep_settings"

    private const val GOAL_KEY_PREFIX = "sleep_goal_minutes_user_"
    private const val DATE_GOAL_KEY_PREFIX = "sleep_date_goal_minutes_user_"
    private const val WEEKDAY_GOAL_KEY_PREFIX = "sleep_weekday_goal_minutes_user_"
    private const val WEEKDAY_BEDTIME_KEY_PREFIX = "sleep_weekday_bedtime_minutes_user_"
    private const val WEEKDAY_WAKE_KEY_PREFIX = "sleep_weekday_wake_minutes_user_"
    private const val CUSTOM_TAGS_KEY_PREFIX = "sleep_custom_tags_user_"
    private const val PAGE_LAYOUT_KEY_PREFIX = "sleep_page_layout_user_"

    fun goal(userId: Long): String {
        return "$GOAL_KEY_PREFIX$userId"
    }

    fun dateGoal(
        userId: Long,
        dateMillis: Long
    ): String {
        return "$DATE_GOAL_KEY_PREFIX${userId}_${SleepDateUtils.formatIsoDate(dateMillis)}"
    }

    fun weekdayGoal(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_GOAL_KEY_PREFIX${userId}_${weekday.name}"
    }

    fun weekdayBedtime(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_BEDTIME_KEY_PREFIX${userId}_${weekday.name}"
    }

    fun weekdayWake(
        userId: Long,
        weekday: SleepWeekday
    ): String {
        return "$WEEKDAY_WAKE_KEY_PREFIX${userId}_${weekday.name}"
    }

    fun customTags(userId: Long): String {
        return "$CUSTOM_TAGS_KEY_PREFIX$userId"
    }

    fun pageLayout(
        userId: Long,
        pageKey: SleepPageKey
    ): String {
        return "$PAGE_LAYOUT_KEY_PREFIX${userId}_${pageKey.storageValue}"
    }

}
