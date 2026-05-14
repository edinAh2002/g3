package com.example.frontpage.sleep.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepDashboardState
import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageSectionId
import com.example.frontpage.sleep.ui.components.SleepDetectionSuggestionCard
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
    onEditGoalClick: () -> Unit,
    pendingDetectionCandidate: SleepDetectionCandidate?,
    onReviewDetectedSleep: (SleepDetectionCandidate) -> Unit,
    onDismissDetectedSleep: (SleepDetectionCandidate) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        pendingDetectionCandidate?.let { candidate ->
            SleepDetectionSuggestionCard(
                candidate = candidate,
                onReview = {
                    onReviewDetectedSleep(candidate)
                },
                onDismiss = {
                    onDismissDetectedSleep(candidate)
                }
            )
        }

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
}
