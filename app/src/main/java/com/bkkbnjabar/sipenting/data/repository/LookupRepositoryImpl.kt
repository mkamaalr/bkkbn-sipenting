package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.db.AppDatabase
import com.bkkbnjabar.sipenting.data.local.entity.LookupItemEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toDomain
import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemDto
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toEntity
import com.bkkbnjabar.sipenting.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookupRepositoryImpl @Inject constructor(
    private val lookupApiService: LookupApiService,
    private val lookupDao: LookupDao,
    private val database: AppDatabase // <-- ADD AppDatabase to the constructor
) : LookupRepository {

    /**
     * Preloads all lookup data from a single API endpoint.
     * It first checks if data already exists. If so, it skips the preload.
     * All database write operations are performed in a single transaction for data integrity.
     */
    override suspend fun preloadAllLookupData() {
        // Check if data already exists to avoid unnecessary preloads.
        if (lookupDao.getProvinsiCount() > 0) {
            Log.d("LookupRepo", "Lookup data already exists. Skipping preload.")
            return
        }

        Log.d("LookupRepo", "Database is empty. Starting API call for all lookups...")
        try {
            val response = lookupApiService.getAllLookups()

            if (response.isSuccessful && response.body()?.data != null) {
                val allData = response.body()!!.data!!

                // Perform all database writes in a single transaction.
                // If any operation fails, all previous operations in this block will be rolled back.
                database.withTransaction  {
                    // Clear old data first
                    lookupDao.clearAllProvinsis()
                    lookupDao.clearAllKabupatens()
                    lookupDao.clearAllKecamatans()
                    lookupDao.clearAllKelurahans()
                    lookupDao.clearAllRws()
                    lookupDao.clearAllRts()
                    lookupDao.clearAllLookupItems()

                    // 1. Save Location Data
                    allData.provinsis?.let { lookupDao.insertAllProvinsis(it.map { dto -> dto.toEntity() }) }
                    allData.kabupatens?.let { lookupDao.insertAllKabupatens(it.map { dto -> dto.toEntity() }) }
                    allData.kecamatans?.let { lookupDao.insertAllKecamatans(it.map { dto -> dto.toEntity() }) }
                    allData.kelurahans?.let { lookupDao.insertAllKelurahans(it.map { dto -> dto.toEntity() }) }
                    allData.rws?.let { lookupDao.insertAllRws(it.map { dto -> dto.toEntity() }) }
                    allData.rts?.let { lookupDao.insertAllRts(it.map { dto -> dto.toEntity() }) }

                    // 2. Prepare all lookup options in a map for efficient processing.
                    val lookupsToSave = mutableMapOf<String, List<LookupItemDto>?>(
                        "birth-assistants" to allData.birthAssistants,
                        "counseling-types" to allData.counselingTypes,
                        "defecation-facilities" to allData.defecationFacilities,
                        "delivery-places" to allData.deliveryPlaces,
                        "disease-histories" to allData.diseaseHistories,
                        "given-birth-statuses" to allData.givenBirthStatuses,
                        "immunization-options" to allData.immunizationOptions,
                        "main-sources-of-drinking-water" to allData.mainSourcesOfDrinkingWater,
                        "postpartum-complication-options" to allData.postpartumComplicationOptions,
                        "pregnant-mother-statuses" to allData.pregnantMotherStatuses,
                        "social-assistance-facilitation-options" to allData.socialAssistanceFacilitationOptions
                    )

                    // 3. Split contraception options into two separate lists.
                    allData.contraceptionOptions?.let { options ->
                        lookupsToSave["contraception-options"] = options.filter { it.id in 1..9 }
                        lookupsToSave["contraception-options-reject"] = options.filter { it.id in 10..22 }
                    }

                    // 4. Iterate over the map and save each lookup list to the database.
                    for ((type, items) in lookupsToSave) {
                        saveLookupItems(type, items)
                    }
                }
                Log.d("LookupRepo", "Successfully preloaded all data from the API.")
            } else {
                // Throw an exception if the API call was not successful.
                throw Exception("API Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("LookupRepo", "Failed to preload lookup data: ${e.message}", e)
            // Optionally re-throw or handle the exception as needed.
        }
    }

    /**
     * A private helper to map a list of DTOs to Entities and insert them into the database.
     */
    private suspend fun saveLookupItems(type: String, items: List<LookupItemDto>?) {
        if (items.isNullOrEmpty()) return

        val entities = items.map { dto ->
            LookupItemEntity(
                serverId = dto.id,
                name = dto.name,
                type = type, // Assign the correct type here
                isRisky = dto.isRisky
            )
        }
        lookupDao.insertLookupItems(entities)
    }

    // --- Data Access Functions ---

    override fun getAllProvinsisFromRoom(): Flow<List<Provinsi>> = lookupDao.getAllProvinsis().map { it.toDomain() }
    override fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>> = lookupDao.getAllKabupatens().map { it.toDomain() }
    override fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>> = lookupDao.getKecamatansByKabupaten(kabupatenId).map { it.toDomain() }
    override fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>> = lookupDao.getKelurahansByKecamatan(kecamatanId).map { it.toDomain() }
    override fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>> = lookupDao.getRwsByKelurahan(kelurahanId).map { it.toDomain() }
    override fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>> = lookupDao.getRtsByRw(rwId).map { it.toDomain() }

    /**
     * A single, generic function to get any type of lookup options from the local database.
     * This is now the single source of truth for all dropdown/chip options.
     */
    override fun getLookupOptions(type: String): Flow<List<LookupItem>> {
        return lookupDao.getLookupItemsByType(type).map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getLocationDetailsByKelurahanId(kelurahanId: Int): LocationDetails? {
        val kelurahan = lookupDao.getKelurahanById(kelurahanId) ?: return null
        val kecamatan = kelurahan.kecamatanId?.let { lookupDao.getKecamatanById(it) } ?: return null
        val kabupaten = kecamatan.kabupatenId?.let { lookupDao.getKabupatenById(it) } ?: return null
        val provinsi = kabupaten.provinsiId?.let { lookupDao.getProvinsiById(it) } ?: return null

        return LocationDetails(
            provinsiId = provinsi.id.toString(),
            provinsiName = provinsi.name,
            kabupatenId = kabupaten.id.toString(),
            kabupatenName = kabupaten.name,
            kecamatanId = kecamatan.id.toString(),
            kecamatanName = kecamatan.name,
            kelurahanId = kelurahan.id.toString(),
            kelurahanName = kelurahan.name
        )
    }

    override suspend fun uploadPendingData() {
        // TODO: Implement logic to get pending mothers and visits from DAO and upload to API
    }

    override suspend fun syncFromServer() {
        // TODO: Implement logic to fetch data from API and insert/update local database
    }
}