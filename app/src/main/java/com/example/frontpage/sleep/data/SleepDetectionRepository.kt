package com.example.frontpage.sleep.data

import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepDetectionStatus
import kotlinx.coroutines.flow.Flow

interface SleepDetectionDataSource {
    fun observePendingCandidatesForUser(userId: Long): Flow<List<SleepDetectionCandidate>>

    suspend fun hasCandidateForWakeDate(
        userId: Long,
        wakeDateStartMillis: Long,
        wakeDateEndMillis: Long
    ): Boolean

    suspend fun upsertCandidate(candidate: SleepDetectionCandidate)

    suspend fun acceptCandidate(
        userId: Long,
        candidateId: Long
    )

    suspend fun dismissCandidate(
        userId: Long,
        candidateId: Long
    )
}

class SleepDetectionRepository(
    private val sleepDetectionDao: SleepDetectionDao
) : SleepDetectionDataSource {

    override fun observePendingCandidatesForUser(userId: Long): Flow<List<SleepDetectionCandidate>> {
        return sleepDetectionDao.observeCandidatesForUserByStatus(
            userId = userId,
            status = SleepDetectionStatus.Pending
        )
    }

    override suspend fun hasCandidateForWakeDate(
        userId: Long,
        wakeDateStartMillis: Long,
        wakeDateEndMillis: Long
    ): Boolean {
        return sleepDetectionDao.countCandidatesForWakeDate(
            userId = userId,
            wakeDateStartMillis = wakeDateStartMillis,
            wakeDateEndMillis = wakeDateEndMillis
        ) > 0
    }

    override suspend fun upsertCandidate(candidate: SleepDetectionCandidate) {
        sleepDetectionDao.upsertCandidate(candidate)
    }

    override suspend fun acceptCandidate(
        userId: Long,
        candidateId: Long
    ) {
        sleepDetectionDao.updateCandidateStatus(
            userId = userId,
            candidateId = candidateId,
            status = SleepDetectionStatus.Accepted,
            updatedAtMillis = System.currentTimeMillis()
        )
    }

    override suspend fun dismissCandidate(
        userId: Long,
        candidateId: Long
    ) {
        sleepDetectionDao.updateCandidateStatus(
            userId = userId,
            candidateId = candidateId,
            status = SleepDetectionStatus.Dismissed,
            updatedAtMillis = System.currentTimeMillis()
        )
    }
}
