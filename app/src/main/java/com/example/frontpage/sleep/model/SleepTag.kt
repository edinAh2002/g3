package com.example.frontpage.sleep.model

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
        fun fromStorage(storageValue: String): List<SleepTag> {
            if (storageValue.isBlank()) return emptyList()

            return storageValue.split(",")
                .mapNotNull { tagName ->
                    entries.firstOrNull { tag ->
                        tag.name == tagName.trim()
                    }
                }
        }

        fun toStorage(tags: List<SleepTag>): String {
            return tags.distinct()
                .joinToString(",") { tag -> tag.name }
        }
    }
}
