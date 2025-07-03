package com.bkkbnjabar.sipenting.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bkkbnjabar.sipenting.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val UNIQUE_WORK_NAME = "app-sync"
    }

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker: Starting two-way data synchronization...")
        return try {
            syncRepository.uploadPendingData()
            syncRepository.downloadAllData()
            Timber.d("SyncWorker: Synchronization finished successfully.")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker: Synchronization failed.")
            Result.retry()
        }
    }
}