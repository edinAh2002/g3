package com.example.frontpage.sleep.model

data class SleepHealthConnectState(
    val availability: HealthConnectAvailability = HealthConnectAvailability.Unknown,
    val hasSleepPermission: Boolean = false,
    val isImporting: Boolean = false,
    val lastImportMessage: String? = null
) {
    val canRequestPermission: Boolean
        get() = availability == HealthConnectAvailability.Available

    val canImport: Boolean
        get() = availability == HealthConnectAvailability.Available &&
                hasSleepPermission &&
                !isImporting
}

enum class HealthConnectAvailability(
    val label: String
) {
    Unknown("Checking Health Connect"),
    Available("Available"),
    ProviderUpdateRequired("Install or update Health Connect"),
    Unavailable("Unavailable")
}
