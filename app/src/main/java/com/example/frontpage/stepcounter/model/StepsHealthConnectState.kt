package com.example.frontpage.stepcounter.model

import com.example.frontpage.sleep.model.HealthConnectAvailability

data class StepsHealthConnectState(
    val availability: HealthConnectAvailability,
    val hasStepsPermission: Boolean
)