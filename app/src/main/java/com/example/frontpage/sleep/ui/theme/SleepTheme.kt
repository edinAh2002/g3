package com.example.frontpage.sleep.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import com.example.frontpage.theme.model.PageThemeColors
import com.example.frontpage.theme.model.PageThemeLayoutStyle
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetDescriptor
import com.example.frontpage.theme.ui.components.PageTheme

typealias SleepThemeColors = PageThemeColors
typealias SleepThemeLayoutStyle = PageThemeLayoutStyle
typealias SleepThemePreset = PageThemePreset
typealias SleepThemePresetDescriptor = PageThemePresetDescriptor

object SleepTheme {
    val preset: SleepThemePreset
        @Composable
        get() = PageTheme.preset

    val colors: SleepThemeColors
        @Composable
        get() = PageTheme.colors
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
