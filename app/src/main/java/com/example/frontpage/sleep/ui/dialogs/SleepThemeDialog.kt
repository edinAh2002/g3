package com.example.frontpage.sleep.ui.dialogs

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
import com.example.frontpage.sleep.model.SleepThemePresetId
import com.example.frontpage.sleep.ui.theme.SleepThemeCustomActionDescriptor
import com.example.frontpage.sleep.ui.theme.SleepThemePreset
import com.example.frontpage.sleep.ui.theme.SleepThemePresetCatalog

private const val THEME_PREVIEW_COLUMNS = 4

@Composable
internal fun SleepThemeDialog(
    selectedThemePresetId: SleepThemePresetId,
    onSelectThemePreset: (SleepThemePresetId) -> Unit,
    onDismiss: () -> Unit
) {
    val baseColorScheme = MaterialTheme.colorScheme
    val presetTiles = SleepThemePresetCatalog.descriptors.map { descriptor ->
        SleepThemePresetCatalog.presetFor(
            presetId = descriptor.id,
            baseColorScheme = baseColorScheme
        )
    }
    var showCustomMessage by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Sleep Theme")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SleepThemePreviewGrid(
                    presetTiles = presetTiles,
                    selectedThemePresetId = selectedThemePresetId,
                    onSelectThemePreset = onSelectThemePreset,
                    onCreateCustomThemeClick = {
                        showCustomMessage = true
                    }
                )

                if (showCustomMessage) {
                    Text(
                        text = "Custom theme creation will use this slot.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun SleepThemePreviewGrid(
    presetTiles: List<SleepThemePreset>,
    selectedThemePresetId: SleepThemePresetId,
    onSelectThemePreset: (SleepThemePresetId) -> Unit,
    onCreateCustomThemeClick: () -> Unit
) {
    val tiles: List<SleepThemeGridTile> = presetTiles.map { preset ->
        SleepThemeGridTile.Preset(preset)
    } + SleepThemeGridTile.Custom(SleepThemePresetCatalog.customActionDescriptor)

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
                        is SleepThemeGridTile.Preset -> {
                            SleepThemePresetTile(
                                preset = tile.preset,
                                selected = tile.preset.descriptor.id == selectedThemePresetId,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onSelectThemePreset(tile.preset.descriptor.id)
                                }
                            )
                        }

                        is SleepThemeGridTile.Custom -> {
                            SleepThemeCustomTile(
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
private fun SleepThemePresetTile(
    preset: SleepThemePreset,
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
            SleepThemeMiniPreview(preset = preset)

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
private fun SleepThemeCustomTile(
    descriptor: SleepThemeCustomActionDescriptor,
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
private fun SleepThemeMiniPreview(
    preset: SleepThemePreset
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

private sealed interface SleepThemeGridTile {
    data class Preset(
        val preset: SleepThemePreset
    ) : SleepThemeGridTile

    data class Custom(
        val descriptor: SleepThemeCustomActionDescriptor
    ) : SleepThemeGridTile
}
