package com.example.frontpage.food.data

import com.example.frontpage.food.model.FoodItem
import kotlinx.coroutines.flow.Flow

class FoodRepository (
    private val foodDao: FoodDao
) {
    fun getFoodItemsForUser(userId: Long): Flow<List<FoodItem>> {
        return foodDao.getFoodItemsForUser(userId)
    }

    fun getLatestFoodItemForUser(userId: Long): Flow<FoodItem?> {
        return foodDao.getLatestFoodItemForUser(userId)
    }

    suspend fun addFoodItem(
        userId: Long,
        foodItem: FoodItem
    ) {
        foodDao.addFoodItem(
            foodItem.copy(userId = userId)
        )
    }

    suspend fun updateFoodItem(
        userId: Long,
        foodItem: FoodItem
    ) {
        foodDao.updateFoodItem(
            foodItem.copy(userId = userId)
        )
    }

    suspend fun deleteFoodItem(
        userId: Long,
        id: Long
    ) {
        foodDao.deleteFoodItemForUser(
            id = id,
            userId = userId
        )
    }

    suspend fun clearFoodItems(userId: Long) {
        foodDao.clearFoodItemsForUser(userId)
    }
}