package com.example.frontpage.theme.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeCustomActionDescriptor
import com.example.frontpage.theme.model.PageThemeCustomPresetDraft
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetKey

private const val THEME_PREVIEW_COLUMNS = 4

@Composable
fun PageThemeDialog(
    target: PageThemeTargetKey,
    catalog: PageThemeCatalog,
    selectedThemePresetId: PageThemePresetId,
    customThemePresets: List<PageThemePreset>,
    onSelectThemePreset: (PageThemePresetId) -> Unit,
    onCreateCustomTheme: (PageThemeCustomPresetDraft) -> Unit,
    onDismiss: () -> Unit
) {
    val baseColorScheme = MaterialTheme.colorScheme
    val targetConfig = catalog.configFor(target)
    val builtInPresetTiles = catalog.descriptorsFor(target).map { descriptor ->
        catalog.presetFor(
            target = target,
            presetId = descriptor.id,
            baseColorScheme = baseColorScheme
        )
    }
    val presetTiles = builtInPresetTiles + customThemePresets
    val customPalettePresets = listOf(
        PageThemePresetId.PurpleRecovery,
        PageThemePresetId.OceanRest,
        PageThemePresetId.ForestCalm,
        PageThemePresetId.RoseDusk
    ).map { presetId ->
        catalog.presetFor(
            target = target,
            presetId = presetId,
            baseColorScheme = baseColorScheme
        )
    }
    var showCustomEditor by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(targetConfig.pickerTitle)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemePreviewGrid(
                    presetTiles = presetTiles,
                    selectedThemePresetId = selectedThemePresetId,
                    customActionDescriptor = catalog.customActionDescriptor,
                    onSelectThemePreset = onSelectThemePreset,
                    onCreateCustomThemeClick = {
                        showCustomEditor = true
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )

    if (showCustomEditor) {
        CustomThemeDialog(
            targetDisplayName = targetConfig.displayName,
            palettePresets = customPalettePresets,
            onDismiss = {
                showCustomEditor = false
            },
            onCreate = { draft ->
                onCreateCustomTheme(draft)
                showCustomEditor = false
            }
        )
    }
}

@Composable
private fun ThemePreviewGrid(
    presetTiles: List<PageThemePreset>,
    selectedThemePresetId: PageThemePresetId,
    customActionDescriptor: PageThemeCustomActionDescriptor,
    onSelectThemePreset: (PageThemePresetId) -> Unit,
    onCreateCustomThemeClick: () -> Unit
) {
    val tiles: List<ThemeGridTile> = presetTiles.map { preset ->
        ThemeGridTile.Preset(preset)
    } + ThemeGridTile.Custom(customActionDescriptor)

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tiles.chunked(THEME_PREVIEW_COLUMNS).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTiles.forEach { tile ->
                    when (tile) {
                        is ThemeGridTile.Preset -> {
                            ThemePresetTile(
                                preset = tile.preset,
                                selected = tile.preset.descriptor.id == selectedThemePresetId,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onSelectThemePreset(tile.preset.descriptor.id)
                                }
                            )
                        }

                        is ThemeGridTile.Custom -> {
                            ThemeCustomTile(
                                descriptor = tile.descriptor,
                                modifier = Modifier.weight(1f),
                                onClick = onCreateCustomThemeClick
                            )
                        }
                    }
                }

                repeat(THEME_PREVIEW_COLUMNS - rowTiles.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ThemePresetTile(
    preset: PageThemePreset,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val themeColors = preset.colors
    val borderColor = if (selected) {
        themeColors.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        modifier = modifier
            .heightIn(min = 128.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                themeColors.primarySoft
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThemeMiniPreview(preset = preset)

            Text(
                text = preset.descriptor.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ThemeCustomTile(
    descriptor: PageThemeCustomActionDescriptor,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .heightIn(min = 128.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = descriptor.displayName,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CustomThemeDialog(
    targetDisplayName: String,
    palettePresets: List<PageThemePreset>,
    onDismiss: () -> Unit,
    onCreate: (PageThemeCustomPresetDraft) -> Unit
) {
    var themeName by remember { mutableStateOf("") }
    var selectedPreset by remember {
        mutableStateOf(palettePresets.first())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Custom $targetDisplayName Theme")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = themeName,
                    onValueChange = { value ->
                        themeName = value
                    },
                    label = {
                        Text("Theme name")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Palette",
                    style = MaterialTheme.typography.titleSmall
                )

                palettePresets.chunked(2).forEach { rowPresets ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPresets.forEach { preset ->
                            ThemePaletteTile(
                                preset = preset,
                                selected = preset.descriptor.id == selectedPreset.descriptor.id,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    selectedPreset = preset
                                }
                            )
                        }

                        repeat(2 - rowPresets.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                ThemeMiniPreview(preset = selectedPreset)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cleanName = themeName.trim().ifBlank {
                        selectedPreset.descriptor.displayName
                    }

                    onCreate(
                        PageThemeCustomPresetDraft(
                            displayName = cleanName,
                            description = "Custom $targetDisplayName theme based on ${selectedPreset.descriptor.displayName}.",
                            colors = selectedPreset.colors,
                            layoutStyle = selectedPreset.layoutStyle
                        )
                    )
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ThemePaletteTile(
    preset: PageThemePreset,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = preset.colors

    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                colors.primarySoft
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) colors.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(22.dp)
                    .weight(0.35f)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colors.headerGradientStart,
                                colors.headerGradientEnd
                            )
                        )
                    )
            )

            Text(
                text = preset.descriptor.displayName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.65f)
            )
        }
    }
}

@Composable
private fun ThemeMiniPreview(
    preset: PageThemePreset
) {
    val colors = preset.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.screenBackground)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            colors.headerGradientStart,
                            colors.headerGradientEnd
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.cardContainer)
                .padding(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .align(Alignment.CenterStart)
                    .background(colors.primary)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(13.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.primarySoft)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(13.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.primary)
            )
        }
    }
}

private sealed interface ThemeGridTile {
    data class Preset(
        val preset: PageThemePreset
    ) : ThemeGridTile

    data class Custom(
        val descriptor: PageThemeCustomActionDescriptor
    ) : ThemeGridTile
}
