package com.example.frontpage.mood.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.frontpage.mood.model.MoodEntry

@Stable
class MoodFeatureController internal constructor() {

    var showLogDialog by mutableStateOf(false)
        private set

    var editingEntry by mutableStateOf<MoodEntry?>(null)
        private set

    fun openLogDialog() {
        editingEntry = null
        showLogDialog = true
    }

    fun openEditDialog(entry: MoodEntry) {
        editingEntry = entry
        showLogDialog = true
    }

    fun closeLogDialog() {
        showLogDialog = false
        editingEntry = null
    }
}

