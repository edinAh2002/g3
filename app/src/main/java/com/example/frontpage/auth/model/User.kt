package com.example.frontpage.auth.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val username: String,

    val isGuest: Boolean = false,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String? = null,

    @ColumnInfo(name = "password_salt")
    val passwordSalt: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)