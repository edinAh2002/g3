package com.example.frontpage.auth.data

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.frontpage.auth.model.User
import com.example.frontpage.auth.security.PasswordHasher
import com.example.frontpage.auth.security.SecureAuthPreferences

class AuthRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val secureAuthPreferences = SecureAuthPreferences(
        context = context.applicationContext
    )

    fun getCurrentUserId(): Long? {
        return secureAuthPreferences.getCurrentUserId()
    }

    suspend fun getCurrentUser(): User? {
        val currentUserId = getCurrentUserId() ?: return null
        return userDao.getUserById(currentUserId)
    }

    fun saveCurrentUserId(userId: Long) {
        secureAuthPreferences.saveCurrentUserId(userId)
    }

    fun logOut() {
        secureAuthPreferences.clearCurrentUserId()
    }

    suspend fun signUp(
        username: String,
        password: String,
        confirmPassword: String
    ): Result<Long> {
        val cleanUsername = username.trim()

        if (cleanUsername.isBlank()) {
            return Result.failure(Exception("Username cannot be empty."))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("PIN/password cannot be empty."))
        }

        if (password.length < MIN_PASSWORD_LENGTH) {
            return Result.failure(Exception("PIN/password must be at least 6 characters."))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("PIN/passwords do not match."))
        }

        if (cleanUsername.equals("Guest", ignoreCase = true) || cleanUsername == GUEST_USERNAME) {
            return Result.failure(Exception("That username is reserved."))
        }

        val existingUser = userDao.getUserByUsername(cleanUsername)

        if (existingUser != null) {
            return Result.failure(Exception("Username already exists."))
        }

        val salt = PasswordHasher.generateSalt()
        val passwordHash = PasswordHasher.hashPassword(
            password = password,
            saltBase64 = salt
        )

        return try {
            val newUserId = userDao.insertUser(
                User(
                    username = cleanUsername,
                    isGuest = false,
                    passwordHash = passwordHash,
                    passwordSalt = salt
                )
            )

            saveCurrentUserId(newUserId)

            Result.success(newUserId)
        } catch (exception: SQLiteConstraintException) {
            Result.failure(Exception("Username already exists."))
        }
    }

    suspend fun logIn(
        username: String,
        password: String
    ): Result<Long> {
        val cleanUsername = username.trim()

        if (cleanUsername.isBlank()) {
            return Result.failure(Exception("Username cannot be empty."))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("PIN/password cannot be empty."))
        }

        val user = userDao.getUserByUsername(cleanUsername)

        if (user == null || user.isGuest) {
            return Result.failure(Exception("No account found with that username."))
        }

        val storedHash = user.passwordHash
        val storedSalt = user.passwordSalt

        if (storedHash.isNullOrBlank() || storedSalt.isNullOrBlank()) {
            return Result.failure(Exception("This account does not have a PIN/password. Please create a new account."))
        }

        val passwordMatches = PasswordHasher.verifyPassword(
            password = password,
            saltBase64 = storedSalt,
            expectedHashBase64 = storedHash
        )

        if (!passwordMatches) {
            return Result.failure(Exception("Incorrect PIN/password."))
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
                    isGuest = true,
                    passwordHash = null,
                    passwordSalt = null
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
        private const val MIN_PASSWORD_LENGTH = 6
    }
}