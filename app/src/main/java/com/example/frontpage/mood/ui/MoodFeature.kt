package com.example.frontpage.mood.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.mood.ui.cards.MoodHomeSummaryCard
import com.example.frontpage.mood.ui.dialogs.MoodLogDialog

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
        MoodScreen(
            modifier = modifier,
            onLogMoodClick = {
                controller.openLogDialog()
            },
            onEditMoodEntry = { entry ->
                controller.openEditDialog(entry)
            }
        )
    }

    @Composable
    fun DialogHost(
        controller: MoodFeatureController,
        viewModel: MoodViewModel = viewModel()
    ) {
        val defaultScalePreset by viewModel.defaultScalePreset.collectAsState()

        if (controller.showLogDialog) {
            LaunchedEffect(Unit) {
                viewModel.refreshCurrentUser()
            }

            MoodLogDialog(
                existingEntry = controller.editingEntry,
                defaultScalePreset = defaultScalePreset,
                onDismiss = {
                    controller.closeLogDialog()
                },
                onSave = { draft ->
                    val editingEntry = controller.editingEntry

                    if (editingEntry == null) {
                        viewModel.addMood(
                            draft.toNewEntry()
                        )
                    } else {
                        viewModel.updateMood(
                            draft.applyTo(editingEntry)
                        )
                    }

                    controller.closeLogDialog()
                }
            )
        }
    }
}
