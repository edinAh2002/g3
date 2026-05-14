package com.example.frontpage.sleep.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.frontpage.sleep.model.SleepDetectionCandidate
import com.example.frontpage.sleep.model.SleepDetectionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDetectionDao {

    @Query(
        """
        SELECT * FROM sleep_detection_candidates
        WHERE userId = :userId AND status = :status
        ORDER BY endMillis DESC
        """
    )
    fun observeCandidatesForUserByStatus(
        userId: Long,
        status: SleepDetectionStatus = SleepDetectionStatus.Pending
    ): Flow<List<SleepDetectionCandidate>>

    @Query(
        """
        SELECT COUNT(*) FROM sleep_detection_candidates
        WHERE userId = :userId
        AND wakeDateMillis BETWEEN :wakeDateStartMillis AND :wakeDateEndMillis
        """
    )
    suspend fun countCandidatesForWakeDate(
        userId: Long,
        wakeDateStartMillis: Long,
        wakeDateEndMillis: Long
    ): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCandidate(candidate: SleepDetectionCandidate)

    @Query(
        """
        UPDATE sleep_detection_candidates
        SET status = :status, updatedAtMillis = :updatedAtMillis
        WHERE userId = :userId AND id = :candidateId
        """
    )
    suspend fun updateCandidateStatus(
        userId: Long,
        candidateId: Long,
        status: SleepDetectionStatus,
        updatedAtMillis: Long
    )
}
