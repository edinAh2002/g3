package com.example.frontpage.theme.domain

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.example.frontpage.theme.model.PageThemeColors
import com.example.frontpage.theme.model.PageThemeCustomActionDescriptor
import com.example.frontpage.theme.model.PageThemeLayoutStyle
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetDescriptor
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetConfig
import com.example.frontpage.theme.model.PageThemeTargetKey

class PageThemeCatalog(
    val targetConfigs: List<PageThemeTargetConfig>
) {
    val customActionDescriptor = PageThemeCustomActionDescriptor(
        displayName = "Custom",
        description = "Create your own page theme."
    )

    fun configFor(target: PageThemeTargetKey): PageThemeTargetConfig {
        return targetConfigs.firstOrNull { config ->
            config.target == target
        } ?: fallbackConfigFor(target)
    }

    fun descriptorsFor(target: PageThemeTargetKey): List<PageThemePresetDescriptor> {
        val config = configFor(target)
        return config.supportedPresetIds.map { presetId ->
            descriptorFor(
                target = config.target,
                presetId = presetId
            )
        }
    }

    fun descriptorFor(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetDescriptor {
        val config = configFor(target)
        val normalizedPresetId = normalizePresetId(config.target, presetId)

        return when (normalizedPresetId) {
            PageThemePresetId.Default -> PageThemePresetDescriptor(
                target = config.target,
                id = PageThemePresetId.Default,
                displayName = "Default",
                description = "Use the app's default ${config.displayName} colors."
            )

            PageThemePresetId.PurpleRecovery -> PageThemePresetDescriptor(
                target = config.target,
                id = PageThemePresetId.PurpleRecovery,
                displayName = "Purple Recovery",
                description = "Use deep purple recovery colors for ${config.displayName}."
            )

            PageThemePresetId.OceanRest -> PageThemePresetDescriptor(
                target = config.target,
                id = PageThemePresetId.OceanRest,
                displayName = "Ocean Rest",
                description = "Use muted blue-green colors for ${config.displayName}."
            )

            PageThemePresetId.ForestCalm -> PageThemePresetDescriptor(
                target = config.target,
                id = PageThemePresetId.ForestCalm,
                displayName = "Forest Calm",
                description = "Use clean green recovery colors for ${config.displayName}."
            )

            PageThemePresetId.RoseDusk -> PageThemePresetDescriptor(
                target = config.target,
                id = PageThemePresetId.RoseDusk,
                displayName = "Rose Dusk",
                description = "Use soft rose dusk colors for ${config.displayName}."
            )

            else -> PageThemePresetDescriptor(
                target = config.target,
                id = config.defaultPresetId,
                displayName = "Default",
                description = "Use the app's default ${config.displayName} colors."
            )
        }
    }

    fun normalizePresetId(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): PageThemePresetId {
        val config = configFor(target)
        return config.supportedPresetIds.firstOrNull { supportedPresetId ->
            supportedPresetId == presetId
        } ?: config.defaultPresetId
    }

    fun supportsBuiltInPreset(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId
    ): Boolean {
        return configFor(target).supportedPresetIds.any { supportedPresetId ->
            supportedPresetId == presetId
        }
    }

    fun presetFor(
        target: PageThemeTargetKey,
        presetId: PageThemePresetId,
        baseColorScheme: ColorScheme
    ): PageThemePreset {
        val normalizedPresetId = normalizePresetId(
            target = target,
            presetId = presetId
        )

        return when (normalizedPresetId) {
            PageThemePresetId.Default -> defaultPreset(
                target = target,
                baseColorScheme = baseColorScheme
            )

            PageThemePresetId.PurpleRecovery -> purpleRecoveryPreset(target)
            PageThemePresetId.OceanRest -> oceanRestPreset(target)
            PageThemePresetId.ForestCalm -> forestCalmPreset(target)
            PageThemePresetId.RoseDusk -> roseDuskPreset(target)
            else -> defaultPreset(
                target = target,
                baseColorScheme = baseColorScheme
            )
        }
    }

    fun materialColorSchemeFor(
        baseColorScheme: ColorScheme,
        preset: PageThemePreset
    ): ColorScheme {
        if (preset.descriptor.id == PageThemePresetId.Default) {
            return baseColorScheme
        }

        val colors = preset.colors
        return baseColorScheme.copy(
            primary = colors.primary,
            onPrimary = colors.onPrimary,
            primaryContainer = colors.primarySoft,
            onPrimaryContainer = colors.primary,
            secondary = colors.primary,
            onSecondary = colors.onPrimary,
            tertiary = colors.positive,
            onTertiary = colors.onPrimary,
            background = colors.screenBackground,
            onBackground = colors.onBackground,
            surface = colors.cardContainer,
            onSurface = colors.onCard,
            surfaceVariant = colors.cardContainer,
            onSurfaceVariant = colors.onCardMuted,
            surfaceTint = colors.primary,
            surfaceBright = colors.cardContainer,
            surfaceDim = colors.screenBackground,
            surfaceContainerLowest = colors.screenBackground,
            surfaceContainerLow = colors.cardContainer,
            surfaceContainer = colors.cardContainer,
            surfaceContainerHigh = colors.cardContainer,
            surfaceContainerHighest = colors.cardContainer,
            inverseSurface = colors.onCard,
            inverseOnSurface = colors.cardContainer,
            outline = colors.outline,
            outlineVariant = colors.outline,
            error = colors.negative,
            onError = colors.onPrimary
        )
    }

    private fun defaultPreset(
        target: PageThemeTargetKey,
        baseColorScheme: ColorScheme
    ): PageThemePreset {
        return PageThemePreset(
            descriptor = descriptorFor(
                target = target,
                presetId = PageThemePresetId.Default
            ),
            colors = PageThemeColors(
                screenBackground = baseColorScheme.background,
                onBackground = baseColorScheme.onBackground,
                onBackgroundMuted = baseColorScheme.onSurfaceVariant,
                cardContainer = baseColorScheme.surfaceVariant,
                onCard = baseColorScheme.onSurfaceVariant,
                onCardMuted = baseColorScheme.onSurfaceVariant,
                primary = baseColorScheme.primary,
                primaryEnd = baseColorScheme.primary,
                onPrimary = baseColorScheme.onPrimary,
                primarySoft = baseColorScheme.primaryContainer,
                progressTrack = baseColorScheme.onSurface.copy(alpha = 0.12f),
                positive = Color(0xFF43A047),
                warning = Color(0xFFFDD835),
                negative = Color(0xFFE53935),
                outline = baseColorScheme.outlineVariant,
                headerGradientStart = baseColorScheme.surfaceVariant,
                headerGradientEnd = baseColorScheme.surfaceVariant,
                onHeader = baseColorScheme.onSurfaceVariant
            ),
            layoutStyle = PageThemeLayoutStyle.Standard
        )
    }

    private fun oceanRestPreset(target: PageThemeTargetKey): PageThemePreset {
        return PageThemePreset(
            descriptor = descriptorFor(
                target = target,
                presetId = PageThemePresetId.OceanRest
            ),
            colors = PageThemeColors(
                screenBackground = Color(0xFFEFF5F6),
                onBackground = Color(0xFF1E2B30),
                onBackgroundMuted = Color(0xFF617278),
                cardContainer = Color(0xFFF8FBFB),
                onCard = Color(0xFF1E2B30),
                onCardMuted = Color(0xFF5D6F75),
                primary = Color(0xFF1E7187),
                primaryEnd = Color(0xFF125166),
                onPrimary = Color(0xFFF8FBFB),
                primarySoft = Color(0xFFD6EAEE),
                progressTrack = Color(0xFFD8E1E4),
                positive = Color(0xFF208A5B),
                warning = Color(0xFFC7972B),
                negative = Color(0xFFB9505A),
                outline = Color(0xFFC9D6DA),
                headerGradientStart = Color(0xFF1E7187),
                headerGradientEnd = Color(0xFF125166),
                onHeader = Color(0xFFF8FBFB)
            ),
            layoutStyle = PageThemeLayoutStyle.Standard
        )
    }

    private fun forestCalmPreset(target: PageThemeTargetKey): PageThemePreset {
        return PageThemePreset(
            descriptor = descriptorFor(
                target = target,
                presetId = PageThemePresetId.ForestCalm
            ),
            colors = PageThemeColors(
                screenBackground = Color(0xFFF0F4EF),
                onBackground = Color(0xFF202D20),
                onBackgroundMuted = Color(0xFF647163),
                cardContainer = Color(0xFFFAFCF8),
                onCard = Color(0xFF202D20),
                onCardMuted = Color(0xFF63705F),
                primary = Color(0xFF2F7142),
                primaryEnd = Color(0xFF235532),
                onPrimary = Color(0xFFFAFCF8),
                primarySoft = Color(0xFFDCEADB),
                progressTrack = Color(0xFFD9E1D6),
                positive = Color(0xFF23824D),
                warning = Color(0xFFC29A38),
                negative = Color(0xFFB44F5B),
                outline = Color(0xFFCCD8C9),
                headerGradientStart = Color(0xFF2F7142),
                headerGradientEnd = Color(0xFF235532),
                onHeader = Color(0xFFFAFCF8)
            ),
            layoutStyle = PageThemeLayoutStyle.Standard
        )
    }

    private fun roseDuskPreset(target: PageThemeTargetKey): PageThemePreset {
        return PageThemePreset(
            descriptor = descriptorFor(
                target = target,
                presetId = PageThemePresetId.RoseDusk
            ),
            colors = PageThemeColors(
                screenBackground = Color(0xFFF6F0F3),
                onBackground = Color(0xFF33242B),
                onBackgroundMuted = Color(0xFF745F68),
                cardContainer = Color(0xFFFBF7F9),
                onCard = Color(0xFF33242B),
                onCardMuted = Color(0xFF705D65),
                primary = Color(0xFF9A3E68),
                primaryEnd = Color(0xFF6F284B),
                onPrimary = Color(0xFFFBF7F9),
                primarySoft = Color(0xFFF0DAE5),
                progressTrack = Color(0xFFE4D8DE),
                positive = Color(0xFF288058),
                warning = Color(0xFFC39132),
                negative = Color(0xFFB54857),
                outline = Color(0xFFDCCCD4),
                headerGradientStart = Color(0xFF9A3E68),
                headerGradientEnd = Color(0xFF6F284B),
                onHeader = Color(0xFFFBF7F9)
            ),
            layoutStyle = PageThemeLayoutStyle.Standard
        )
    }

    private fun purpleRecoveryPreset(target: PageThemeTargetKey): PageThemePreset {
        return PageThemePreset(
            descriptor = descriptorFor(
                target = target,
                presetId = PageThemePresetId.PurpleRecovery
            ),
            colors = PageThemeColors(
                screenBackground = Color(0xFFF3EFF8),
                onBackground = Color(0xFF251C31),
                onBackgroundMuted = Color(0xFF695E76),
                cardContainer = Color(0xFFF8F5FA),
                onCard = Color(0xFF251C31),
                onCardMuted = Color(0xFF6F647A),
                primary = Color(0xFF5F2DB8),
                primaryEnd = Color(0xFF492092),
                onPrimary = Color(0xFFF8F5FA),
                primarySoft = Color(0xFFE7DDF3),
                progressTrack = Color(0xFFDDD6E5),
                positive = Color(0xFF1E9F58),
                warning = Color(0xFFD7A542),
                negative = Color(0xFFB94B63),
                outline = Color(0xFFD5CCE0),
                headerGradientStart = Color(0xFF5F2DB8),
                headerGradientEnd = Color(0xFF492092),
                onHeader = Color(0xFFF8F5FA)
            ),
            layoutStyle = PageThemeLayoutStyle.Standard
        )
    }

    private fun fallbackConfigFor(target: PageThemeTargetKey): PageThemeTargetConfig {
        return PageThemeTargetConfig(
            target = target,
            displayName = target.storageValue,
            pickerTitle = "${target.storageValue} Theme",
            defaultPresetId = PageThemePresetId.Default,
            supportedPresetIds = listOf(PageThemePresetId.Default)
        )
    }

    companion object {
        private val SleepTargetConfig = PageThemeTargetConfig(
            target = PageThemeTargetKey.Sleep,
            displayName = "Sleep",
            pickerTitle = "Sleep Theme",
            defaultPresetId = PageThemePresetId.Default,
            supportedPresetIds = listOf(
                PageThemePresetId.Default,
                PageThemePresetId.PurpleRecovery,
                PageThemePresetId.OceanRest,
                PageThemePresetId.ForestCalm,
                PageThemePresetId.RoseDusk
            )
        )

        val Default = PageThemeCatalog(
            targetConfigs = listOf(SleepTargetConfig)
        )
    }
}
