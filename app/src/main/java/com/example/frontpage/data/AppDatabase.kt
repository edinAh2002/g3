package com.example.frontpage.data

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.frontpage.auth.data.UserDao
import com.example.frontpage.auth.model.User
import com.example.frontpage.data.security.DatabasePassphraseManager
import com.example.frontpage.mood.data.MoodDao
import com.example.frontpage.mood.model.MoodEntry
import com.example.frontpage.sleep.data.SleepDao
import com.example.frontpage.sleep.model.SleepEntry
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import com.example.frontpage.food.data.FoodDao
import com.example.frontpage.food.model.FoodItem
import com.example.frontpage.theme.data.PageThemeDao
import com.example.frontpage.theme.model.PageThemeEntry
import com.example.frontpage.theme.model.PageThemePreferenceEntry
import com.example.frontpage.workout.data.WorkoutDao
import com.example.frontpage.workout.model.WorkoutEntry

@Database(
    entities = [
        MoodEntry::class,
        SleepEntry::class,
        User::class,
        FoodItem::class,
        PageThemePreferenceEntry::class,
        PageThemeEntry::class,
        WorkoutEntry::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moodDao(): MoodDao
    abstract fun sleepDao(): SleepDao
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun foodDao(): FoodDao
    abstract fun pageThemeDao(): PageThemeDao

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
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS page_theme_preferences (
                        userId INTEGER NOT NULL,
                        target TEXT NOT NULL,
                        presetId TEXT NOT NULL,
                        updatedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(userId, target)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS page_theme_entries (
                        id TEXT NOT NULL,
                        userId INTEGER NOT NULL,
                        target TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        description TEXT NOT NULL,
                        screenBackground INTEGER NOT NULL,
                        onBackground INTEGER NOT NULL,
                        onBackgroundMuted INTEGER NOT NULL,
                        cardContainer INTEGER NOT NULL,
                        onCard INTEGER NOT NULL,
                        onCardMuted INTEGER NOT NULL,
                        primary INTEGER NOT NULL,
                        primaryEnd INTEGER NOT NULL,
                        onPrimary INTEGER NOT NULL,
                        primarySoft INTEGER NOT NULL,
                        progressTrack INTEGER NOT NULL,
                        positive INTEGER NOT NULL,
                        warning INTEGER NOT NULL,
                        negative INTEGER NOT NULL,
                        outline INTEGER NOT NULL,
                        headerGradientStart INTEGER NOT NULL,
                        headerGradientEnd INTEGER NOT NULL,
                        onHeader INTEGER NOT NULL,
                        layoutStyle TEXT NOT NULL,
                        createdAtMillis INTEGER NOT NULL,
                        updatedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_page_theme_entries_userId_target
                    ON page_theme_entries(userId, target)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS page_theme_entries (
                        id TEXT NOT NULL,
                        userId INTEGER NOT NULL,
                        target TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        description TEXT NOT NULL,
                        screenBackground INTEGER NOT NULL,
                        onBackground INTEGER NOT NULL,
                        onBackgroundMuted INTEGER NOT NULL,
                        cardContainer INTEGER NOT NULL,
                        onCard INTEGER NOT NULL,
                        onCardMuted INTEGER NOT NULL,
                        primary INTEGER NOT NULL,
                        primaryEnd INTEGER NOT NULL,
                        onPrimary INTEGER NOT NULL,
                        primarySoft INTEGER NOT NULL,
                        progressTrack INTEGER NOT NULL,
                        positive INTEGER NOT NULL,
                        warning INTEGER NOT NULL,
                        negative INTEGER NOT NULL,
                        outline INTEGER NOT NULL,
                        headerGradientStart INTEGER NOT NULL,
                        headerGradientEnd INTEGER NOT NULL,
                        onHeader INTEGER NOT NULL,
                        layoutStyle TEXT NOT NULL,
                        createdAtMillis INTEGER NOT NULL,
                        updatedAtMillis INTEGER NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                if (db.tableExists("page_theme_custom_presets")) {
                    db.execSQL(
                        """
                        INSERT OR IGNORE INTO page_theme_entries (
                            id,
                            userId,
                            target,
                            displayName,
                            description,
                            screenBackground,
                            onBackground,
                            onBackgroundMuted,
                            cardContainer,
                            onCard,
                            onCardMuted,
                            primary,
                            primaryEnd,
                            onPrimary,
                            primarySoft,
                            progressTrack,
                            positive,
                            warning,
                            negative,
                            outline,
                            headerGradientStart,
                            headerGradientEnd,
                            onHeader,
                            layoutStyle,
                            createdAtMillis,
                            updatedAtMillis
                        )
                        SELECT
                            id,
                            userId,
                            target,
                            displayName,
                            description,
                            screenBackground,
                            onBackground,
                            onBackgroundMuted,
                            cardContainer,
                            onCard,
                            onCardMuted,
                            primary,
                            primaryEnd,
                            onPrimary,
                            primarySoft,
                            progressTrack,
                            positive,
                            warning,
                            negative,
                            outline,
                            headerGradientStart,
                            headerGradientEnd,
                            onHeader,
                            layoutStyle,
                            createdAtMillis,
                            updatedAtMillis
                        FROM page_theme_custom_presets
                        """.trimIndent()
                    )
                }
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_page_theme_entries_userId_target
                    ON page_theme_entries(userId, target)
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS food_items (
                        id INTEGER NOT NULL,
                        userId INTEGER NOT NULL,
                        mealName TEXT NOT NULL,
                        calories INTEGER,
                        dateTime INTEGER NOT NULL,
                        protein INTEGER NOT NULL,
                        carbs INTEGER NOT NULL,
                        fat INTEGER NOT NULL,
                        mealType TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS workout_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        dateMillis INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        exercisesText TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private fun SupportSQLiteDatabase.tableExists(tableName: String): Boolean {
            return query(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
                arrayOf(tableName)
            ).use { cursor -> cursor.moveToFirst() }
        }
    }
}
