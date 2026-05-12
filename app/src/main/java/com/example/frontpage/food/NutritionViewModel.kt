package com.example.frontpage.food

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.food.data.FoodRepository
import com.example.frontpage.food.model.FoodItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: FoodRepository
    private val authRepository: AuthRepository

    private val currentUserId = MutableStateFlow<Long?>(null)

    val foodItems: StateFlow<List<FoodItem>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository= FoodRepository(
            foodDao = database.foodDao()
        )

        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = application.applicationContext
        )

        currentUserId.value = authRepository.getCurrentUserId()

        foodItems = currentUserId
            .flatMapLatest { userId ->
                if (userId == null) {
                    flowOf(emptyList())
                } else {
                    repository.getFoodItemsForUser(userId)
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
    }

    private fun getCurrentUserIdOrRefresh(): Long? {
        val userId = authRepository.getCurrentUserId()
        currentUserId.value = userId
        return userId
    }

    fun addFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.addFoodItem(
                userId = userId,
                foodItem = foodItem
            )
        }
    }

    fun updateFoodItem(foodItem: FoodItem) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.updateFoodItem(
                userId = userId,
                foodItem = foodItem
            )
        }
    }

    fun deleteFoodItem(id: Long) {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.deleteFoodItem(
                userId = userId,
                id = id
            )
        }
    }

    fun clearFoodItems() {
        viewModelScope.launch {
            val userId = getCurrentUserIdOrRefresh() ?: return@launch

            repository.clearFoodItems(userId)
        }
    }
}