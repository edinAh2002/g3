package com.example.frontpage.sleep

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.sleep.data.SleepRepository
import com.example.frontpage.sleep.data.SleepSettingsRepository
import com.example.frontpage.sleep.model.SleepEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SleepViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: SleepRepository
    private val authRepository: AuthRepository

    private val authPreferences: SharedPreferences
    private val appContext = application.applicationContext

    private val currentUserId = MutableStateFlow<Long?>(null)

    val sleepLogs: StateFlow<List<SleepEntry>>

    private val _goalMinutes = MutableStateFlow(SleepSettingsRepository.DEFAULT_SLEEP_GOAL_MINUTES)
    val goalMinutes: StateFlow<Int> = _goalMinutes

    private val authPreferenceListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            refreshCurrentUser()
        }

    init {
        val database = AppDatabase.getDatabase(application)

        repository = SleepRepository(database.sleepDao())

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        authPreferences = application.applicationContext.getSharedPreferences(
            AUTH_PREFS_NAME,
            Context.MODE_PRIVATE
        )

        authPreferences.registerOnSharedPreferenceChangeListener(authPreferenceListener)

        currentUserId.value = authRepository.getCurrentUserId()
        refreshSleepGoal()

        sleepLogs = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getSleepLogsForUser(userId)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun refreshCurrentUser() {
        currentUserId.value = authRepository.getCurrentUserId()
        refreshSleepGoal()
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        refreshSleepGoal()
        return userId
    }

    fun updateSleepGoalMinutes(newGoalMinutes: Int) {
        val updatedGoalMinutes = SleepSettingsRepository.updateSleepGoalMinutes(
            context = appContext,
            userId = getCurrentUserIdOrRefresh(),
            newGoalMinutes = newGoalMinutes
        )

        _goalMinutes.value = updatedGoalMinutes
    }

    fun addSleep(entry: SleepEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.addSleep(
                userId = userId,
                entry = entry
            )
        }
    }

    fun updateSleep(entry: SleepEntry) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.updateSleep(
                userId = userId,
                entry = entry
            )
        }
    }

    fun deleteSleep(id: Long) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.deleteSleep(
                userId = userId,
                id = id
            )
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.clearAllLogs(userId)
        }
    }

    private fun refreshSleepGoal() {
        _goalMinutes.value = SleepSettingsRepository.getSleepGoalMinutes(
            context = appContext,
            userId = currentUserId.value
        )
    }

    override fun onCleared() {
        authPreferences.unregisterOnSharedPreferenceChangeListener(authPreferenceListener)
        super.onCleared()
    }

    companion object {
        private const val AUTH_PREFS_NAME = "auth_preferences"
    }
}
