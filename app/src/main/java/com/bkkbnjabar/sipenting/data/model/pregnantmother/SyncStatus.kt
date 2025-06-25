package com.bkkbnjabar.sipenting.data.model.pregnantmother

/**
 * Enum to represent the synchronization status of local data with the remote server.
 */
enum class SyncStatus {
    /**
     * Data has been created locally but not yet pushed to the server.
     */
    PENDING,

    /**
     * Data has been successfully synchronized with the server.
     */
    DONE,

    /**
     * An error occurred during the synchronization attempt.
     */
    ERROR
}