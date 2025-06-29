package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the repository that handles all lookup data
 * (locations, dropdown options, etc.).
 * This interface now uses Room as the single source of truth for all options.
 */
interface LookupRepository {
    /**
     * Checks if local data exists. If not, fetches all lookup data from the
     * remote API and stores it in the local Room database.
     */
    suspend fun preloadAllLookupData()

    // --- Functions for Location Data ---

    fun getAllProvinsisFromRoom(): Flow<List<Provinsi>>
    fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>>
    fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>>
    fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>>
    fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>>
    fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>>
    suspend fun getLocationDetailsByKelurahanId(kelurahanId: Int): LocationDetails?

    // --- A single, powerful function to get all other lookup options ---

    /**
     * Gets a list of lookup items for a specific type from the local database.
     * This is now the ONLY function needed to get options for dropdowns and chip groups.
     *
     * Example Usage in ViewModel:
     * - getLookupOptions("disease-histories")
     * - getLookupOptions("contraception-options")
     * - getLookupOptions("contraception-options-reject")
     *
     * @param type The unique identifier for the list of options (e.g., "disease-histories").
     * @return A Flow emitting a list of LookupItem.
     */
    fun getLookupOptions(type: String): Flow<List<LookupItem>>
}