package com.example.frontpage.theme.ui

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.frontpage.theme.PageThemeViewModel
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeCustomPresetDraft
import com.example.frontpage.theme.model.PageThemeNavigationAccent
import com.example.frontpage.theme.model.PageThemePreference
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetDescriptor
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetKey
import kotlinx.coroutines.flow.StateFlow

class PageThemeController(
    private val viewModel: PageThemeViewModel
) {
    val catalog: PageThemeCatalog
        get() = viewModel.catalog

    val preferences: StateFlow<Map<PageThemeTargetKey, PageThemePreference>>
        get() = viewModel.preferences

    val customPresets: StateFlow<Map<PageThemeTargetKey, List<PageThemePreset>>>
        get() = viewModel.customPresets

    fun refreshForUser(userId: Long?) {
        viewModel.refreshForUser(userId)
    }

    fun updateThemePreset(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ) {
        viewModel.updateThemePreset(
            target = target,
            presetId = presetId
        )
    }

    fun createCustomThemePreset(
        target: PageThemeTargetKey,
        draft: PageThemeCustomPresetDraft
    ) {
        viewModel.createCustomThemePreset(
            target = target,
            draft = draft
        )
    }

    fun updateCustomThemePreset(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        draft: PageThemeCustomPresetDraft
    ) {
        viewModel.updateCustomThemePreset(
            target = target,
            presetId = presetId,
            draft = draft
        )
    }

    fun deleteCustomThemePreset(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ) {
        viewModel.deleteCustomThemePreset(
            target = target,
            presetId = presetId
        )
    }

    fun presetIdFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference>
    ): PageThemePresetId {
        return viewModel.presetIdFor(
            target = target,
            preferences = preferences
        )
    }

    fun descriptorFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference>,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): PageThemePresetDescriptor {
        return viewModel.descriptorFor(
            target = target,
            preferences = preferences,
            customPresets = customPresets
        )
    }

    fun presetsFor(
        target: PageThemeTargetKey,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): List<PageThemePreset> {
        return viewModel.presetsFor(
            target = target,
            customPresets = customPresets
        )
    }

    fun presetFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference>,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>,
        baseColorScheme: androidx.compose.material3.ColorScheme
    ): PageThemePreset {
        return viewModel.presetFor(
            target = target,
            preferences = preferences,
            customPresets = customPresets,
            baseColorScheme = baseColorScheme
        )
    }

    @Composable
    fun navigationAccentFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference>,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): PageThemeNavigationAccent {
        val preset = presetFor(
            target = target,
            preferences = preferences,
            customPresets = customPresets,
            baseColorScheme = MaterialTheme.colorScheme
        )
        val colors = preset.colors

        return PageThemeNavigationAccent(
            selectedIndicatorColor = colors.primary,
            selectedIconColor = colors.onPrimary,
            selectedTextColor = colors.primary
        )
    }
}

@Composable
fun rememberPageThemeController(
    catalog: PageThemeCatalog = PageThemeCatalog.Default
): PageThemeController {
    val application = LocalContext.current.applicationContext as Application
    val pageThemeViewModel: PageThemeViewModel = viewModel(
        factory = PageThemeViewModel.factory(
            application = application,
            catalog = catalog
        )
    )

    return remember(pageThemeViewModel) {
        PageThemeController(pageThemeViewModel)
    }
}
