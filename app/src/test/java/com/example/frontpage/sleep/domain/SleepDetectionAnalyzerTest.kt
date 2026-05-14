package com.example.frontpage.sleep.domain

import com.example.frontpage.sleep.model.SleepDetectionSettings
import com.example.frontpage.sleep.model.SleepSource
import com.example.frontpage.sleep.model.toSleepLogDraft
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SleepDetectionAnalyzerTest {

    @Test
    fun longScreenOffWithMatchingAlarmCreatesCandidate() {
        val startMillis = millisFor(hour = 23, minute = 0)
        val endMillis = millisFor(hour = 7, minute = 0, dayOffset = 1)
        val alarmMillis = millisFor(hour = 7, minute = 15, dayOffset = 1)

        val candidate = SleepDetectionAnalyzer.buildCandidate(
            userId = 12L,
            startMillis = startMillis,
            endMillis = endMillis,
            alarmMillis = alarmMillis,
            interruptionMillis = 0L,
            settings = SleepDetectionSettings(),
            nowMillis = endMillis
        )

        assertNotNull(candidate)
        assertEquals(12L, candidate?.userId)
        assertTrue(candidate?.confidence ?: 0 >= 80)
    }

    @Test
    fun shortScreenOffDoesNotCreateCandidate() {
        val startMillis = millisFor(hour = 23, minute = 0)
        val endMillis = millisFor(hour = 23, minute = 45)
        val alarmMillis = millisFor(hour = 7, minute = 0, dayOffset = 1)

        val candidate = SleepDetectionAnalyzer.buildCandidate(
            userId = 12L,
            startMillis = startMillis,
            endMillis = endMillis,
            alarmMillis = alarmMillis,
            interruptionMillis = 0L,
            settings = SleepDetectionSettings(),
            nowMillis = endMillis
        )

        assertNull(candidate)
    }

    @Test
    fun missingAlarmDoesNotCreateCandidate() {
        val startMillis = millisFor(hour = 23, minute = 0)
        val endMillis = millisFor(hour = 7, minute = 0, dayOffset = 1)

        val candidate = SleepDetectionAnalyzer.buildCandidate(
            userId = 12L,
            startMillis = startMillis,
            endMillis = endMillis,
            alarmMillis = null,
            interruptionMillis = 0L,
            settings = SleepDetectionSettings(),
            nowMillis = endMillis
        )

        assertNull(candidate)
    }

    @Test
    fun briefInterruptionsAreIncludedInSummary() {
        val startMillis = millisFor(hour = 23, minute = 0)
        val endMillis = millisFor(hour = 7, minute = 0, dayOffset = 1)
        val alarmMillis = millisFor(hour = 7, minute = 5, dayOffset = 1)

        val candidate = SleepDetectionAnalyzer.buildCandidate(
            userId = 12L,
            startMillis = startMillis,
            endMillis = endMillis,
            alarmMillis = alarmMillis,
            interruptionMillis = 10 * 60_000L,
            settings = SleepDetectionSettings(),
            nowMillis = endMillis
        )

        assertNotNull(candidate)
        assertTrue(candidate?.signalSummary.orEmpty().contains("Brief screen use"))
    }

    @Test
    fun candidateConvertsToDetectedSleepDraft() {
        val startMillis = millisFor(hour = 23, minute = 30)
        val endMillis = millisFor(hour = 7, minute = 15, dayOffset = 1)
        val alarmMillis = millisFor(hour = 7, minute = 10, dayOffset = 1)

        val candidate = SleepDetectionAnalyzer.buildCandidate(
            userId = 12L,
            startMillis = startMillis,
            endMillis = endMillis,
            alarmMillis = alarmMillis,
            interruptionMillis = 0L,
            settings = SleepDetectionSettings(),
            nowMillis = endMillis
        )

        val draft = requireNotNull(candidate).toSleepLogDraft()

        assertEquals(SleepSource.Detected, draft.source)
        assertEquals(23, draft.sleepHour)
        assertEquals(30, draft.sleepMinute)
        assertEquals(7, draft.wakeHour)
        assertEquals(15, draft.wakeMinute)
    }

    private fun millisFor(
        hour: Int,
        minute: Int,
        dayOffset: Int = 0
    ): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 10)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }.timeInMillis
    }
}
