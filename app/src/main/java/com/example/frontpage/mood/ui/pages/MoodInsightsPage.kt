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
import com.example.frontpage.mood.domain.MoodNoteInsight
import com.example.frontpage.mood.domain.MoodPatternInsight
import com.example.frontpage.mood.domain.MoodScoreSummary
import com.example.frontpage.mood.domain.MoodStreakSummary
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.mood.model.MoodScalePreset
import com.example.frontpage.mood.ui.components.MoodMetricTile
import com.example.frontpage.mood.ui.components.MoodMomentumCard
import com.example.frontpage.mood.ui.components.MoodNoteInsightCard
import com.example.frontpage.mood.ui.components.MoodPatternInsightCard
import com.example.frontpage.mood.ui.components.MoodRecommendationCard
import com.example.frontpage.mood.ui.components.MoodScoreCard
import com.example.frontpage.mood.ui.components.MoodSectionHeader
import com.example.frontpage.mood.ui.components.MoodStreakCard

@Composable
fun MoodInsightsPage(
    moodEntries: List<MoodEntry>,
    averageMood: Double?,
    todayAverageMood: Double?,
    bestMood: MoodEntry?,
    lowestMood: MoodEntry?,
    moodScoreSummary: MoodScoreSummary?,
    moodMomentumSummary: MoodMomentumSummary,
    streakSummary: MoodStreakSummary,
    primaryRecommendation: String,
    moodPatternInsight: MoodPatternInsight?,
    moodNoteInsight: MoodNoteInsight?,
    scalePreset: MoodScalePreset
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MoodSectionHeader(
            title = "Mood Insights",
            subtitle = "The useful patterns first, then the details."
        )

        MoodRecommendationCard(
            recommendation = primaryRecommendation
        )

        MoodSectionHeader(
            title = "Performance",
            subtitle = "How your recent mood compares with previous logs."
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

        MoodStreakCard(
            streakSummary = streakSummary
        )

        MoodSectionHeader(
            title = "Snapshot",
            subtitle = "Basic numbers from all saved mood logs."
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
                value = moodEntries.size.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodMetricTile(
                title = "Best",
                value = bestMood?.let { entry ->
                    MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )

            MoodMetricTile(
                title = "Lowest",
                value = lowestMood?.let { entry ->
                    MoodLabelUtils.getMoodLabel(entry.moodValue, scalePreset)
                } ?: "No data",
                modifier = Modifier.weight(1f)
            )
        }

        MoodSectionHeader(
            title = "Patterns",
            subtitle = "Daily averages, notes, and repeated mood signals."
        )

        MoodMetricTile(
            title = "Today's Average",
            value = MoodLabelUtils.formatMoodAverage(todayAverageMood),
            modifier = Modifier.fillMaxWidth()
        )

        if (moodPatternInsight != null || moodNoteInsight != null) {
            MoodSectionHeader(
                title = "Connections",
                subtitle = "Notes and repeated feelings that add context."
            )

            if (moodPatternInsight != null) {
                MoodPatternInsightCard(
                    moodPatternInsight = moodPatternInsight
                )
            }

            if (moodNoteInsight != null) {
                MoodNoteInsightCard(
                    moodNoteInsight = moodNoteInsight
                )
            }
        }
    }
}
