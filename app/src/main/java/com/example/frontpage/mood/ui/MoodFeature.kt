package com.example.frontpage.mood.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

object MoodFeature {

    @Composable
    fun rememberController(): MoodFeatureController {
        return remember {
            MoodFeatureController()
        }
    }

    @Composable
    fun HomeSummaryCard(
        modifier: Modifier = Modifier
    ) {
        MoodHomeSummaryCard(
            modifier = modifier
        )
    }

    @Composable
    fun MainRoute(
        modifier: Modifier = Modifier,
        controller: MoodFeatureController
    ) {
        MoodMainRoute(
            modifier = modifier,
            onLogNewMood = {
                controller.openTrackingDialog()
            }
        )
    }

    @Composable
    fun DialogHost(
        controller: MoodFeatureController
    ) {
        if (controller.showTrackingDialog) {
            MoodTrackingDialogRoute(
                onSaved = {
                    controller.closeTrackingDialog()
                },
                onDismiss = {
                    controller.closeTrackingDialog()
                }
            )
        }
    }
}