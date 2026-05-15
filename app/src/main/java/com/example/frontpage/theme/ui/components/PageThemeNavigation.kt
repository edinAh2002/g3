package com.example.frontpage.theme.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.frontpage.theme.model.PageThemeTargetKey
import com.example.frontpage.theme.ui.PageThemeController

@Composable
fun pageThemeNavigationBarItemColors(
    controller: PageThemeController,
    target: PageThemeTargetKey
): NavigationBarItemColors {
    val preferences by controller.preferences.collectAsState()
    val customPresets by controller.customPresets.collectAsState()
    val accent = controller.navigationAccentFor(
        target = target,
        preferences = preferences,
        customPresets = customPresets
    )

    return NavigationBarItemDefaults.colors(
        selectedIconColor = accent.selectedIconColor,
        selectedTextColor = accent.selectedTextColor,
        indicatorColor = accent.selectedIndicatorColor,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
