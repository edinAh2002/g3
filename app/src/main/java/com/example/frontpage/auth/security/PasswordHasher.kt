package com.example.frontpage.auth.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 310_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 32

    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH_BYTES)
        SecureRandom().nextBytes(salt)

        return Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )
    }

    fun hashPassword(
        password: String,
        saltBase64: String
    ): String {
        val salt = Base64.decode(
            saltBase64,
            Base64.NO_WRAP
        )

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH_BITS
        )

        return try {
            val factory = SecretKeyFactory.getInstance(ALGORITHM)
            val hash = factory.generateSecret(spec).encoded

            Base64.encodeToString(
                hash,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
        }
    }

    fun verifyPassword(
        password: String,
        saltBase64: String,
        expectedHashBase64: String
    ): Boolean {
        return try {
            val actualHashBase64 = hashPassword(
                password = password,
                saltBase64 = saltBase64
            )

            val actualHash = Base64.decode(
                actualHashBase64,
                Base64.NO_WRAP
            )

            val expectedHash = Base64.decode(
                expectedHashBase64,
                Base64.NO_WRAP
            )

            MessageDigest.isEqual(
                actualHash,
                expectedHash
            )
        } catch (exception: Exception) {
            false
        }
    }
}