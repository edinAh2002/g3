package com.example.frontpage.sleep.ui

import com.example.frontpage.sleep.model.SleepPageKey

internal enum class SleepPage(
    val label: String,
    val pageKey: SleepPageKey
) {
    Overview("Overview", SleepPageKey.Overview),
    History("History", SleepPageKey.History),
    Insights("Insights", SleepPageKey.Insights),
    Settings("Settings", SleepPageKey.Settings)
}
