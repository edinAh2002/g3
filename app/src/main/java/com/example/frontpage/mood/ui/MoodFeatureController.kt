package com.example.frontpage.mood.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class MoodFeatureController internal constructor() {

    var showTrackingDialog by mutableStateOf(false)
        private set

    fun openTrackingDialog() {
        showTrackingDialog = true
    }

    fun closeTrackingDialog() {
        showTrackingDialog = false
    }
}