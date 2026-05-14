package com.example.frontpage.sleep.data

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.frontpage.MainActivity
import com.example.frontpage.auth.data.AuthRepository
import com.example.frontpage.data.AppDatabase
import com.example.frontpage.sleep.domain.SleepDetectionAnalyzer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SleepDetectionService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private lateinit var sleepRepository: SleepLogDataSource
    private lateinit var detectionRepository: SleepDetectionDataSource
    private lateinit var settingsDataSource: SleepSettingsDataSource
    private lateinit var sessionStore: SleepDetectionSessionStore
    private lateinit var authRepository: AuthRepository

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> handleScreenOff()
                Intent.ACTION_SCREEN_ON,
                Intent.ACTION_USER_PRESENT -> handleScreenActive()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getDatabase(applicationContext)
        sleepRepository = SleepRepository(database.sleepDao())
        detectionRepository = SleepDetectionRepository(database.sleepDetectionDao())
        settingsDataSource = SharedPreferencesSleepSettingsDataSource(applicationContext)
        sessionStore = SleepDetectionSessionStore(applicationContext)
        authRepository = AuthRepository(
            userDao = database.userDao(),
            context = applicationContext
        )

        startForeground(NOTIFICATION_ID, createNotification())
        registerScreenReceiver()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (intent?.action == ACTION_STOP) {
            sessionStore.clear()
            stopSelf()
            return START_NOT_STICKY
        }

        val userId = authRepository.getCurrentUserId()
        val settings = settingsDataSource.getSleepDetectionSettings(userId)
        if (!settings.enabled) {
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        runCatching {
            unregisterReceiver(screenReceiver)
        }
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun handleScreenOff() {
        serviceScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val settings = settingsDataSource.getSleepDetectionSettings(userId)
            if (!settings.enabled) return@launch

            val nowMillis = System.currentTimeMillis()
            val existingState = sessionStore.load()
            val interruptionStartMillis = existingState?.interruptionStartMillis

            if (existingState != null && interruptionStartMillis != null) {
                val interruptionMillis = nowMillis - interruptionStartMillis
                val toleranceMillis = settings.interruptionToleranceMinutes * MILLIS_PER_MINUTE

                if (interruptionMillis <= toleranceMillis) {
                    sessionStore.save(
                        existingState.copy(
                            interruptionMillis = existingState.interruptionMillis + interruptionMillis,
                            interruptionStartMillis = null
                        )
                    )
                } else if (SleepDetectionAnalyzer.shouldStartSession(nowMillis)) {
                    sessionStore.save(
                        SleepDetectionSessionState(
                            startMillis = nowMillis,
                            alarmMillis = nextAlarmMillis(),
                            interruptionMillis = 0L,
                            interruptionStartMillis = null
                        )
                    )
                } else {
                    sessionStore.clear()
                }

                return@launch
            }

            if (existingState == null && SleepDetectionAnalyzer.shouldStartSession(nowMillis)) {
                sessionStore.save(
                    SleepDetectionSessionState(
                        startMillis = nowMillis,
                        alarmMillis = nextAlarmMillis(),
                        interruptionMillis = 0L,
                        interruptionStartMillis = null
                    )
                )
            }
        }
    }

    private fun handleScreenActive() {
        serviceScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val settings = settingsDataSource.getSleepDetectionSettings(userId)
            if (!settings.enabled) return@launch

            val state = sessionStore.load() ?: return@launch
            if (state.interruptionStartMillis != null) return@launch

            val nowMillis = System.currentTimeMillis()
            val alarmMillis = state.alarmMillis ?: nextAlarmMillis()
            val candidate = SleepDetectionAnalyzer.buildCandidate(
                userId = userId,
                startMillis = state.startMillis,
                endMillis = nowMillis,
                alarmMillis = alarmMillis,
                interruptionMillis = state.interruptionMillis,
                settings = settings,
                nowMillis = nowMillis
            )

            if (candidate == null) {
                if (nowMillis - state.startMillis > MAX_SESSION_MILLIS) {
                    sessionStore.clear()
                } else {
                    sessionStore.save(
                        state.copy(
                            alarmMillis = alarmMillis,
                            interruptionStartMillis = nowMillis
                        )
                    )
                }
                return@launch
            }

            val wakeDateStartMillis = SleepDetectionAnalyzer.startOfDayMillis(
                candidate.wakeDateMillis
            )
            val wakeDateEndMillis = SleepDetectionAnalyzer.endOfDayMillis(
                candidate.wakeDateMillis
            )
            val hasExistingLog = sleepRepository.hasSleepLogForWakeDate(
                userId = userId,
                wakeDateStartMillis = wakeDateStartMillis,
                wakeDateEndMillis = wakeDateEndMillis
            )
            val hasExistingCandidate = detectionRepository.hasCandidateForWakeDate(
                userId = userId,
                wakeDateStartMillis = wakeDateStartMillis,
                wakeDateEndMillis = wakeDateEndMillis
            )

            if (!hasExistingLog && !hasExistingCandidate) {
                detectionRepository.upsertCandidate(candidate)
            }

            sessionStore.clear()
        }
    }

    private fun nextAlarmMillis(): Long? {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.nextAlarmClock?.triggerTime
    }

    private fun registerScreenReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(screenReceiver, filter)
        }
    }

    private fun createNotification(): android.app.Notification {
        createNotificationChannel()

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Sleep detection is on")
            .setContentText("Watching screen and alarm signals for sleep suggestions.")
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sleep detection",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "sleep_detection"
        private const val NOTIFICATION_ID = 4207
        private const val ACTION_STOP = "com.example.frontpage.sleep.action.STOP_DETECTION"
        private const val MILLIS_PER_MINUTE = 60_000L
        private const val MAX_SESSION_MILLIS = 18 * 60 * MILLIS_PER_MINUTE

        fun setEnabled(
            context: Context,
            enabled: Boolean
        ) {
            val appContext = context.applicationContext
            if (enabled) {
                ContextCompat.startForegroundService(
                    appContext,
                    Intent(appContext, SleepDetectionService::class.java)
                )
            } else {
                SleepDetectionSessionStore(appContext).clear()
                appContext.startService(
                    Intent(appContext, SleepDetectionService::class.java).apply {
                        action = ACTION_STOP
                    }
                )
            }
        }
    }
}
