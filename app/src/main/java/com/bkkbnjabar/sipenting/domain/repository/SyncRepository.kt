package com.bkkbnjabar.sipenting.domain.repository

/**
 * A centralized repository interface for managing data synchronization.
 */
interface SyncRepository {

    /**
     * Finds all local data with a 'PENDING' sync status across all modules
     * and attempts to upload it to the server.
     * @throws Exception if any of the uploads fail.
     */
    suspend fun uploadPendingData()

    /**
     * Fetches all data from the server for all modules, clearing old
     * local data to ensure a fresh and consistent state.
     * @throws Exception if any of the downloads fail.
     */
    suspend fun downloadAllData()
}
