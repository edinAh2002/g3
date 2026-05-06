package com.example.frontpage.auth.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.frontpage.auth.model.User

class AuthRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun getCurrentUserId(): Long? {
        val savedUserId = preferences.getLong(KEY_CURRENT_USER_ID, NO_USER_ID)
        return if (savedUserId == NO_USER_ID) null else savedUserId
    }

    suspend fun getCurrentUser(): User? {
        val currentUserId = getCurrentUserId() ?: return null
        return userDao.getUserById(currentUserId)
    }

    fun saveCurrentUserId(userId: Long) {
        preferences.edit()
            .putLong(KEY_CURRENT_USER_ID, userId)
            .apply()
    }

    fun logOut() {
        preferences.edit()
            .remove(KEY_CURRENT_USER_ID)
            .apply()
    }

    suspend fun signUp(username: String): Result<Long> {
        val cleanUsername = username.trim()

        if (cleanUsername.isBlank()) {
            return Result.failure(Exception("Username cannot be empty."))
        }

        if (cleanUsername.equals("Guest", ignoreCase = true) || cleanUsername == GUEST_USERNAME) {
            return Result.failure(Exception("That username is reserved."))
        }

        val existingUser = userDao.getUserByUsername(cleanUsername)

        if (existingUser != null) {
            return Result.failure(Exception("Username already exists."))
        }

        return try {
            val newUserId = userDao.insertUser(
                User(
                    username = cleanUsername,
                    isGuest = false
                )
            )

            saveCurrentUserId(newUserId)

            Result.success(newUserId)
        } catch (exception: SQLiteConstraintException) {
            Result.failure(Exception("Username already exists."))
        }
    }

    suspend fun logIn(username: String): Result<Long> {
        val cleanUsername = username.trim()

        if (cleanUsername.isBlank()) {
            return Result.failure(Exception("Username cannot be empty."))
        }

        val user = userDao.getUserByUsername(cleanUsername)

        if (user == null || user.isGuest) {
            return Result.failure(Exception("No account found with that username."))
        }

        saveCurrentUserId(user.id)

        return Result.success(user.id)
    }

    suspend fun continueAsGuest(): Result<Long> {
        val existingGuest = userDao.getGuestUser()

        if (existingGuest != null) {
            saveCurrentUserId(existingGuest.id)
            return Result.success(existingGuest.id)
        }

        return try {
            val guestId = userDao.insertUser(
                User(
                    username = GUEST_USERNAME,
                    isGuest = true
                )
            )

            saveCurrentUserId(guestId)

            Result.success(guestId)
        } catch (exception: SQLiteConstraintException) {
            val guest = userDao.getGuestUser()

            if (guest != null) {
                saveCurrentUserId(guest.id)
                Result.success(guest.id)
            } else {
                Result.failure(Exception("Could not continue as guest."))
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val NO_USER_ID = -1L
        private const val GUEST_USERNAME = "__guest__"
    }
}