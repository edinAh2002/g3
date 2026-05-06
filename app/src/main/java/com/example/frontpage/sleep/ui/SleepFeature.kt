package com.example.frontpage.sleep.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.sleep.SleepViewModel
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.domain.SleepDateUtils
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.cards.SleepHomeSummaryCard
import com.example.frontpage.sleep.ui.dialogs.SleepLogDialog

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
        controller: SleepFeatureController
    ) {
        SleepScreen(
            modifier = modifier,
            onLogSleepClick = {
                controller.openLogDialog()
            },
            onEditSleepEntry = { entry ->
                controller.openEditDialog(entry)
            }
        )
    }

    @Composable
    fun DialogHost(
        controller: SleepFeatureController,
        viewModel: SleepViewModel = viewModel()
    ) {
        if (controller.showLogDialog) {
            SleepLogDialog(
                existingEntry = controller.editingEntry,
                goalMinutes = SleepSettingsRepository.sleepGoalMinutes,
                onDismiss = {
                    controller.closeLogDialog()
                },
                onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, quality, durationMinutes, notes ->

                    val editingEntry = controller.editingEntry

                    if (editingEntry == null) {
                        val now = System.currentTimeMillis()

                        viewModel.addSleep(
                            SleepEntry(
                                id = now,
                                date = SleepDateUtils.formatHistoryDate(now),
                                sleepHour = sleepHour,
                                sleepMinute = sleepMinute,
                                wakeHour = wakeHour,
                                wakeMinute = wakeMinute,
                                durationMinutes = durationMinutes,
                                quality = quality,
                                notes = notes,
                                dateMillis = now
                            )
                        )
                    } else {
                        viewModel.updateSleep(
                            editingEntry.copy(
                                sleepHour = sleepHour,
                                sleepMinute = sleepMinute,
                                wakeHour = wakeHour,
                                wakeMinute = wakeMinute,
                                durationMinutes = durationMinutes,
                                quality = quality,
                                notes = notes
                            )
                        )
                    }

                    controller.closeLogDialog()
                }
            )
        }
    }
}