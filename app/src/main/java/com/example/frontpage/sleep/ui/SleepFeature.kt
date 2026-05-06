package com.example.frontpage.sleep.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.sleep.SleepViewModel
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
        val goalMinutes by viewModel.goalMinutes.collectAsState()

        if (controller.showLogDialog) {
            SleepLogDialog(
                existingEntry = controller.editingEntry,
                goalMinutes = goalMinutes,
                onDismiss = {
                    controller.closeLogDialog()
                },
                onSave = { sleepHour, sleepMinute, wakeHour, wakeMinute, wakeDateMillis, quality, durationMinutes, notes ->

                    val editingEntry = controller.editingEntry

                    if (editingEntry == null) {
                        viewModel.addSleep(
                            SleepEntry(
                                id = System.currentTimeMillis(),
                                date = SleepDateUtils.formatHistoryDate(wakeDateMillis),
                                sleepHour = sleepHour,
                                sleepMinute = sleepMinute,
                                wakeHour = wakeHour,
                                wakeMinute = wakeMinute,
                                durationMinutes = durationMinutes,
                                quality = quality,
                                notes = notes,
                                dateMillis = wakeDateMillis
                            )
                        )
                    } else {
                        viewModel.updateSleep(
                            editingEntry.copy(
                                date = SleepDateUtils.formatHistoryDate(wakeDateMillis),
                                sleepHour = sleepHour,
                                sleepMinute = sleepMinute,
                                wakeHour = wakeHour,
                                wakeMinute = wakeMinute,
                                durationMinutes = durationMinutes,
                                quality = quality,
                                notes = notes,
                                dateMillis = wakeDateMillis
                            )
                        )
                    }

                    controller.closeLogDialog()
                }
            )
        }
    }
}
