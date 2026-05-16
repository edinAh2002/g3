package com.example.frontpage.sleep.model

enum class SleepPageKey(
    val storageValue: String
) {
    Overview("overview"),
    History("history"),
    Insights("insights"),
    Settings("settings");

    companion object {
        fun fromStorageValue(storageValue: String): SleepPageKey? {
            return entries.firstOrNull { pageKey ->
                pageKey.storageValue == storageValue
            }
        }
    }
}

enum class SleepPageSectionId(
    val storageValue: String
) {
    LatestSleep("latest_sleep"),
    GoalPerformance("goal_performance"),
    KeyNumbers("key_numbers"),
    WeeklyChart("weekly_chart"),
    Records("records"),
    Recommendation("recommendation"),
    Snapshot("snapshot"),
    Streaks("streaks"),
    Patterns("patterns"),
    SleepTimingTrends("sleep_timing_trends"),
    SleepConsistency("sleep_consistency"),
    Connections("connections");

    companion object {
        fun fromStorageValue(storageValue: String): SleepPageSectionId? {
            return entries.firstOrNull { sectionId ->
                sectionId.storageValue == storageValue
            }
        }
    }
}

data class SleepPageLayout(
    val pageKey: SleepPageKey,
    val sectionIds: List<SleepPageSectionId>
)

object SleepPageLayoutDefaults {
    fun defaultLayout(pageKey: SleepPageKey): SleepPageLayout {
        return SleepPageLayout(
            pageKey = pageKey,
            sectionIds = defaultSectionIds(pageKey)
        )
    }

    fun defaultLayouts(): Map<SleepPageKey, SleepPageLayout> {
        return SleepPageKey.entries.associateWith { pageKey ->
            defaultLayout(pageKey)
        }
    }

    fun defaultSectionIds(pageKey: SleepPageKey): List<SleepPageSectionId> {
        return when (pageKey) {
            SleepPageKey.Overview -> listOf(
                SleepPageSectionId.LatestSleep,
                SleepPageSectionId.GoalPerformance,
                SleepPageSectionId.WeeklyChart
            )

            SleepPageKey.Insights -> listOf(
                SleepPageSectionId.Recommendation,
                SleepPageSectionId.GoalPerformance,
                SleepPageSectionId.Snapshot,
                SleepPageSectionId.Streaks,
                SleepPageSectionId.WeeklyChart,
                SleepPageSectionId.SleepTimingTrends,
                SleepPageSectionId.SleepConsistency,
                SleepPageSectionId.Connections
            )

            SleepPageKey.History,
            SleepPageKey.Settings -> emptyList()
        }
    }

    fun normalizeSectionIds(sectionIds: List<SleepPageSectionId>): List<SleepPageSectionId> {
        return sectionIds
            .flatMap { sectionId -> canonicalSectionIds(sectionId) }
            .distinct()
    }

    private fun canonicalSectionIds(sectionId: SleepPageSectionId): List<SleepPageSectionId> {
        return when (sectionId) {
            SleepPageSectionId.KeyNumbers,
            SleepPageSectionId.Records -> listOf(SleepPageSectionId.Snapshot)
            SleepPageSectionId.Patterns -> listOf(
                SleepPageSectionId.SleepTimingTrends,
                SleepPageSectionId.SleepConsistency
            )
            else -> listOf(sectionId)
        }
    }
}
