package com.example.frontpage.auth.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureAuthPreferences(
    context: Context
) {
    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getCurrentUserId(): Long? {
        val savedUserId = preferences.getLong(KEY_CURRENT_USER_ID, NO_USER_ID)
        return if (savedUserId == NO_USER_ID) null else savedUserId
    }

    fun saveCurrentUserId(userId: Long) {
        preferences.edit()
            .putLong(KEY_CURRENT_USER_ID, userId)
            .apply()
    }

    fun clearCurrentUserId() {
        preferences.edit()
            .remove(KEY_CURRENT_USER_ID)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "secure_auth_preferences"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val NO_USER_ID = -1L
    }
}