package com.example.frontpage.sleep.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.frontpage.sleep.model.SleepThemePresetId

@Immutable
data class SleepThemeColors(
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

enum class SleepThemeLayoutStyle {
    Standard,
    RecoveryHeader
}

@Immutable
data class SleepThemePresetDescriptor(
    val id: SleepThemePresetId,
    val displayName: String,
    val description: String
)

@Immutable
data class SleepThemePreset(
    val descriptor: SleepThemePresetDescriptor,
    val colors: SleepThemeColors,
    val layoutStyle: SleepThemeLayoutStyle
)

@Immutable
data class SleepThemeCustomActionDescriptor(
    val displayName: String,
    val description: String
)

object SleepThemePresetCatalog {
    val descriptors = listOf(
        SleepThemePresetDescriptor(
            id = SleepThemePresetId.Default,
            displayName = "Default",
            description = "Use the app's Default Sleep colors."
        ),
        SleepThemePresetDescriptor(
            id = SleepThemePresetId.PurpleRecovery,
            displayName = "Purple Recovery",
            description = "A bright recovery-style Sleep page with purple accents."
        )
    )

    val customActionDescriptor = SleepThemeCustomActionDescriptor(
        displayName = "Custom",
        description = "Create your own Sleep theme."
    )

    fun descriptorFor(presetId: SleepThemePresetId): SleepThemePresetDescriptor {
        return descriptors.firstOrNull { descriptor ->
            descriptor.id == presetId
        } ?: descriptors.first()
    }

    fun materialColorSchemeFor(
        baseColorScheme: ColorScheme,
        preset: SleepThemePreset
    ): ColorScheme {
        if (preset.descriptor.id == SleepThemePresetId.Default) {
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

    fun presetFor(
        presetId: SleepThemePresetId,
        baseColorScheme: ColorScheme
    ): SleepThemePreset {
        return when (presetId) {
            SleepThemePresetId.Default -> defaultPreset(baseColorScheme)
            SleepThemePresetId.PurpleRecovery -> purpleRecoveryPreset()
        }
    }

    private fun defaultPreset(baseColorScheme: ColorScheme): SleepThemePreset {
        return SleepThemePreset(
            descriptor = descriptorFor(SleepThemePresetId.Default),
            colors = SleepThemeColors(
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
            layoutStyle = SleepThemeLayoutStyle.Standard
        )
    }

    private fun purpleRecoveryPreset(): SleepThemePreset {
        return SleepThemePreset(
            descriptor = descriptorFor(SleepThemePresetId.PurpleRecovery),
            colors = SleepThemeColors(
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
            layoutStyle = SleepThemeLayoutStyle.Standard
        )
    }
}

private val fallbackSleepThemePreset = SleepThemePresetCatalog.presetFor(
    presetId = SleepThemePresetId.Default,
    baseColorScheme = androidx.compose.material3.lightColorScheme()
)

private val LocalSleepThemePreset = staticCompositionLocalOf {
    fallbackSleepThemePreset
}

object SleepTheme {
    val preset: SleepThemePreset
        @Composable
        get() = LocalSleepThemePreset.current

    val colors: SleepThemeColors
        @Composable
        get() = LocalSleepThemePreset.current.colors
}

@Composable
fun SleepThemeProvider(
    presetId: SleepThemePresetId,
    content: @Composable () -> Unit
) {
    val baseColorScheme = MaterialTheme.colorScheme
    val baseTypography = MaterialTheme.typography
    val baseShapes = MaterialTheme.shapes
    val preset = SleepThemePresetCatalog.presetFor(
        presetId = presetId,
        baseColorScheme = baseColorScheme
    )
    val colorScheme = SleepThemePresetCatalog.materialColorSchemeFor(
        baseColorScheme = baseColorScheme,
        preset = preset
    )

    CompositionLocalProvider(LocalSleepThemePreset provides preset) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = baseTypography,
            shapes = baseShapes,
            content = content
        )
    }
}

@Composable
fun sleepCardColors(): CardColors {
    val colors = SleepTheme.colors
    return CardDefaults.cardColors(
        containerColor = colors.cardContainer,
        contentColor = colors.onCard
    )
}

@Composable
fun sleepPrimaryButtonColors(): ButtonColors {
    val colors = SleepTheme.colors
    return ButtonDefaults.buttonColors(
        containerColor = colors.primary,
        contentColor = colors.onPrimary
    )
}

@Composable
fun sleepOutlinedButtonColors(): ButtonColors {
    return ButtonDefaults.outlinedButtonColors(
        contentColor = SleepTheme.colors.primary
    )
}
