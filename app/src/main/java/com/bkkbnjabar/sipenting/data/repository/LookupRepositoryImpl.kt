package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.entity.LookupItemEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toDomain
import com.bkkbnjabar.sipenting.data.local.mapper.toEntity
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemDto
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toEntity
import com.bkkbnjabar.sipenting.domain.model.*
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookupRepositoryImpl @Inject constructor(
    private val lookupApiService: LookupApiService,
    private val lookupDao: LookupDao,
    private val sharedPrefsManager: SharedPrefsManager
) : LookupRepository {

    /**
     * Memeriksa database lokal terlebih dahulu. Jika kosong, baru mengunduh semua data lookup
     * dari API dan menyimpannya. Jika sudah ada, proses unduh akan dilewati.
     */
    override suspend fun preloadAllLookupData() {
        try {
            // ================== LOGIKA PENGECEKAN DATA ==================
            if (lookupDao.getProvinsiCount() > 0) {
                Log.d("LookupRepo", "Data lookup sudah ada. Melewatkan preload.")
                return
            }
            // ==========================================================
            Log.d("LookupRepo", "Database kosong. Memulai satu panggilan API untuk semua lookup...")
            val response = lookupApiService.getAllLookups()
            if (response.isSuccessful && response.body()?.data != null) {
                val allData = response.body()!!.data!!

                // Simpan semua data lokasi secara berurutan
                allData.provinsis?.let { lookupDao.insertAllProvinsis(it.map { dto -> dto.toEntity() }) }
                allData.kabupatens?.let { lookupDao.insertAllKabupatens(it.map { dto -> dto.toEntity() }) }
                allData.kecamatans?.let { lookupDao.insertAllKecamatans(it.map { dto -> dto.toEntity() }) }
                allData.kelurahans?.let { lookupDao.insertAllKelurahans(it.map { dto -> dto.toEntity() }) }
                allData.rws?.let { lookupDao.insertAllRws(it.map { dto -> dto.toEntity() }) }
                allData.rts?.let { lookupDao.insertAllRts(it.map { dto -> dto.toEntity() }) }

                // Simpan semua data opsi dropdown
                saveLookupItemsToDb("birth-assistants", allData.birthAssistants)
                saveLookupItemsToDb("contraception-options", allData.contraceptionOptions)
                saveLookupItemsToDb("counseling-types", allData.counselingTypes)
                saveLookupItemsToDb("defecation-facilities", allData.defecationFacilities)
                saveLookupItemsToDb("delivery-places", allData.deliveryPlaces)
                saveLookupItemsToDb("disease-histories", allData.diseaseHistories)
                saveLookupItemsToDb("given-birth-statuses", allData.givenBirthStatuses)
                saveLookupItemsToDb("immunization-options", allData.immunizationOptions)
                saveLookupItemsToDb("main-sources-of-drinking-water", allData.mainSourcesOfDrinkingWater)
                saveLookupItemsToDb("postpartum-complication-options", allData.postpartumComplicationOptions)
                saveLookupItemsToDb("pregnant-mother-statuses", allData.pregnantMotherStatuses)
                saveLookupItemsToDb("social-assistance-facilitation-options", allData.socialAssistanceFacilitationOptions)

                Log.d("LookupRepo", "Proses preload semua data dari satu API berhasil.")
            } else {
                Log.e("LookupRepo", "Gagal mendapatkan data lookup: ${response.code()} - ${response.message()}")
            }
//            Log.d("LookupRepo", "Database lookup kosong. Memulai proses preload dari API...")
//
//            // Urutan penyimpanan SANGAT PENTING di sini.
//            // ... (logika penyimpanan berurutan tetap sama seperti sebelumnya) ...
//            val provinsiResponse = lookupApiService.getProvinsis()
//            if (provinsiResponse.isSuccessful) {
//                val entities = provinsiResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllProvinsis(entities)
//            }
//            val kabupatenResponse = lookupApiService.getKabupatens()
//            if (kabupatenResponse.isSuccessful) {
//                val entities = kabupatenResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllKabupatens(entities)
//            }
//            val kecamatanResponse = lookupApiService.getKecamatans()
//            if (kecamatanResponse.isSuccessful) {
//                val entities = kecamatanResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllKecamatans(entities)
//            }
//            val kelurahanResponse = lookupApiService.getKelurahans()
//            if (kelurahanResponse.isSuccessful) {
//                val entities = kelurahanResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllKelurahans(entities)
//            }
//            val rwResponse = lookupApiService.getRws()
//            if (rwResponse.isSuccessful) {
//                val entities = rwResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllRws(entities)
//            }
//            val rtResponse = lookupApiService.getRts()
//            if (rtResponse.isSuccessful) {
//                val entities = rtResponse.body()?.data?.map { it.toEntity() } ?: emptyList()
//                lookupDao.insertAllRts(entities)
//            }
// ================== LOGIKA BARU UNTUK MENYIMPAN LOOKUP KE DB ==================
//            // Daftar semua tipe lookup yang akan diunduh
//            val lookupTypes = listOf(
//                "birth-assistants", "contraception-options", "counseling-types",
//                "defecation-facilities", "delivery-places", "disease-histories",
//                "given-birth-statuses", "immunization-options", "main-sources-of-drinking-water",
//                "postpartum-complication-options", "pregnant-mother-statuses",
//                "social-assistance-facilitation-options"
//            )
//
//            // Loop untuk mengambil dan menyimpan setiap tipe lookup
//            for (type in lookupTypes) {
//                fetchAndSaveLookupItemsToDb(type)
//            }
//            fetchAndSaveLookupOptions("disease-histories", SharedPrefsManager.KEY_DISEASE_HISTORY_OPTIONS)
//            fetchAndSaveLookupOptions("main-sources-of-drinking-water", SharedPrefsManager.KEY_DRINKING_WATER_OPTIONS)
//            fetchAndSaveLookupOptions("defecation-facilities", SharedPrefsManager.KEY_DEFECATION_FACILITY_OPTIONS)
//            fetchAndSaveLookupOptions("social-assistance-facilitation-options", SharedPrefsManager.KEY_SOCIAL_ASSISTANCE_OPTIONS)
            Log.d("LookupRepo", "Proses preload selesai.")

        } catch (e: Exception) {
            Log.e("LookupRepo", "Gagal memuat data lookup: ${e.message}", e)
        }
    }

    private suspend fun saveLookupItemsToDb(type: String, items: List<LookupItemDto>?) {
        if (items.isNullOrEmpty()) return

        val entities = items.map { dto ->
            LookupItemEntity(
                serverId = dto.id,
                name = dto.name,
                type = type
            )
        }
        lookupDao.insertLookupItems(entities)
    }

    private suspend fun fetchAndSaveLookupItemsToDb(lookupType: String) {
        try {
            val response = lookupApiService.getLookupItem(lookupType)
            if (response.isSuccessful) {
                val lookupItems = response.body()?.data?.map { dto ->
                    LookupItemEntity(
                        serverId = dto.id, // Asumsikan DTO punya 'id' dari server
                        name = dto.name,
                        type = lookupType
                    )
                } ?: emptyList()

                if (lookupItems.isNotEmpty()) {
                    lookupDao.insertLookupItems(lookupItems)
                    Log.d("LookupRepo", "Sukses menyimpan ${lookupItems.size} item untuk tipe: $lookupType")
                }
            }
        } catch (e: Exception) {
            Log.e("LookupRepo", "Gagal mengambil opsi untuk $lookupType: ${e.message}")
        }
    }

    private suspend fun fetchAndSaveLookupOptions(lookupType: String, prefsKey: String) {
        try {
            val response = lookupApiService.getLookupItem(lookupType)
            if (response.isSuccessful) {
                val options = response.body()?.data?.map { it.name } ?: emptyList()
                sharedPrefsManager.saveStringList(prefsKey, options)
            }
        } catch (e: Exception) {
            Log.e("LookupRepo", "Gagal mengambil opsi untuk $lookupType: ${e.message}")
        }
    }

    // --- (Sisa fungsi lainnya tidak berubah) ---
    override fun getAllProvinsisFromRoom(): Flow<List<Provinsi>> = lookupDao.getAllProvinsis().map { it.toDomain() }
    override fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>> = lookupDao.getAllKabupatens().map { it.toDomain() }
    override fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>> = lookupDao.getKecamatansByKabupaten(kabupatenId).map { it.toDomain() }
    override fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>> = lookupDao.getKelurahansByKecamatan(kecamatanId).map { it.toDomain() }
    override fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>> = lookupDao.getRwsByKelurahan(kelurahanId).map { it.toDomain() }
    override fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>> = lookupDao.getRtsByRw(rwId).map { it.toDomain() }

    // ================== FUNGSI BARU UNTUK MENGAMBIL LOOKUP DARI DB ==================
    override fun getLookupOptions(type: String): Flow<List<LookupItem>> {
        return lookupDao.getLookupItemsByType(type).map { list ->
            list.map { entity ->
                // Mapping sederhana dari Entity ke Domain model
                LookupItem(id = entity.serverId, name = entity.name)
            }
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

    override fun getDiseaseHistoryOptions(): Flow<List<String>> = flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DISEASE_HISTORY_OPTIONS)) }
    override fun getMainSourceOfDrinkingWaterOptions(): Flow<List<String>> = flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DRINKING_WATER_OPTIONS)) }
    override fun getDefecationFacilityOptions(): Flow<List<String>> = flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DEFECATION_FACILITY_OPTIONS)) }
    override fun getSocialAssistanceFacilitationOptions(): Flow<List<String>> = flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_SOCIAL_ASSISTANCE_OPTIONS)) }
}
