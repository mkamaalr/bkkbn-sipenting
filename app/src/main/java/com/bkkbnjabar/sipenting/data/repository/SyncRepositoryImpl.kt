package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.domain.repository.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val pregnantMotherRepository: PregnantMotherRepository,
    private val breastfeedingMotherRepository: BreastfeedingMotherRepository,
    private val childRepository: ChildRepository,
    private val childMotherRepository: ChildMotherRepository,
    private val lookupRepository: LookupRepository
) : SyncRepository {

    override suspend fun uploadPendingData() {
        // coroutineScope allows us to launch multiple async tasks and wait for all of them
        // to complete. If one fails, the whole scope is cancelled.
        coroutineScope {
            // Create a list of deferred tasks for each upload operation
            val uploadTasks = listOf(
                async { pregnantMotherRepository.uploadPendingData() },
                async { breastfeedingMotherRepository.uploadPendingData() },
                async { childRepository.uploadPendingData() },
                async { childMotherRepository.uploadPendingData() }
                // Add other upload tasks here
            )
            // awaitAll() will suspend until all async tasks are complete
            uploadTasks.awaitAll()
        }
    }

    override suspend fun downloadAllData() {
        coroutineScope {
            // Create a list of deferred tasks for each download/sync operation
            val downloadTasks = listOf(
                async { lookupRepository.preloadAllLookupData() },
                async { pregnantMotherRepository.syncFromServer() },
                async { breastfeedingMotherRepository.syncFromServer() },
                async { childRepository.syncFromServer() },
                async { childMotherRepository.syncFromServer() }
                // Add other download/sync tasks here
            )
            // awaitAll() will suspend until all async tasks are complete
            downloadTasks.awaitAll()
        }
    }
}
