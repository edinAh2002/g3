package com.example.frontpage.sleep.ui.pages

import androidx.compose.runtime.Composable
import com.example.frontpage.sleep.domain.SleepDashboardState
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageSectionId
import com.example.frontpage.sleep.ui.components.SleepCustomizablePageContent

@Composable
fun SleepOverviewPage(
    dashboardState: SleepDashboardState,
    sleepLogs: List<SleepEntry>,
    pageLayout: SleepPageLayout,
    isEditing: Boolean,
    onRemoveSection: (SleepPageSectionId) -> Unit,
    onMoveSectionUp: (SleepPageSectionId) -> Unit,
    onMoveSectionDown: (SleepPageSectionId) -> Unit,
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    SleepCustomizablePageContent(
        pageKey = SleepPageKey.Overview,
        layout = pageLayout,
        dashboardState = dashboardState,
        sleepLogs = sleepLogs,
        isEditing = isEditing,
        onRemoveSection = onRemoveSection,
        onMoveSectionUp = onMoveSectionUp,
        onMoveSectionDown = onMoveSectionDown,
        onLogSleepClick = onLogSleepClick,
        onEditGoalClick = onEditGoalClick
    )
}
