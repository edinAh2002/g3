package com.example.frontpage.sleep.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepEntry

@Stable
class SleepFeatureController internal constructor() {

    var showLogDialog by mutableStateOf(false)
        private set

    var editingEntry by mutableStateOf<SleepEntry?>(null)
        private set

    var reviewingDetectionCandidate by mutableStateOf<SleepDetectionCandidate?>(null)
        private set

    fun openLogDialog() {
        editingEntry = null
        reviewingDetectionCandidate = null
        showLogDialog = true
    }

    fun openEditDialog(entry: SleepEntry) {
        editingEntry = entry
        reviewingDetectionCandidate = null
        showLogDialog = true
    }

    fun openDetectedSleepReview(candidate: SleepDetectionCandidate) {
        editingEntry = null
        reviewingDetectionCandidate = candidate
        showLogDialog = true
    }

    fun closeLogDialog() {
        showLogDialog = false
        editingEntry = null
        reviewingDetectionCandidate = null
    }
}
