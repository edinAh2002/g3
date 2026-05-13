package com.example.frontpage.food.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    val userId: Long = 0L,
    val mealName: String,
    val calories: Int? = null,
    val dateTime: Long = System.currentTimeMillis(),
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val mealType: String
)