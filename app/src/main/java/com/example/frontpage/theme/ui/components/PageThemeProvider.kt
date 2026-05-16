package com.example.frontpage.theme.ui.components

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeColors
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetKey
import com.example.frontpage.theme.ui.PageThemeController

private val fallbackPageThemePreset = PageThemeCatalog.Default.presetFor(
    target = PageThemeTargetKey.Sleep,
    presetId = PageThemePresetId.Default,
    baseColorScheme = lightColorScheme()
)

private val LocalPageThemePreset = staticCompositionLocalOf {
    fallbackPageThemePreset
}

object PageTheme {
    val preset: PageThemePreset
        @Composable
        get() = LocalPageThemePreset.current

    val colors: PageThemeColors
        @Composable
        get() = LocalPageThemePreset.current.colors
}

@Composable
fun PageThemeProvider(
    controller: PageThemeController,
    target: PageThemeTargetKey,
    content: @Composable () -> Unit
) {
    val preferences by controller.preferences.collectAsState()
    val customPresets by controller.customPresets.collectAsState()
    val baseColorScheme = MaterialTheme.colorScheme
    val baseTypography = MaterialTheme.typography
    val baseShapes = MaterialTheme.shapes
    val preset = controller.presetFor(
        target = target,
        preferences = preferences,
        customPresets = customPresets,
        baseColorScheme = baseColorScheme
    )
    val colorScheme = controller.catalog.materialColorSchemeFor(
        baseColorScheme = baseColorScheme,
        preset = preset
    )

    CompositionLocalProvider(LocalPageThemePreset provides preset) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = baseTypography,
            shapes = baseShapes,
            content = content
        )
    }
}

@Composable
fun pageThemeCardColors(): CardColors {
    val colors = PageTheme.colors
    return CardDefaults.cardColors(
        containerColor = colors.cardContainer,
        contentColor = colors.onCard
    )
}

@Composable
fun pageThemePrimaryButtonColors(): ButtonColors {
    val colors = PageTheme.colors
    return ButtonDefaults.buttonColors(
        containerColor = colors.primary,
        contentColor = colors.onPrimary
    )
}

@Composable
fun pageThemeOutlinedButtonColors(): ButtonColors {
    return ButtonDefaults.outlinedButtonColors(
        contentColor = PageTheme.colors.primary
    )
}
