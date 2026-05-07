package com.example.frontpage.sleep.model

import android.net.Uri

enum class SleepTag(
    val label: String,
    val category: String
) {
    ConsistentBedtime("Consistent bedtime", "Routine"),
    LateBedtime("Late bedtime", "Routine"),
    EarlyWake("Early wake", "Routine"),
    SleptIn("Slept in", "Routine"),
    NapDay("Nap day", "Routine"),

    Stress("Stress", "Mind"),
    CalmMind("Calm mind", "Mind"),
    Anxiety("Anxiety", "Mind"),
    DreamHeavy("Dream-heavy", "Mind"),
    Nightmare("Nightmare", "Mind"),

    Caffeine("Caffeine", "Lifestyle"),
    Alcohol("Alcohol", "Lifestyle"),
    LateMeal("Late meal", "Lifestyle"),
    LateWorkout("Late workout", "Lifestyle"),
    ScreensLate("Screens late", "Lifestyle"),

    HotRoom("Hot room", "Environment"),
    ColdRoom("Cold room", "Environment"),
    Noise("Noise", "Environment"),
    Light("Light", "Environment"),
    Travel("Travel", "Environment");

    companion object {
        private const val CUSTOM_PREFIX = "custom:"
        private const val CUSTOM_SEPARATOR = ":"

        fun builtInOptions(): List<SleepTagOption> {
            return entries.map { tag ->
                SleepTagOption(
                    storageValue = tag.name,
                    label = tag.label,
                    category = tag.category
                )
            }
        }

        fun customOption(customTag: SleepCustomTag): SleepTagOption {
            return SleepTagOption(
                storageValue = customStorageValue(
                    id = customTag.id,
                    label = customTag.label
                ),
                label = customTag.label,
                category = "Custom",
                customId = customTag.id
            )
        }

        fun fromStorage(storageValue: String): List<SleepTag> {
            if (storageValue.isBlank()) return emptyList()

            return storageValue.split(",")
                .mapNotNull { tagName ->
                    entries.firstOrNull { tag ->
                        tag.name == tagName.trim()
                    }
                }
        }

        fun optionsFromStorage(storageValue: String): List<SleepTagOption> {
            if (storageValue.isBlank()) return emptyList()

            return storageValue.split(",")
                .mapNotNull { token ->
                    optionFromStorageToken(token.trim())
                }
        }

        fun toStorage(tags: List<SleepTag>): String {
            return tags.distinct()
                .joinToString(",") { tag -> tag.name }
        }

        fun toStorageOptions(tags: List<SleepTagOption>): String {
            return tags.distinctBy { tag -> tag.storageValue }
                .joinToString(",") { tag -> tag.storageValue }
        }

        private fun optionFromStorageToken(token: String): SleepTagOption? {
            if (token.isBlank()) return null

            val builtInTag = entries.firstOrNull { tag ->
                tag.name == token || "builtin:${tag.name}" == token
            }

            if (builtInTag != null) {
                return SleepTagOption(
                    storageValue = builtInTag.name,
                    label = builtInTag.label,
                    category = builtInTag.category
                )
            }

            if (!token.startsWith(CUSTOM_PREFIX)) return null

            val customParts = token.removePrefix(CUSTOM_PREFIX)
                .split(CUSTOM_SEPARATOR, limit = 2)

            val customId = customParts.getOrNull(0)?.takeIf { id -> id.isNotBlank() }
                ?: return null

            val label = customParts.getOrNull(1)
                ?.let { encodedLabel -> Uri.decode(encodedLabel) }
                ?.takeIf { decodedLabel -> decodedLabel.isNotBlank() }
                ?: "Custom tag"

            return SleepTagOption(
                storageValue = token,
                label = label,
                category = "Custom",
                customId = customId
            )
        }

        private fun customStorageValue(
            id: String,
            label: String
        ): String {
            return "$CUSTOM_PREFIX$id$CUSTOM_SEPARATOR${Uri.encode(label)}"
        }
    }
}
