package com.example.frontpage.sleep

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SleepViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: SleepRepository

    val sleepLogs: StateFlow<List<SleepEntry>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SleepRepository(database.sleepDao())

        sleepLogs = repository
            .getAllSleepLogs()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addSleep(entry: SleepEntry) {
        viewModelScope.launch {
            repository.addSleep(entry)
        }
    }

    fun updateSleep(entry: SleepEntry) {
        viewModelScope.launch {
            repository.updateSleep(entry)
        }
    }

    fun deleteSleep(id: Long) {
        viewModelScope.launch {
            repository.deleteSleep(id)
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
        }
    }
}