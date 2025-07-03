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
        coroutineScope {
            listOf(
                async { pregnantMotherRepository.uploadPendingData() },
//                async { breastfeedingMotherRepository.uploadPendingData() },
//                async { childRepository.uploadPendingData() },
//                async { childMotherRepository.uploadPendingData() }
            ).forEach { it.await() }
        }
    }

    override suspend fun downloadAllData() {
        coroutineScope {
            listOf(
                async { lookupRepository.preloadAllLookupData() },
                async { pregnantMotherRepository.syncFromServer() },
//                async { breastfeedingMotherRepository.syncFromServer() },
//                async { childRepository.syncFromServer() },
//                async { childMotherRepository.syncFromServer() }
            ).forEach { it.await() }
        }
    }
}