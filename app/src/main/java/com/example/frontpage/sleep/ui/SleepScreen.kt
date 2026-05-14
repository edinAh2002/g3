package com.example.frontpage.sleep.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.health.connect.client.PermissionController
import com.example.frontpage.mood.MoodViewModel
import com.example.frontpage.sleep.SleepViewModel
import com.example.frontpage.sleep.domain.SleepDashboardStateBuilder
import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepDetectionSettings
import com.example.frontpage.sleep.model.SleepPageLayoutDefaults
import com.example.frontpage.sleep.model.SleepEntry
import com.example.frontpage.sleep.ui.components.SleepPageCustomizationPanel
import com.example.frontpage.sleep.ui.components.SleepPageNavigation
import com.example.frontpage.sleep.ui.components.SleepPageSectionRegistry
import com.example.frontpage.sleep.ui.components.SleepTrackerHeader
import com.example.frontpage.sleep.ui.dialogs.SleepGoalDialog
import com.example.frontpage.sleep.ui.pages.SleepHistoryPage
import com.example.frontpage.sleep.ui.pages.SleepInsightsPage
import com.example.frontpage.sleep.ui.pages.SleepOverviewPage
import com.example.frontpage.sleep.ui.pages.SleepSettingsPage
import com.example.frontpage.sleep.ui.theme.SleepTheme
import com.example.frontpage.theme.model.PageThemeTargetKey
import com.example.frontpage.theme.ui.PageThemeController
import com.example.frontpage.theme.ui.components.PageThemeProvider

@Composable
fun SleepScreen(
    modifier: Modifier = Modifier,
    onLogSleepClick: () -> Unit,
    onEditSleepEntry: (SleepEntry) -> Unit,
    onReviewDetectedSleep: (SleepDetectionCandidate) -> Unit,
    themeController: PageThemeController,
    viewModel: SleepViewModel = viewModel(),
    moodViewModel: MoodViewModel = viewModel()
) {
    var selectedPage by remember { mutableStateOf(SleepPage.Overview) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var isEditingPage by remember { mutableStateOf(false) }

    val sleepLogs by viewModel.sleepLogs.collectAsState()
    val goalMinutes by viewModel.goalMinutes.collectAsState()
    val weekdaySettings by viewModel.weekdaySettings.collectAsState()
    val customTags by viewModel.customTags.collectAsState()
    val sleepDetectionSettings by viewModel.sleepDetectionSettings.collectAsState()
    val pendingDetectionCandidate by viewModel.pendingDetectionCandidate.collectAsState()
    val pageLayouts by viewModel.pageLayouts.collectAsState()
    val themePreferences by themeController.preferences.collectAsState()
    val customThemePresets by themeController.customPresets.collectAsState()
    val themePresetId = themeController.presetIdFor(
        target = PageThemeTargetKey.Sleep,
        preferences = themePreferences
    )
    val healthConnectState by viewModel.healthConnectState.collectAsState()
    val moodEntries by moodViewModel.allMoodEntries.collectAsState()
    val dashboardStateBuilder = remember { SleepDashboardStateBuilder() }
    val context = LocalContext.current
    var pendingSleepDetectionSettings by remember {
        mutableStateOf<SleepDetectionSettings?>(null)
    }

    val healthConnectPermissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        viewModel.onHealthConnectPermissionsChanged(grantedPermissions)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val settings = pendingSleepDetectionSettings
        pendingSleepDetectionSettings = null

        if (settings != null) {
            viewModel.updateSleepDetectionSettings(
                if (granted) settings else settings.copy(enabled = false)
            )
        }
    }

    fun updateSleepDetectionSettings(settings: SleepDetectionSettings) {
        val needsNotificationPermission =
            settings.enabled &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

        if (needsNotificationPermission) {
            pendingSleepDetectionSettings = settings
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.updateSleepDetectionSettings(settings)
        }
    }

    LaunchedEffect(moodViewModel) {
        moodViewModel.refreshCurrentUser()
    }

    LaunchedEffect(viewModel) {
        viewModel.refreshCurrentUser()
        viewModel.refreshHealthConnectState()
        viewModel.removeFakeMoodSleepContextLinks {
            moodViewModel.loadMoods()
        }
    }

    val dashboardState = dashboardStateBuilder.build(
        sleepLogs = sleepLogs,
        fallbackGoalMinutes = goalMinutes,
        moodEntries = moodEntries,
        goalMinutesForDate = viewModel::getGoalMinutesForDate
    )

    val selectedPageKey = selectedPage.pageKey
    val selectedPageLayout = pageLayouts[selectedPageKey]
        ?: SleepPageLayoutDefaults.defaultLayout(selectedPageKey)
    val canCustomizeSelectedPage = SleepPageSectionRegistry.sectionsFor(selectedPageKey).isNotEmpty()

    LaunchedEffect(selectedPageKey, canCustomizeSelectedPage) {
        if (!canCustomizeSelectedPage) {
            isEditingPage = false
        }
    }

    PageThemeProvider(
        controller = themeController,
        target = PageThemeTargetKey.Sleep
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = SleepTheme.colors.screenBackground,
            contentColor = SleepTheme.colors.onBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SleepTrackerHeader(
                    canCustomizeSelectedPage = canCustomizeSelectedPage,
                    isEditingPage = isEditingPage,
                    onToggleEdit = {
                        isEditingPage = !isEditingPage
                    }
                )

                SleepPageNavigation(
                    selectedPage = selectedPage,
                    onPageSelected = { selectedPage = it }
                )

                if (isEditingPage && canCustomizeSelectedPage) {
                    SleepPageCustomizationPanel(
                        pageKey = selectedPageKey,
                        layout = selectedPageLayout,
                        onAddSection = { sectionId ->
                            viewModel.addSleepPageSection(
                                pageKey = selectedPageKey,
                                sectionId = sectionId
                            )
                        },
                        onResetLayout = {
                            viewModel.resetSleepPageLayout(selectedPageKey)
                        }
                    )
                }

                when (selectedPage) {
                    SleepPage.Overview -> {
                        SleepOverviewPage(
                            dashboardState = dashboardState,
                            sleepLogs = sleepLogs,
                            pageLayout = selectedPageLayout,
                            isEditing = isEditingPage,
                            onRemoveSection = { sectionId ->
                                viewModel.removeSleepPageSection(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onMoveSectionUp = { sectionId ->
                                viewModel.moveSleepPageSectionUp(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onMoveSectionDown = { sectionId ->
                                viewModel.moveSleepPageSectionDown(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onLogSleepClick = onLogSleepClick,
                            onEditGoalClick = {
                                showGoalDialog = true
                            },
                            pendingDetectionCandidate = pendingDetectionCandidate,
                            onReviewDetectedSleep = onReviewDetectedSleep,
                            onDismissDetectedSleep = { candidate ->
                                viewModel.dismissDetectionCandidate(candidate.id)
                            }
                        )
                    }

                    SleepPage.History -> {
                        SleepHistoryPage(
                            sleepLogs = sleepLogs,
                            goalMinutesForDate = viewModel::getGoalMinutesForDate,
                            onEditEntry = { entry ->
                                onEditSleepEntry(entry)
                            },
                            onDeleteEntry = { entry ->
                                viewModel.deleteSleep(entry.id)
                            }
                        )
                    }

                    SleepPage.Insights -> {
                        SleepInsightsPage(
                            dashboardState = dashboardState,
                            sleepLogs = sleepLogs,
                            pageLayout = selectedPageLayout,
                            isEditing = isEditingPage,
                            onRemoveSection = { sectionId ->
                                viewModel.removeSleepPageSection(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onMoveSectionUp = { sectionId ->
                                viewModel.moveSleepPageSectionUp(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onMoveSectionDown = { sectionId ->
                                viewModel.moveSleepPageSectionDown(
                                    pageKey = selectedPageKey,
                                    sectionId = sectionId
                                )
                            },
                            onLogSleepClick = onLogSleepClick,
                            onEditGoalClick = {
                                showGoalDialog = true
                            }
                        )
                    }

                    SleepPage.Settings -> {
                        SleepSettingsPage(
                            goalMinutes = goalMinutes,
                            weekdaySettings = weekdaySettings,
                            customTags = customTags,
                            totalLogs = sleepLogs.size,
                            healthConnectState = healthConnectState,
                            sleepDetectionSettings = sleepDetectionSettings,
                            selectedThemePresetId = themePresetId,
                            customThemePresets = themeController.presetsFor(
                                target = PageThemeTargetKey.Sleep,
                                customPresets = customThemePresets
                            ),
                            onUpdateAllWeekdayGoals = { newGoalMinutes ->
                                viewModel.updateSleepGoalMinutes(newGoalMinutes)
                            },
                            onClearSleepHistoryClick = {
                                viewModel.clearAllLogs()
                            },
                            onUpdateWeekdayGoal = { weekday, newGoalMinutes ->
                                viewModel.updateWeekdayGoalMinutes(
                                    weekday = weekday,
                                    newGoalMinutes = newGoalMinutes
                                )
                            },
                            onUpdateWeekdayScheduleTargets = { weekday, bedtimeMinutes, wakeMinutes ->
                                viewModel.updateWeekdayScheduleTargets(
                                    weekday = weekday,
                                    bedtimeMinutes = bedtimeMinutes,
                                    wakeMinutes = wakeMinutes
                                )
                            },
                            onUpdateAllWeekdayScheduleTargets = { bedtimeMinutes, wakeMinutes ->
                                viewModel.updateAllWeekdayScheduleTargets(
                                    bedtimeMinutes = bedtimeMinutes,
                                    wakeMinutes = wakeMinutes
                                )
                            },
                            onAddCustomTag = { label ->
                                viewModel.addCustomTag(label)
                            },
                            onDeleteCustomTag = { tagId ->
                                viewModel.deleteCustomTag(tagId)
                            },
                            onUpdateThemePreset = { presetId ->
                                themeController.updateThemePreset(
                                    target = PageThemeTargetKey.Sleep,
                                    presetId = presetId
                                )
                            },
                            onCreateCustomTheme = { draft ->
                                themeController.createCustomThemePreset(
                                    target = PageThemeTargetKey.Sleep,
                                    draft = draft
                                )
                            },
                            onUpdateCustomTheme = { presetId, draft ->
                                themeController.updateCustomThemePreset(
                                    target = PageThemeTargetKey.Sleep,
                                    presetId = presetId,
                                    draft = draft
                                )
                            },
                            onDeleteCustomTheme = { presetId ->
                                themeController.deleteCustomThemePreset(
                                    target = PageThemeTargetKey.Sleep,
                                    presetId = presetId
                                )
                            },
                            onUpdateSleepDetectionSettings = { settings ->
                                updateSleepDetectionSettings(settings)
                            },
                            onRequestHealthConnectAccessClick = {
                                viewModel.onHealthConnectPermissionRequestStarted()

                                try {
                                    healthConnectPermissionLauncher.launch(viewModel.sleepHealthPermissions)
                                } catch (_: Exception) {
                                    viewModel.onHealthConnectPermissionRequestFailed()
                                }
                            },
                            onImportHealthConnectSleepClick = {
                                viewModel.importHealthConnectSleep()
                            }
                        )
                    }
                }
            }
        }

        if (showGoalDialog) {
            SleepGoalDialog(
                currentGoalMinutes = goalMinutes,
                onDismiss = {
                    showGoalDialog = false
                },
                onSave = { newGoalMinutes ->
                    viewModel.updateTodaySleepGoalMinutes(newGoalMinutes)
                    showGoalDialog = false
                }
            )
        }
    }
}
