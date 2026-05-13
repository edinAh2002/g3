package com.example.frontpage.mood.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.frontpage.mood.domain.MoodLabelUtils
import com.example.frontpage.mood.domain.MoodMomentumSummary
import com.example.frontpage.mood.domain.MoodScoreSummary
import com.example.frontpage.mood.domain.MoodStreakSummary
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.model.WeeklyMoodChartItem
import com.example.frontpage.mood.ui.components.LatestMoodCard
import com.example.frontpage.mood.ui.components.MoodMetricTile
import com.example.frontpage.mood.ui.components.MoodMomentumCard
import com.example.frontpage.mood.ui.components.MoodScoreCard
import com.example.frontpage.mood.ui.components.MoodSectionHeader
import com.example.frontpage.mood.ui.components.WeeklyMoodChart

@Composable
fun MoodOverviewPage(
    latestMood: MoodEntry?,
    averageMood: Double?,
    todayAverageMood: Double?,
    bestMood: MoodEntry?,
    lowestMood: MoodEntry?,
    totalLogs: Int,
    weeklyChartData: List<WeeklyMoodChartItem>,
    moodScoreSummary: MoodScoreSummary?,
    moodMomentumSummary: MoodMomentumSummary,
    streakSummary: MoodStreakSummary,
    scalePreset: MoodScalePreset,
    onLogMoodClick: () -> Unit,
    onViewScaleClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LatestMoodCard(
            latestMood = latestMood,
            scalePreset = scalePreset,
            onLogMoodClick = onLogMoodClick,
            onViewScaleClick = onViewScaleClick
        )

        MoodSectionHeader(
            title = "This Week",
            subtitle = "Score and momentum use your recent mood logs."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            MoodScoreCard(
                scoreSummary = moodScoreSummary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            MoodMomentumCard(
                moodMomentumSummary = moodMomentumSummary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        MoodSectionHeader(
            title = "Key Numbers",
            subtitle = "A quick scan of your mood log."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Average",
                value = MoodLabelUtils.formatMoodAverage(averageMood),
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Logs",
                value = totalLogs.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Logged Streak",
                value = "${streakSummary.loggedDayStreak}d",
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Positive Streak",
                value = "${streakSummary.positiveMoodStreak}d",
                modifier = Modifier.weight(1f)
            )
        }

        MoodSectionHeader(
            title = "Last 7 Days",
            subtitle = "Each bar shows average mood by day."
        )

        WeeklyMoodChart(
            chartData = weeklyChartData
        )

        MoodSectionHeader(
            title = "Records",
            subtitle = "Your highest and lowest saved mood logs."
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Today Avg",
                value = MoodLabelUtils.formatMoodAverage(todayAverageMood),
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Best",
                value = bestMood?.let { entry ->
                    MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Lowest",
                value = lowestMood?.let { entry ->
                    MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Latest",
                value = latestMood?.let { entry ->
                    MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
