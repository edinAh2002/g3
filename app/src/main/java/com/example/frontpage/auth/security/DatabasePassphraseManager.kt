package com.example.frontpage.data.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

class DatabasePassphraseManager(
    context: Context
) {
    private val appContext = context.applicationContext

    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        appContext,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getOrCreatePassphrase(): ByteArray {
        val existingPassphrase = preferences.getString(KEY_DATABASE_PASSPHRASE, null)

        if (existingPassphrase != null) {
            return Base64.decode(
                existingPassphrase,
                Base64.NO_WRAP
            )
        }

        val newPassphrase = ByteArray(PASSPHRASE_LENGTH_BYTES)
        SecureRandom().nextBytes(newPassphrase)

        val encodedPassphrase = Base64.encodeToString(
            newPassphrase,
            Base64.NO_WRAP
        )

        preferences.edit()
            .putString(KEY_DATABASE_PASSPHRASE, encodedPassphrase)
            .apply()

        return newPassphrase
    }

    companion object {
        private const val PREFS_NAME = "secure_database_preferences"
        private const val KEY_DATABASE_PASSPHRASE = "database_passphrase"
        private const val PASSPHRASE_LENGTH_BYTES = 32
    }
}