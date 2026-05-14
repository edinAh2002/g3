package com.example.frontpage.sleep.model

enum class SleepSource(
    val label: String
) {
    Manual("Manual"),
    Detected("Detected"),
    HealthConnect("Health Connect"),
    Wearable("Wearable")
}
