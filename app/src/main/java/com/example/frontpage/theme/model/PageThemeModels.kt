package com.example.frontpage.theme.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

data class PageThemeTargetKey(
    val storageValue: String
) {
    companion object {
        val Sleep = PageThemeTargetKey("sleep")
        val Mood = PageThemeTargetKey("mood")
        val Workout = PageThemeTargetKey("workout")
        val Nutrition = PageThemeTargetKey("nutrition")
        val Steps = PageThemeTargetKey("steps")
    }
}

data class PageThemePresetId(
    val storageValue: String
) {
    companion object {
        val Default = PageThemePresetId("default")
        val PurpleRecovery = PageThemePresetId("purple_recovery")
        val OceanRest = PageThemePresetId("ocean_rest")
        val ForestCalm = PageThemePresetId("forest_calm")
        val RoseDusk = PageThemePresetId("rose_dusk")

        fun fromStorageValue(storageValue: String?): PageThemePresetId {
            val cleanValue = storageValue?.trim().orEmpty()
            return if (cleanValue.isBlank()) {
                Default
            } else {
                PageThemePresetId(cleanValue)
            }
        }
    }
}

data class PageThemePreference(
    val target: PageThemeTargetKey,
    val presetId: PageThemePresetId
)

data class PageThemeStoredState(
    val preferences: Map<PageThemeTargetKey, PageThemePreference>,
    val customPresets: Map<PageThemeTargetKey, List<PageThemePreset>>
)

@Immutable
data class PageThemeColors(
    val screenBackground: Color,
    val onBackground: Color,
    val onBackgroundMuted: Color,
    val cardContainer: Color,
    val onCard: Color,
    val onCardMuted: Color,
    val primary: Color,
    val primaryEnd: Color,
    val onPrimary: Color,
    val primarySoft: Color,
    val progressTrack: Color,
    val positive: Color,
    val warning: Color,
    val negative: Color,
    val outline: Color,
    val headerGradientStart: Color,
    val headerGradientEnd: Color,
    val onHeader: Color
)

enum class PageThemeLayoutStyle {
    Standard,
    RecoveryHeader
}

@Immutable
data class PageThemePresetDescriptor(
    val target: PageThemeTargetKey,
    val id: PageThemePresetId,
    val displayName: String,
    val description: String,
    val isCustom: Boolean = false
)

@Immutable
data class PageThemePreset(
    val descriptor: PageThemePresetDescriptor,
    val colors: PageThemeColors,
    val layoutStyle: PageThemeLayoutStyle
)

@Immutable
data class PageThemeCustomActionDescriptor(
    val displayName: String,
    val description: String
)

data class PageThemeCustomPresetDraft(
    val displayName: String,
    val description: String,
    val colors: PageThemeColors,
    val layoutStyle: PageThemeLayoutStyle = PageThemeLayoutStyle.Standard
)

@Immutable
data class PageThemeTargetConfig(
    val target: PageThemeTargetKey,
    val displayName: String,
    val pickerTitle: String,
    val defaultPresetId: PageThemePresetId,
    val supportedPresetIds: List<PageThemePresetId>
)

@Immutable
data class PageThemeNavigationAccent(
    val selectedIndicatorColor: Color,
    val selectedIconColor: Color,
    val selectedTextColor: Color
)
