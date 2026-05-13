package com.example.frontpage.theme.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "page_theme_preferences",
    primaryKeys = ["userId", "target"]
)
data class PageThemePreferenceEntry(
    val userId: Long,
    val target: String,
    val presetId: String,
    val updatedAtMillis: Long
)

@Entity(
    tableName = "page_theme_entries",
    indices = [
        Index(value = ["userId", "target"])
    ]
)
data class PageThemeEntry(
    @PrimaryKey
    val id: String,
    val userId: Long,
    val target: String,
    val displayName: String,
    val description: String,
    val screenBackground: Int,
    val onBackground: Int,
    val onBackgroundMuted: Int,
    val cardContainer: Int,
    val onCard: Int,
    val onCardMuted: Int,
    val primary: Int,
    val primaryEnd: Int,
    val onPrimary: Int,
    val primarySoft: Int,
    val progressTrack: Int,
    val positive: Int,
    val warning: Int,
    val negative: Int,
    val outline: Int,
    val headerGradientStart: Int,
    val headerGradientEnd: Int,
    val onHeader: Int,
    val layoutStyle: String,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

fun PageThemeEntry.toPreset(): PageThemePreset {
    return PageThemePreset(
        descriptor = PageThemePresetDescriptor(
            target = PageThemeTargetKey(target),
            id = PageThemePresetId(id),
            displayName = displayName,
            description = description,
            isCustom = true
        ),
        colors = PageThemeColors(
            screenBackground = Color(screenBackground),
            onBackground = Color(onBackground),
            onBackgroundMuted = Color(onBackgroundMuted),
            cardContainer = Color(cardContainer),
            onCard = Color(onCard),
            onCardMuted = Color(onCardMuted),
            primary = Color(primary),
            primaryEnd = Color(primaryEnd),
            onPrimary = Color(onPrimary),
            primarySoft = Color(primarySoft),
            progressTrack = Color(progressTrack),
            positive = Color(positive),
            warning = Color(warning),
            negative = Color(negative),
            outline = Color(outline),
            headerGradientStart = Color(headerGradientStart),
            headerGradientEnd = Color(headerGradientEnd),
            onHeader = Color(onHeader)
        ),
        layoutStyle = PageThemeLayoutStyle.entries.firstOrNull { style ->
            style.name == layoutStyle
        } ?: PageThemeLayoutStyle.Standard
    )
}

fun PageThemePreset.toCustomPresetEntity(
    userId: Long,
    createdAtMillis: Long,
    updatedAtMillis: Long
): PageThemeEntry {
    val presetColors = colors
    return PageThemeEntry(
        id = descriptor.id.storageValue,
        userId = userId,
        target = descriptor.target.storageValue,
        displayName = descriptor.displayName,
        description = descriptor.description,
        screenBackground = presetColors.screenBackground.toArgb(),
        onBackground = presetColors.onBackground.toArgb(),
        onBackgroundMuted = presetColors.onBackgroundMuted.toArgb(),
        cardContainer = presetColors.cardContainer.toArgb(),
        onCard = presetColors.onCard.toArgb(),
        onCardMuted = presetColors.onCardMuted.toArgb(),
        primary = presetColors.primary.toArgb(),
        primaryEnd = presetColors.primaryEnd.toArgb(),
        onPrimary = presetColors.onPrimary.toArgb(),
        primarySoft = presetColors.primarySoft.toArgb(),
        progressTrack = presetColors.progressTrack.toArgb(),
        positive = presetColors.positive.toArgb(),
        warning = presetColors.warning.toArgb(),
        negative = presetColors.negative.toArgb(),
        outline = presetColors.outline.toArgb(),
        headerGradientStart = presetColors.headerGradientStart.toArgb(),
        headerGradientEnd = presetColors.headerGradientEnd.toArgb(),
        onHeader = presetColors.onHeader.toArgb(),
        layoutStyle = layoutStyle.name,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}
