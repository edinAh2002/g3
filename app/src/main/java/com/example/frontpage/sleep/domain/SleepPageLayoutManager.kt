package com.example.frontpage.sleep.domain

import com.example.frontpage.sleep.data.SleepSettingsDataSource
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageLayoutDefaults
import com.example.frontpage.sleep.model.SleepPageSectionId

class SleepPageLayoutManager(
    private val settingsDataSource: SleepSettingsDataSource
) {
    fun loadLayouts(userId: Long?): Map<SleepPageKey, SleepPageLayout> {
        return SleepPageKey.entries.associateWith { pageKey ->
            settingsDataSource.getSleepPageLayout(
                userId = userId,
                pageKey = pageKey,
                defaultSectionIds = SleepPageLayoutDefaults.defaultSectionIds(pageKey)
            )
        }
    }

    fun addSection(
        userId: Long?,
        currentLayouts: Map<SleepPageKey, SleepPageLayout>,
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ): SleepPageLayout {
        val currentLayout = currentPageLayout(
            layouts = currentLayouts,
            pageKey = pageKey
        )

        if (sectionId in currentLayout.sectionIds) return currentLayout

        return saveLayout(
            userId = userId,
            layout = currentLayout.copy(
                sectionIds = currentLayout.sectionIds + sectionId
            )
        )
    }

    fun removeSection(
        userId: Long?,
        currentLayouts: Map<SleepPageKey, SleepPageLayout>,
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId
    ): SleepPageLayout {
        val currentLayout = currentPageLayout(
            layouts = currentLayouts,
            pageKey = pageKey
        )

        return saveLayout(
            userId = userId,
            layout = currentLayout.copy(
                sectionIds = currentLayout.sectionIds.filterNot { currentSectionId ->
                    currentSectionId == sectionId
                }
            )
        )
    }

    fun moveSection(
        userId: Long?,
        currentLayouts: Map<SleepPageKey, SleepPageLayout>,
        pageKey: SleepPageKey,
        sectionId: SleepPageSectionId,
        offset: Int
    ): SleepPageLayout {
        val currentLayout = currentPageLayout(
            layouts = currentLayouts,
            pageKey = pageKey
        )
        val currentIndex = currentLayout.sectionIds.indexOf(sectionId)
        val newIndex = currentIndex + offset

        if (currentIndex == -1 || newIndex !in currentLayout.sectionIds.indices) {
            return currentLayout
        }

        val reorderedSectionIds = currentLayout.sectionIds.toMutableList()
        val movedSectionId = reorderedSectionIds.removeAt(currentIndex)
        reorderedSectionIds.add(newIndex, movedSectionId)

        return saveLayout(
            userId = userId,
            layout = currentLayout.copy(
                sectionIds = reorderedSectionIds
            )
        )
    }

    fun resetLayout(
        userId: Long?,
        pageKey: SleepPageKey
    ): SleepPageLayout {
        return settingsDataSource.resetSleepPageLayout(
            userId = userId,
            pageKey = pageKey,
            defaultSectionIds = SleepPageLayoutDefaults.defaultSectionIds(pageKey)
        )
    }

    private fun currentPageLayout(
        layouts: Map<SleepPageKey, SleepPageLayout>,
        pageKey: SleepPageKey
    ): SleepPageLayout {
        return layouts[pageKey] ?: SleepPageLayoutDefaults.defaultLayout(pageKey)
    }

    private fun saveLayout(
        userId: Long?,
        layout: SleepPageLayout
    ): SleepPageLayout {
        return settingsDataSource.updateSleepPageLayout(
            userId = userId,
            layout = layout
        )
    }
}
