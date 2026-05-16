package com.example.frontpage.mood.model

data class MoodScaleOption(
    val value: Int,
    val label: String,
    val description: String
)

enum class MoodScalePreset(
    val storageKey: String,
    val label: String,
    val description: String,
    val options: List<MoodScaleOption>
) {
    Feelings(
        storageKey = "feelings",
        label = "Feelings",
        description = "A feelings-first scale for naming what is present.",
        options = listOf(
            MoodScaleOption(
                value = 1,
                label = "Overwhelmed",
                description = "Too much is happening or the feeling is hard to carry."
            ),
            MoodScaleOption(
                value = 2,
                label = "Sad",
                description = "Low, heavy, worried, or discouraged."
            ),
            MoodScaleOption(
                value = 3,
                label = "Calm",
                description = "Neutral, steady, or emotionally even."
            ),
            MoodScaleOption(
                value = 4,
                label = "Content",
                description = "Mostly good, settled, or quietly positive."
            ),
            MoodScaleOption(
                value = 5,
                label = "Joyful",
                description = "Bright, energized, grateful, or deeply good."
            )
        )
    ),
    Classic(
        storageKey = "classic",
        label = "Classic",
        description = "A direct low-to-high mood scale.",
        options = listOf(
            MoodScaleOption(
                value = 1,
                label = "Very bad",
                description = "A hard moment. Keep the log gentle and specific."
            ),
            MoodScaleOption(
                value = 2,
                label = "Bad",
                description = "A low mood. Notes can help reveal what is weighing on you."
            ),
            MoodScaleOption(
                value = 3,
                label = "Okay",
                description = "A neutral mood. Useful for spotting steady days."
            ),
            MoodScaleOption(
                value = 4,
                label = "Good",
                description = "A good mood. Look for what supported it."
            ),
            MoodScaleOption(
                value = 5,
                label = "Great",
                description = "A great mood. Capture the conditions worth repeating."
            )
        )
    ),
    Emoji(
        storageKey = "emoji",
        label = "Emoji",
        description = "A quick visual scale with emojis.",
        options = listOf(
            MoodScaleOption(
                value = 1,
                label = "😞 Very low",
                description = "A difficult or draining mood."
            ),
            MoodScaleOption(
                value = 2,
                label = "😕 Low",
                description = "Not feeling like yourself yet."
            ),
            MoodScaleOption(
                value = 3,
                label = "😐 Neutral",
                description = "Neither especially low nor especially high."
            ),
            MoodScaleOption(
                value = 4,
                label = "🙂 Good",
                description = "A positive, manageable mood."
            ),
            MoodScaleOption(
                value = 5,
                label = "😄 Great",
                description = "A strong, bright mood."
            )
        )
    );

    fun optionForValue(value: Int): MoodScaleOption {
        return options.firstOrNull { option -> option.value == value }
            ?: MoodScaleOption(
                value = value,
                label = "Unknown",
                description = "Select a mood from 1 to 5."
            )
    }

    companion object {
        val Default = Feelings

        fun fromStorageKey(storageKey: String?): MoodScalePreset {
            return entries.firstOrNull { preset -> preset.storageKey == storageKey }
                ?: Default
        }
    }
}

