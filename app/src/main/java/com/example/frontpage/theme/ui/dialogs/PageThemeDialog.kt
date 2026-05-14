package com.example.frontpage.theme.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.frontpage.theme.domain.PageThemeCatalog
import com.example.frontpage.theme.model.PageThemeColors
import com.example.frontpage.theme.model.PageThemeCustomActionDescriptor
import com.example.frontpage.theme.model.PageThemeCustomPresetDraft
import com.example.frontpage.theme.model.PageThemePreset
import com.example.frontpage.theme.model.PageThemePresetId
import com.example.frontpage.theme.model.PageThemeTargetKey
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val THEME_PREVIEW_COLUMNS = 4

@Composable
fun PageThemeDialog(
    target: PageThemeTargetKey,
    catalog: PageThemeCatalog,
    selectedThemePresetId: PageThemePresetId,
    customThemePresets: List<PageThemePreset>,
    onSelectThemePreset: (PageThemePresetId) -> Unit,
    onCreateCustomTheme: (PageThemeCustomPresetDraft) -> Unit,
    onUpdateCustomTheme: (PageThemePresetId, PageThemeCustomPresetDraft) -> Unit,
    onDeleteCustomTheme: (PageThemePresetId) -> Unit,
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
    val selectedPreset = presetTiles.firstOrNull { preset ->
        preset.descriptor.id == selectedThemePresetId
    } ?: builtInPresetTiles.firstOrNull { preset ->
        preset.descriptor.id == targetConfig.defaultPresetId
    } ?: builtInPresetTiles.first()
    val selectedCustomPreset = customThemePresets.firstOrNull { preset ->
        preset.descriptor.id == selectedThemePresetId
    }

    var editorMode by remember { mutableStateOf<CustomThemeEditorMode?>(null) }
    var deletingPreset by remember { mutableStateOf<PageThemePreset?>(null) }

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
                        editorMode = CustomThemeEditorMode.Create(selectedPreset)
                    }
                )
            }
        },
        confirmButton = {
            ThemeDialogButtonBar(
                selectedCustomPreset = selectedCustomPreset,
                onEdit = { preset ->
                    editorMode = CustomThemeEditorMode.Edit(preset)
                },
                onDelete = { preset ->
                    deletingPreset = preset
                },
                onDone = onDismiss
            )
        }
    )

    editorMode?.let { mode ->
        CustomThemeEditorDialog(
            targetDisplayName = targetConfig.displayName,
            mode = mode,
            onDismiss = {
                editorMode = null
            },
            onSave = { draft ->
                when (mode) {
                    is CustomThemeEditorMode.Create -> onCreateCustomTheme(draft)
                    is CustomThemeEditorMode.Edit -> {
                        onUpdateCustomTheme(
                            mode.preset.descriptor.id,
                            draft
                        )
                    }
                }
                editorMode = null
            }
        )
    }

    deletingPreset?.let { preset ->
        DeleteCustomThemeDialog(
            preset = preset,
            onDismiss = {
                deletingPreset = null
            },
            onConfirmDelete = {
                onDeleteCustomTheme(preset.descriptor.id)
                deletingPreset = null
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
private fun ThemeDialogButtonBar(
    selectedCustomPreset: PageThemePreset?,
    onEdit: (PageThemePreset) -> Unit,
    onDelete: (PageThemePreset) -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedCustomPreset != null) {
                OutlinedButton(onClick = { onEdit(selectedCustomPreset) }) {
                    Text("Edit")
                }

                TextButton(onClick = { onDelete(selectedCustomPreset) }) {
                    Text("Delete")
                }
            }
        }

        TextButton(onClick = onDone) {
            Text("Done")
        }
    }
}

@Composable
private fun CustomThemeEditorDialog(
    targetDisplayName: String,
    mode: CustomThemeEditorMode,
    onDismiss: () -> Unit,
    onSave: (PageThemeCustomPresetDraft) -> Unit
) {
    val initialPreset = mode.initialPreset
    val dialogTitle = when (mode) {
        is CustomThemeEditorMode.Create -> "Custom $targetDisplayName Theme"
        is CustomThemeEditorMode.Edit -> "Edit ${initialPreset.descriptor.displayName}"
    }
    var themeName by remember(mode) {
        mutableStateOf(initialPreset.descriptor.displayName)
    }
    var colors by remember(mode) {
        mutableStateOf(initialPreset.colors)
    }
    var sliderMode by remember(mode) {
        mutableStateOf(ColorSliderMode.Rgb)
    }
    var manualTextColors by remember(mode) {
        mutableStateOf(mode is CustomThemeEditorMode.Edit)
    }

    fun updateSurfaceColor(transform: (PageThemeColors) -> PageThemeColors) {
        val nextColors = transform(colors)
        colors = if (manualTextColors) {
            nextColors
        } else {
            nextColors.withAutomaticTextColors()
        }
    }

    fun updateTextColor(transform: (PageThemeColors) -> PageThemeColors) {
        colors = transform(colors)
    }

    val previewPreset = initialPreset.copy(
        descriptor = initialPreset.descriptor.copy(
            displayName = themeName.trim().ifBlank {
                initialPreset.descriptor.displayName
            }
        ),
        colors = colors
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(dialogTitle)
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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

                ThemeMiniPreview(preset = previewPreset)
                SleepSamplePreview(preset = previewPreset)

                ColorSliderTabs(
                    selectedMode = sliderMode,
                    onModeSelected = { selectedMode ->
                        sliderMode = selectedMode
                    }
                )

                ThemeColorGroup(title = "Page background") {
                    ThemeColorControl(
                        label = "Screen background",
                        color = colors.screenBackground,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(screenBackground = color) }
                        }
                    )
                }

                ThemeColorGroup(title = "Cards") {
                    ThemeColorControl(
                        label = "Card background",
                        color = colors.cardContainer,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(cardContainer = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Soft accent",
                        color = colors.primarySoft,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(primarySoft = color) }
                        }
                    )
                }

                ThemeColorGroup(title = "Primary actions") {
                    ThemeColorControl(
                        label = "Primary",
                        color = colors.primary,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(primary = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Primary end",
                        color = colors.primaryEnd,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(primaryEnd = color) }
                        }
                    )
                }

                ThemeColorGroup(title = "Header gradient") {
                    ThemeColorControl(
                        label = "Header start",
                        color = colors.headerGradientStart,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(headerGradientStart = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Header end",
                        color = colors.headerGradientEnd,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(headerGradientEnd = color) }
                        }
                    )
                }

                ThemeColorGroup(title = "Progress and status") {
                    ThemeColorControl(
                        label = "Progress track",
                        color = colors.progressTrack,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(progressTrack = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Positive",
                        color = colors.positive,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(positive = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Warning",
                        color = colors.warning,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(warning = color) }
                        }
                    )
                    ThemeColorControl(
                        label = "Negative",
                        color = colors.negative,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(negative = color) }
                        }
                    )
                }

                ThemeColorGroup(title = "Outline") {
                    ThemeColorControl(
                        label = "Outline",
                        color = colors.outline,
                        sliderMode = sliderMode,
                        onColorChange = { color ->
                            updateSurfaceColor { it.copy(outline = color) }
                        }
                    )
                }

                TextButton(
                    onClick = {
                        val nextManualTextColors = !manualTextColors
                        manualTextColors = nextManualTextColors
                        if (!nextManualTextColors) {
                            colors = colors.withAutomaticTextColors()
                        }
                    }
                ) {
                    Text(
                        if (manualTextColors) {
                            "Use automatic text colors"
                        } else {
                            "Advanced text colors"
                        }
                    )
                }

                if (manualTextColors) {
                    ThemeColorGroup(title = "Advanced text colors") {
                        ThemeColorControl(
                            label = "On background",
                            color = colors.onBackground,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onBackground = color) }
                            }
                        )
                        ThemeColorControl(
                            label = "Muted background text",
                            color = colors.onBackgroundMuted,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onBackgroundMuted = color) }
                            }
                        )
                        ThemeColorControl(
                            label = "On card",
                            color = colors.onCard,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onCard = color) }
                            }
                        )
                        ThemeColorControl(
                            label = "Muted card text",
                            color = colors.onCardMuted,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onCardMuted = color) }
                            }
                        )
                        ThemeColorControl(
                            label = "On primary",
                            color = colors.onPrimary,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onPrimary = color) }
                            }
                        )
                        ThemeColorControl(
                            label = "On header",
                            color = colors.onHeader,
                            sliderMode = sliderMode,
                            onColorChange = { color ->
                                updateTextColor { it.copy(onHeader = color) }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cleanName = themeName.trim().ifBlank {
                        initialPreset.descriptor.displayName
                    }

                    onSave(
                        PageThemeCustomPresetDraft(
                            displayName = cleanName,
                            description = "Custom $targetDisplayName theme.",
                            colors = colors,
                            layoutStyle = initialPreset.layoutStyle
                        )
                    )
                }
            ) {
                Text(if (mode is CustomThemeEditorMode.Edit) "Save" else "Create")
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
private fun ColorSliderTabs(
    selectedMode: ColorSliderMode,
    onModeSelected: (ColorSliderMode) -> Unit
) {
    TabRow(selectedTabIndex = selectedMode.ordinal) {
        ColorSliderMode.entries.forEach { mode ->
            Tab(
                selected = selectedMode == mode,
                onClick = {
                    onModeSelected(mode)
                },
                text = {
                    Text(mode.label)
                }
            )
        }
    }
}

@Composable
private fun ThemeColorGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )

        content()
    }
}

@Composable
private fun ThemeColorControl(
    label: String,
    color: Color,
    sliderMode: ColorSliderMode,
    onColorChange: (Color) -> Unit
) {
    var hexText by remember { mutableStateOf(color.toHexString()) }

    LaunchedEffect(color) {
        hexText = color.toHexString()
    }

    val parsedHexColor = parseHexColor(hexText)
    val hexInvalid = hexText.isNotBlank() && parsedHexColor == null

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(32.dp)
                        .weight(0.16f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                )

                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(0.44f)
                )

                OutlinedTextField(
                    value = hexText,
                    onValueChange = { value ->
                        hexText = value
                        parseHexColor(value)?.let { parsedColor ->
                            onColorChange(parsedColor)
                        }
                    },
                    singleLine = true,
                    isError = hexInvalid,
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(0.4f)
                )
            }

            if (hexInvalid) {
                Text(
                    text = "Use #RRGGBB",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            when (sliderMode) {
                ColorSliderMode.Rgb -> RgbSliders(
                    color = color,
                    onColorChange = onColorChange
                )

                ColorSliderMode.Hsv -> HsvSliders(
                    color = color,
                    onColorChange = onColorChange
                )
            }
        }
    }
}

@Composable
private fun RgbSliders(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    ColorChannelSlider(
        label = "R",
        value = color.red * 255f,
        valueRange = 0f..255f,
        onValueChange = { value ->
            onColorChange(
                Color(
                    red = value.roundToInt().coerceIn(0, 255) / 255f,
                    green = color.green,
                    blue = color.blue,
                    alpha = color.alpha
                )
            )
        }
    )
    ColorChannelSlider(
        label = "G",
        value = color.green * 255f,
        valueRange = 0f..255f,
        onValueChange = { value ->
            onColorChange(
                Color(
                    red = color.red,
                    green = value.roundToInt().coerceIn(0, 255) / 255f,
                    blue = color.blue,
                    alpha = color.alpha
                )
            )
        }
    )
    ColorChannelSlider(
        label = "B",
        value = color.blue * 255f,
        valueRange = 0f..255f,
        onValueChange = { value ->
            onColorChange(
                Color(
                    red = color.red,
                    green = color.green,
                    blue = value.roundToInt().coerceIn(0, 255) / 255f,
                    alpha = color.alpha
                )
            )
        }
    )
}

@Composable
private fun HsvSliders(
    color: Color,
    onColorChange: (Color) -> Unit
) {
    val hsvColor = color.toHsvColor()

    ColorChannelSlider(
        label = "H",
        value = hsvColor.hue,
        valueRange = 0f..360f,
        onValueChange = { value ->
            onColorChange(
                colorFromHsv(
                    hue = value,
                    saturation = hsvColor.saturation,
                    value = hsvColor.value,
                    alpha = color.alpha
                )
            )
        }
    )
    ColorChannelSlider(
        label = "S",
        value = hsvColor.saturation * 100f,
        valueRange = 0f..100f,
        onValueChange = { value ->
            onColorChange(
                colorFromHsv(
                    hue = hsvColor.hue,
                    saturation = value.coerceIn(0f, 100f) / 100f,
                    value = hsvColor.value,
                    alpha = color.alpha
                )
            )
        }
    )
    ColorChannelSlider(
        label = "V",
        value = hsvColor.value * 100f,
        valueRange = 0f..100f,
        onValueChange = { value ->
            onColorChange(
                colorFromHsv(
                    hue = hsvColor.hue,
                    saturation = hsvColor.saturation,
                    value = value.coerceIn(0f, 100f) / 100f,
                    alpha = color.alpha
                )
            )
        }
    )
}

@Composable
private fun ColorChannelSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(0.12f)
        )
        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(0.68f)
        )
        Text(
            text = value.roundToInt().toString(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.2f)
        )
    }
}

@Composable
private fun SleepSamplePreview(
    preset: PageThemePreset
) {
    val colors = preset.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.screenBackground,
            contentColor = colors.onBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colors.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colors.headerGradientStart,
                                colors.headerGradientEnd
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Sleep Tracker",
                        color = colors.onHeader,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Rest and recover",
                        color = colors.onHeader.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colors.cardContainer,
                    contentColor = colors.onCard
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Latest sleep",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "7h 45m logged",
                                color = colors.onCardMuted,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = colors.onPrimary
                            )
                        ) {
                            Text("Log")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colors.progressTrack)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.72f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            colors.negative,
                                            colors.warning,
                                            colors.positive
                                        )
                                    )
                                )
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SleepPreviewChip(
                    label = "Goal",
                    color = colors.primarySoft,
                    contentColor = colors.primary,
                    modifier = Modifier.weight(1f)
                )
                SleepPreviewChip(
                    label = "Good",
                    color = colors.positive,
                    contentColor = colors.onPrimary,
                    modifier = Modifier.weight(1f)
                )
                SleepPreviewChip(
                    label = "Risk",
                    color = colors.negative,
                    contentColor = colors.onPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SleepPreviewChip(
    label: String,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(50))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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

@Composable
private fun DeleteCustomThemeDialog(
    preset: PageThemePreset,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Delete custom theme?")
        },
        text = {
            Text("Delete ${preset.descriptor.displayName}? This will switch Sleep back to the default theme.")
        },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun PageThemeColors.withAutomaticTextColors(): PageThemeColors {
    val onBackgroundColor = readableTextColor(screenBackground)
    val onCardColor = readableTextColor(cardContainer)
    val onPrimaryColor = readableTextColor(primary)
    val headerAverage = averageColor(headerGradientStart, headerGradientEnd)
    val onHeaderColor = readableTextColor(headerAverage)

    return copy(
        onBackground = onBackgroundColor,
        onBackgroundMuted = onBackgroundColor.copy(alpha = 0.72f),
        onCard = onCardColor,
        onCardMuted = onCardColor.copy(alpha = 0.68f),
        onPrimary = onPrimaryColor,
        onHeader = onHeaderColor
    )
}

private fun readableTextColor(background: Color): Color {
    return if (background.luminance() > 0.52f) {
        Color(0xFF182028)
    } else {
        Color(0xFFF9FBFD)
    }
}

private fun averageColor(
    first: Color,
    second: Color
): Color {
    return Color(
        red = (first.red + second.red) / 2f,
        green = (first.green + second.green) / 2f,
        blue = (first.blue + second.blue) / 2f,
        alpha = (first.alpha + second.alpha) / 2f
    )
}

private fun Color.toHexString(): String {
    return String.format(
        Locale.US,
        "#%06X",
        toArgb() and 0x00FFFFFF
    )
}

private fun parseHexColor(value: String): Color? {
    val cleanValue = value.trim().removePrefix("#")
    if (cleanValue.length != 6 || cleanValue.any { character -> !character.isHexDigit() }) {
        return null
    }

    val rgb = cleanValue.toLongOrNull(16)?.toInt() ?: return null
    return Color(0xFF000000.toInt() or rgb)
}

private fun Char.isHexDigit(): Boolean {
    return this in '0'..'9' ||
        this in 'a'..'f' ||
        this in 'A'..'F'
}

private fun Color.toHsvColor(): HsvColor {
    val maxChannel = max(red, max(green, blue))
    val minChannel = min(red, min(green, blue))
    val delta = maxChannel - minChannel
    val rawHue = when {
        delta == 0f -> 0f
        maxChannel == red -> 60f * (((green - blue) / delta) % 6f)
        maxChannel == green -> 60f * (((blue - red) / delta) + 2f)
        else -> 60f * (((red - green) / delta) + 4f)
    }
    val hue = if (rawHue < 0f) rawHue + 360f else rawHue
    val saturation = if (maxChannel == 0f) 0f else delta / maxChannel

    return HsvColor(
        hue = hue,
        saturation = saturation,
        value = maxChannel
    )
}

private fun colorFromHsv(
    hue: Float,
    saturation: Float,
    value: Float,
    alpha: Float
): Color {
    val normalizedHue = ((hue % 360f) + 360f) % 360f
    val cleanSaturation = saturation.coerceIn(0f, 1f)
    val cleanValue = value.coerceIn(0f, 1f)
    val chroma = cleanValue * cleanSaturation
    val intermediate = chroma * (1f - abs((normalizedHue / 60f) % 2f - 1f))
    val match = cleanValue - chroma
    val channels = when {
        normalizedHue < 60f -> Triple(chroma, intermediate, 0f)
        normalizedHue < 120f -> Triple(intermediate, chroma, 0f)
        normalizedHue < 180f -> Triple(0f, chroma, intermediate)
        normalizedHue < 240f -> Triple(0f, intermediate, chroma)
        normalizedHue < 300f -> Triple(intermediate, 0f, chroma)
        else -> Triple(chroma, 0f, intermediate)
    }

    return Color(
        red = channels.first + match,
        green = channels.second + match,
        blue = channels.third + match,
        alpha = alpha
    )
}

private enum class ColorSliderMode(
    val label: String
) {
    Rgb("RGB"),
    Hsv("HSV")
}

private data class HsvColor(
    val hue: Float,
    val saturation: Float,
    val value: Float
)

private sealed interface CustomThemeEditorMode {
    val initialPreset: PageThemePreset

    data class Create(
        override val initialPreset: PageThemePreset
    ) : CustomThemeEditorMode

    data class Edit(
        val preset: PageThemePreset
    ) : CustomThemeEditorMode {
        override val initialPreset: PageThemePreset
            get() = preset
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
