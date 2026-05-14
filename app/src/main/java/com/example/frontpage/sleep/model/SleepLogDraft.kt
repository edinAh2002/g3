package com.example.frontpage.sleep.model

data class SleepLogDraft(
    val sleepHour: Int,
    val sleepMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val wakeDateMillis: Long,
    val quality: SleepQuality,
    val durationMinutes: Int,
    val notes: String,
    val dreamJournal: String,
    val snoringLevel: SnoringLevel,
    val tags: String,
    val source: SleepSource = SleepSource.Manual
) {
    fun toNewEntry(
        id: Long,
        date: String
    ): SleepEntry {
        return SleepEntry(
            id = id,
            date = date,
            sleepHour = sleepHour,
            sleepMinute = sleepMinute,
            wakeHour = wakeHour,
            wakeMinute = wakeMinute,
            durationMinutes = durationMinutes,
            quality = quality,
            notes = notes,
            dateMillis = wakeDateMillis,
            dreamJournal = dreamJournal,
            snoringLevel = snoringLevel,
            tags = tags,
            source = source
        )
    }

    fun applyTo(
        entry: SleepEntry,
        date: String
    ): SleepEntry {
        return entry.copy(
            date = date,
            sleepHour = sleepHour,
            sleepMinute = sleepMinute,
            wakeHour = wakeHour,
            wakeMinute = wakeMinute,
            durationMinutes = durationMinutes,
            quality = quality,
            notes = notes,
            dateMillis = wakeDateMillis,
            dreamJournal = dreamJournal,
            snoringLevel = snoringLevel,
            tags = tags,
            source = source
        )
    }
}
