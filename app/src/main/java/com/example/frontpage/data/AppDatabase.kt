package com.example.frontpage.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frontpage.mood.data.MoodDao
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.sleep.data.SleepDao
import com.example.frontpage.sleep.model.SleepEntry

@Database(
    entities = [
        MoodEntry::class,
        SleepEntry::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moodDao(): MoodDao
    abstract fun sleepDao(): SleepDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "frontpage_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}