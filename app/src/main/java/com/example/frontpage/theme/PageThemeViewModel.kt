package com.example.frontpage.theme

import android.app.Application
import androidx.compose.material3.ColorScheme
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.theme.data.PageThemeDataSource
import com.example.frontpage.theme.data.PageThemeRepository
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeCustomPresetDraft
import com.example.frontpage.theme.model.PageThemePreference
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetDescriptor
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PageThemeViewModel(
    application: Application,
    val catalog: PageThemeCatalog = PageThemeCatalog.Default
) : AndroidViewModel(application) {
    private val themeDataSource: PageThemeDataSource
    private var currentUserId: Long? = null
    private var themeStateJob: Job? = null

    private val _preferences = MutableStateFlow(defaultPreferences())
    val preferences: StateFlow<Map<PageThemeTargetKey, PageThemePreference>> =
        _preferences.asStateFlow()

    private val _customPresets = MutableStateFlow<Map<PageThemeTargetKey, List<PageThemePreset>>>(
        emptyMap()
    )
    val customPresets: StateFlow<Map<PageThemeTargetKey, List<PageThemePreset>>> =
        _customPresets.asStateFlow()

    init {
        val appContext = application.applicationContext
        val database = AppDatabase.getDatabase(appContext)

        themeDataSource = PageThemeRepository(
            context = appContext,
            themeDao = database.pageThemeDao(),
            catalog = catalog
        )
    }

    fun refreshForUser(userId: Long?) {
        if (currentUserId == userId && themeStateJob?.isActive == true) return

        currentUserId = userId
        themeStateJob?.cancel()
        themeStateJob = viewModelScope.launch {
            themeDataSource.observeThemeState(userId).collect { state ->
                _preferences.value = state.preferences
                _customPresets.value = state.customPresets
            }
        }
    }

    fun updateThemePreset(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ) {
        val resolvedPresetId = resolveSelectablePresetId(
            target = target,
            presetId = presetId,
            customPresets = _customPresets.value
        )

        _preferences.value = _preferences.value + (
            target to PageThemePreference(
                target = target,
                presetId = resolvedPresetId
            )
        )

        viewModelScope.launch {
            themeDataSource.updateThemePresetId(
                userId = currentUserId,
                target = target,
                presetId = resolvedPresetId
            )
        }
    }

    fun createCustomThemePreset(
        target: PageThemeTargetKey,
        draft: PageThemeCustomPresetDraft
    ) {
        viewModelScope.launch {
            val preset = themeDataSource.createCustomPreset(
                userId = currentUserId,
                target = target,
                draft = draft
            ) ?: return@launch

            _customPresets.value = _customPresets.value + (
                target to (_customPresets.value[target].orEmpty() + preset)
            )
            _preferences.value = _preferences.value + (
                target to PageThemePreference(
                    target = target,
                    presetId = preset.descriptor.id
                )
            )
        }
    }

    fun presetIdFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference> = _preferences.value
    ): PageThemePresetId {
        return preferences[target]?.presetId
            ?: catalog.configFor(target).defaultPresetId
    }

    fun descriptorFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference> = _preferences.value,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>> = _customPresets.value
    ): PageThemePresetDescriptor {
        val presetId = presetIdFor(
            target = target,
            preferences = preferences
        )

        return customPresets[target].orEmpty()
            .firstOrNull { preset -> preset.descriptor.id == presetId }
            ?.descriptor
            ?: catalog.descriptorFor(
                target = target,
                presetId = presetId
            )
    }

    fun presetsFor(
        target: PageThemeTargetKey,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>> = _customPresets.value
    ): List<PageThemePreset> {
        return customPresets[target].orEmpty()
    }

    fun presetFor(
        target: PageThemeTargetKey,
        preferences: Map<PageThemeTargetKey, PageThemePreference>,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>,
        baseColorScheme: ColorScheme
    ): PageThemePreset {
        val presetId = presetIdFor(
            target = target,
            preferences = preferences
        )

        return customPresets[target].orEmpty()
            .firstOrNull { preset -> preset.descriptor.id == presetId }
            ?: catalog.presetFor(
                target = target,
                presetId = presetId,
                baseColorScheme = baseColorScheme
            )
    }

    private fun resolveSelectablePresetId(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
    ): PageThemePresetId {
        if (catalog.supportsBuiltInPreset(target, presetId)) {
            return presetId
        }

        val customPresetExists = customPresets[target].orEmpty().any { preset ->
            preset.descriptor.id == presetId
        }

        return if (customPresetExists) {
            presetId
        } else {
            catalog.configFor(target).defaultPresetId
        }
    }

    private fun defaultPreferences(): Map<PageThemeTargetKey, PageThemePreference> {
        return catalog.targetConfigs.associate { config ->
            config.target to PageThemePreference(
                target = config.target,
                presetId = config.defaultPresetId
            )
        }
    }

    companion object {
        fun factory(
            application: Application,
            catalog: PageThemeCatalog = PageThemeCatalog.Default
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(PageThemeViewModel::class.java)) {
                        return PageThemeViewModel(
                            application = application,
                            catalog = catalog
                        ) as T
                    }

                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}
