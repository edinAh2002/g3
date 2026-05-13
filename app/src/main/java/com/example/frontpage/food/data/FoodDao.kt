package com.example.frontpage.food.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.frontpage.food.model.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Query(
        """
        SELECT * FROM food_items
        WHERE userId = :userId
        ORDER BY dateTime DESC
        """
    )
    fun getFoodItemsForUser(userId: Long): Flow<List<FoodItem>>

    @Query(
        """
        SELECT * FROM food_items
        WHERE userId = :userId
        ORDER BY dateTime DESC
        LIMIT 1
        """
    )
    fun getLatestFoodItemForUser(userId: Long): Flow<FoodItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFoodItem(foodItem: FoodItem)

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Query(
        """
        DELETE FROM food_items
        WHERE id = :id AND userId = :userId
        """
    )
    suspend fun deleteFoodItemForUser(
        id: Long,
        userId: Long
    )

    @Query(
        """
        DELETE FROM food_items
        WHERE userId = :userId
        """
    )
    suspend fun clearFoodItemsForUser(userId: Long)
}