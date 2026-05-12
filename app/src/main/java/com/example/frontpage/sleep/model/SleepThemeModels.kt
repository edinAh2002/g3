package com.example.frontpage.sleep.model

enum class SleepThemeTarget(
    val storageValue: String
) {
    Sleep("sleep")
}

enum class SleepThemePresetId(
    val storageValue: String
) {
    Default("default"),
    PurpleRecovery("purple_recovery");

    companion object {
        fun fromStorageValue(storageValue: String?): SleepThemePresetId {
            return entries.firstOrNull { presetId ->
                presetId.storageValue == storageValue
            } ?: Default
        }
    }
}

data class SleepThemePreference(
    val target: SleepThemeTarget = SleepThemeTarget.Sleep,
    val presetId: SleepThemePresetId = SleepThemePresetId.Default
)
