package com.example.frontpage.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frontpage.auth.data.UserDao
import com.example.frontpage.auth.model.User
import com.example.frontpage.data.security.DatabasePassphraseManager
import com.example.frontpage.mood.data.MoodDao
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.sleep.data.SleepDao
import com.example.frontpage.sleep.model.SleepEntry
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import com.example.frontpage.workout.data.WorkoutDao
import com.example.frontpage.workout.model.WorkoutEntry

@Database(
    entities = [
        MoodEntry::class,
        SleepEntry::class,
        User::class,
        WorkoutEntry::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moodDao(): MoodDao
    abstract fun sleepDao(): SleepDao
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "frontpage_database_encrypted"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext

                System.loadLibrary("sqlcipher")

                val passphrase = DatabasePassphraseManager(appContext)
                    .getOrCreatePassphrase()

                val factory = SupportOpenHelperFactory(passphrase)

                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
