package com.example.frontpage.sleep.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.sleep.domain.SleepCalculator
import com.example.frontpage.sleep.domain.SleepDashboardState
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.model.SleepPageKey
import com.example.frontpage.sleep.model.SleepPageLayout
import com.example.frontpage.sleep.model.SleepPageSectionId

internal data class SleepPageSectionRenderContext(
    val pageKey: SleepPageKey,
    val dashboardState: SleepDashboardState,
    val sleepLogs: List<SleepEntry>,
    val onLogSleepClick: () -> Unit,
    val onEditGoalClick: () -> Unit,
    val editControls: SleepSectionEditControls? = null
)

data class SleepSectionEditControls(
    val canMoveUp: Boolean,
    val canMoveDown: Boolean,
    val onMoveUp: () -> Unit,
    val onMoveDown: () -> Unit,
    val onRemove: () -> Unit
)

internal abstract class SleepPageSectionDefinition(
    val id: SleepPageSectionId,
    val title: String,
    val description: String,
    val supportedPages: Set<SleepPageKey>
) {
    @Composable
    abstract fun Content(context: SleepPageSectionRenderContext)
}

internal object SleepPageSectionRegistry {
    private val definitions: List<SleepPageSectionDefinition> = listOf(
        LatestSleepSection,
        GoalPerformanceSection,
        WeeklyChartSection,
        RecommendationSection,
        SnapshotSection,
        StreaksSection,
        SleepTimingTrendsSection,
        SleepConsistencySection,
        ConnectionsSection
    )

    fun sectionsFor(pageKey: SleepPageKey): List<SleepPageSectionDefinition> {
        return definitions.filter { definition ->
            pageKey in definition.supportedPages
        }
    }

    fun visibleSections(
        pageKey: SleepPageKey,
        layout: SleepPageLayout
    ): List<SleepPageSectionDefinition> {
        return layout.sectionIds.mapNotNull { sectionId ->
            sectionsFor(pageKey).firstOrNull { definition ->
                definition.id == sectionId
            }
        }
    }
}

@Composable
internal fun SleepCustomizablePageContent(
    pageKey: SleepPageKey,
    layout: SleepPageLayout,
    dashboardState: SleepDashboardState,
    sleepLogs: List<SleepEntry>,
    isEditing: Boolean,
    onRemoveSection: (SleepPageSectionId) -> Unit,
    onMoveSectionUp: (SleepPageSectionId) -> Unit,
    onMoveSectionDown: (SleepPageSectionId) -> Unit,
    onLogSleepClick: () -> Unit,
    onEditGoalClick: () -> Unit
) {
    val baseContext = SleepPageSectionRenderContext(
        pageKey = pageKey,
        dashboardState = dashboardState,
        sleepLogs = sleepLogs,
        onLogSleepClick = onLogSleepClick,
        onEditGoalClick = onEditGoalClick
    )

    val visibleSections = SleepPageSectionRegistry.visibleSections(
        pageKey = pageKey,
        layout = layout
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (visibleSections.isEmpty()) {
            EmptyPageLayoutCard()
        }

        visibleSections.forEachIndexed { index, section ->
            val sectionContext = if (isEditing) {
                baseContext.copy(
                    editControls = SleepSectionEditControls(
                        canMoveUp = index > 0,
                        canMoveDown = index < visibleSections.lastIndex,
                        onMoveUp = {
                            onMoveSectionUp(section.id)
                        },
                        onMoveDown = {
                            onMoveSectionDown(section.id)
                        },
                        onRemove = {
                            onRemoveSection(section.id)
                        }
                    )
                )
            } else {
                baseContext
            }

            section.Content(sectionContext)
        }
    }
}

@Composable
private fun EmptyPageLayoutCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "No sections shown",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Use Edit to add sections back to this page.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private object LatestSleepSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.LatestSleep,
    title = "Most Recent Sleep Log",
    description = "Shows your newest saved sleep entry, goal progress, and quick log actions.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        LatestSleepCard(
            latestSleep = context.dashboardState.latestSleep,
            goalMinutes = context.dashboardState.latestGoalMinutes,
            onLogSleepClick = context.onLogSleepClick,
            onEditGoalClick = context.onEditGoalClick,
            showTitle = false
        )
    }
}

private object GoalPerformanceSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.GoalPerformance,
    title = "Sleep Score and Goal Balance",
    description = "Shows your sleep score and seven-day goal balance.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            SleepScoreCard(
                scoreSummary = context.dashboardState.sleepScoreSummary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            SleepGoalBalanceCard(
                goalBalance = context.dashboardState.sleepGoalBalance,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

private object WeeklyChartSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.WeeklyChart,
    title = "Seven-Day Sleep Duration Chart",
    description = "Shows sleep duration by wake day for the last seven days.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        WeeklySleepChart(
            chartData = context.dashboardState.weeklyChartData,
            goalMinutes = context.dashboardState.latestGoalMinutes,
            showTitle = false
        )
    }
}

private object RecommendationSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.Recommendation,
    title = "Personal Sleep Recommendation",
    description = "Shows the main suggested action from your recent sleep data.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        SleepRecommendationCard(
            recommendation = context.dashboardState.primaryRecommendation
        )
    }
}

private object SnapshotSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.Snapshot,
    title = "Sleep Log Metrics Snapshot",
    description = "Shows average sleep, total logs, longest sleep, and shortest sleep.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Average",
                value = SleepCalculator.formatDuration(context.dashboardState.averageSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Logs",
                value = context.sleepLogs.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SleepMetricTile(
                title = "Longest",
                value = SleepCalculator.formatDuration(context.dashboardState.longestSleepMinutes),
                modifier = Modifier.weight(1f)
            )

            SleepMetricTile(
                title = "Shortest",
                value = SleepCalculator.formatDuration(context.dashboardState.shortestSleepMinutes),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private object StreaksSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.Streaks,
    title = "Sleep Streak Summary",
    description = "Shows logged-day and near-goal streak counts.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        SleepStreakCard(
            streakSummary = context.dashboardState.streakSummary
        )
    }
}

private object SleepTimingTrendsSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.SleepTimingTrends,
    title = "Bedtime and Wake Time Trends",
    description = "Shows average bedtime, wake time, and sleep duration trends.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        SleepTrendsCard(
            sleepLogs = context.sleepLogs,
            averageBedtimeMinutes = context.dashboardState.averageBedtimeMinutes,
            averageWakeTimeMinutes = context.dashboardState.averageWakeTimeMinutes,
            averageDurationMinutes = context.dashboardState.averageSleepMinutes
        )
    }
}

private object SleepConsistencySection : SleepPageSectionDefinition(
    id = SleepPageSectionId.SleepConsistency,
    title = "Sleep Consistency Analysis",
    description = "Shows how steady your sleep timing and duration have been recently.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        SleepConsistencyCard(
            variationMinutes = context.dashboardState.sleepConsistencyVariationMinutes,
            durationRangeMinutes = context.dashboardState.sleepDurationRangeMinutes,
            logCount = context.dashboardState.last7DaysSleepLogs.size
        )
    }
}

private object ConnectionsSection : SleepPageSectionDefinition(
    id = SleepPageSectionId.Connections,
    title = "Mood and Sleep Context Links",
    description = "Shows relationships between sleep, mood entries, tags, snoring, and dreams.",
    supportedPages = setOf(SleepPageKey.Overview, SleepPageKey.Insights)
) {
    @Composable
    override fun Content(context: SleepPageSectionRenderContext) {
        SleepSectionHeader(
            title = title,
            subtitle = description,
            editControls = context.editControls
        )

        val sleepMoodInsight = context.dashboardState.sleepMoodInsight
        val sleepTagInsight = context.dashboardState.sleepTagInsight

        if (sleepMoodInsight == null && sleepTagInsight == null) {
            NoSectionDataCard(
                title = "No connections yet",
                description = "Add more sleep context or mood entries to see relationship insights."
            )
        }

        if (sleepMoodInsight != null) {
            SleepMoodInsightCard(
                sleepMoodInsight = sleepMoodInsight
            )
        }

        if (sleepTagInsight != null) {
            SleepTagInsightCard(
                sleepTagInsight = sleepTagInsight
            )
        }
    }
}

@Composable
private fun NoSectionDataCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 112.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
