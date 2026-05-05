package com.example.frontpage.mood.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object MoodFeature {

    @Composable
    fun HomeSummaryCard(
        modifier: Modifier = Modifier
    ) {
        MoodHomeSummaryCard(
            modifier = modifier
        )
    }

    @Composable
    fun LogRoute(
        modifier: Modifier = Modifier,
        onLogNewMood: () -> Unit
    ) {
        MoodMainRoute(
            modifier = modifier,
            onLogNewMood = onLogNewMood
        )
    }

    @Composable
    fun TrackingRoute(
        modifier: Modifier = Modifier,
        onSaved: () -> Unit,
        onBack: () -> Unit
    ) {
        MoodTrackingRoute(
            modifier = modifier,
            onSaved = onSaved,
            onBack = onBack
        )
    }
}