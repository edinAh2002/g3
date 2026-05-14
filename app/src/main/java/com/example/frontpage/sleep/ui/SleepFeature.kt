package com.example.frontpage.sleep.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.sleep.SleepViewModel
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.toSleepLogDraft
import com.example.frontpage.sleep.ui.cards.SleepHomeSummaryCard
import com.example.frontpage.sleep.ui.dialogs.SleepLogDialog
import com.example.frontpage.theme.model.PageThemeTargetKey
import com.example.frontpage.theme.ui.PageThemeController
import com.example.frontpage.theme.ui.components.PageThemeProvider

object SleepFeature {

    @Composable
    fun rememberController(): SleepFeatureController {
        return remember {
            SleepFeatureController()
        }
    }

    @Composable
    fun HomeSummaryCard(
        modifier: Modifier = Modifier
    ) {
        SleepHomeSummaryCard(
            modifier = modifier
        )
    }

    @Composable
    fun MainRoute(
        modifier: Modifier = Modifier,
        controller: SleepFeatureController,
        themeController: PageThemeController,
        viewModel: SleepViewModel = viewModel()
    ) {
        SleepScreen(
            modifier = modifier,
            viewModel = viewModel,
            themeController = themeController,
            onLogSleepClick = {
                controller.openLogDialog()
            },
            onEditSleepEntry = { entry ->
                controller.openEditDialog(entry)
            },
            onReviewDetectedSleep = { candidate ->
                controller.openDetectedSleepReview(candidate)
            }
        )
    }

    @Composable
    fun DialogHost(
        controller: SleepFeatureController,
        themeController: PageThemeController,
        viewModel: SleepViewModel = viewModel()
    ) {
        val goalMinutes by viewModel.goalMinutes.collectAsState()
        val weekdaySettings by viewModel.weekdaySettings.collectAsState()
        val customTags by viewModel.customTags.collectAsState()

        PageThemeProvider(
            controller = themeController,
            target = PageThemeTargetKey.Sleep
        ) {
            if (controller.showLogDialog) {
                LaunchedEffect(Unit) {
                    viewModel.refreshCurrentUser()
                }

                SleepLogDialog(
                    existingEntry = controller.editingEntry,
                    initialDraft = controller.reviewingDetectionCandidate?.toSleepLogDraft(),
                    goalMinutes = goalMinutes,
                    weekdaySettings = weekdaySettings,
                    customTags = customTags,
                    onDismiss = {
                        controller.closeLogDialog()
                    },
                    onSave = { draft ->
                        val editingEntry = controller.editingEntry
                        val reviewingCandidate = controller.reviewingDetectionCandidate
                        val date = SleepDateUtils.formatHistoryDate(draft.wakeDateMillis)

                        if (editingEntry == null) {
                            viewModel.addSleep(
                                draft.toNewEntry(
                                    id = System.currentTimeMillis(),
                                    date = date
                                )
                            )

                            if (reviewingCandidate != null) {
                                viewModel.acceptDetectionCandidate(reviewingCandidate.id)
                            }
                        } else {
                            viewModel.updateSleep(
                                draft.applyTo(
                                    entry = editingEntry,
                                    date = date
                                )
                            )
                        }

                        controller.closeLogDialog()
                    }
                )
            }
        }
    }
}
