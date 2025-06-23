package com.bkkbnjabar.sipenting.data.repository

import android.util.Log
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.mapper.toKabupaten
import com.bkkbnjabar.sipenting.data.local.mapper.toKabupatenEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toKecamatan
import com.bkkbnjabar.sipenting.data.local.mapper.toKecamatanEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toKelurahan
import com.bkkbnjabar.sipenting.data.local.mapper.toKelurahanEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toProvinsi
import com.bkkbnjabar.sipenting.data.local.mapper.toProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toRt
import com.bkkbnjabar.sipenting.data.local.mapper.toRtEntity
import com.bkkbnjabar.sipenting.data.local.mapper.toRw
import com.bkkbnjabar.sipenting.data.local.mapper.toRwEntity
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.data.remote.mapper.toKabupaten
import com.bkkbnjabar.sipenting.data.remote.mapper.toKecamatan
import com.bkkbnjabar.sipenting.data.remote.mapper.toKelurahan
import com.bkkbnjabar.sipenting.data.remote.mapper.toProvinsi
import com.bkkbnjabar.sipenting.data.remote.mapper.toRt
import com.bkkbnjabar.sipenting.data.remote.mapper.toRw
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.LocationDetails
import com.bkkbnjabar.sipenting.domain.model.LookupItemDto
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.utils.Resource
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.awaitAll

@Singleton
class LookupRepositoryImpl @Inject constructor(
    private val apiService: LookupApiService,
    private val lookupDao: LookupDao,
    private val sharedPrefsManager: SharedPrefsManager
) : LookupRepository {

    // --- API Fetch Methods (Mengembalikan Domain Model jika ada mapper, atau DTO jika langsung) ---

    override suspend fun getProvinsisFromApi(): Resource<List<Provinsi>> {
        return try {
            val response = apiService.getProvinsis()
            if (response.isSuccessful) {
                // Menggunakan mapper dari data/remote/mapper
                val provinsisDto = response.body()?.data ?: emptyList()
                val provinsis = provinsisDto.map { it.toProvinsi() }
                Resource.Success(provinsis)
            } else {
                Resource.Error("Failed to fetch provinces from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching provinces from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getKabupatensFromApi(provinsiId: Int?): Resource<List<Kabupaten>> {
        return try {
            val response = apiService.getKabupatens(provinsiId)
            if (response.isSuccessful) {
                val kabupatensDto = response.body()?.data ?: emptyList()
                val kabupatens = kabupatensDto.map { it.toKabupaten() }
                Resource.Success(kabupatens)
            } else {
                Resource.Error("Failed to fetch kabupatens from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching kabupatens from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getKecamatansFromApi(kabupatenId: Int?): Resource<List<Kecamatan>> {
        return try {
            val response = apiService.getKecamatans(kabupatenId)
            if (response.isSuccessful) {
                val kecamatansDto = response.body()?.data ?: emptyList()
                val kecamatans = kecamatansDto.map { it.toKecamatan() }
                Resource.Success(kecamatans)
            } else {
                Resource.Error("Failed to fetch kecamatans from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching kecamatans from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getKelurahansFromApi(kecamatanId: Int?): Resource<List<Kelurahan>> {
        return try {
            val response = apiService.getKelurahans(kecamatanId)
            if (response.isSuccessful) {
                val kelurahansDto = response.body()?.data ?: emptyList()
                val kelurahans = kelurahansDto.map { it.toKelurahan() }
                Resource.Success(kelurahans)
            } else {
                Resource.Error("Failed to fetch kelurahans from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching kelurahans from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getRWSFromApi(kelurahanId: Int?): Resource<List<Rw>> {
        return try {
            val response = apiService.getRWS(kelurahanId)
            if (response.isSuccessful) {
                val rwsDto = response.body()?.data ?: emptyList()
                val rws = rwsDto.map { it.toRw() }
                Resource.Success(rws)
            } else {
                Resource.Error("Failed to fetch RWs from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching RWs from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getRTSFromApi(rwId: Int?): Resource<List<Rt>> {
        return try {
            val response = apiService.getRTS(rwId)
            if (response.isSuccessful) {
                val rtsDto = response.body()?.data ?: emptyList()
                val rts = rtsDto.map { it.toRt() }
                Resource.Success(rts)
            } else {
                Resource.Error("Failed to fetch RTs from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching RTs from API: ${e.localizedMessage}")
        }
    }

    // NEW: Metode getUserLocationData() yang lama dihapus karena user location sekarang diambil dari SharedPrefs/Room
    // (Melalui kelurahanId di UserSession dan LookupRepository.getLocationDetailsByKelurahanId)

    override suspend fun getBirthAssistantsFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getBirthAssistants()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch birth assistants from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching birth assistants from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getContraceptionOptionsFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getContraceptionOptions()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch contraception options from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching contraception options from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getCounselingTypesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getCounselingTypes()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch counseling types from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching counseling types from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getDefecationFacilitiesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getDefecationFacilities()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch defecation facilities from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching defecation facilities from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getDeliveryPlacesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getDeliveryPlaces()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch delivery places from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching delivery places from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getDiseaseHistoriesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getDiseaseHistories()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch disease histories from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching disease histories from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getGivenBirthStatusesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getGivenBirthStatuses()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch given birth statuses from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching given birth statuses from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getImmunizationOptionsFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getImmunizationOptions()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch immunization options from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching immunization options from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getMainSourceOfDrinkingWatersFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getMainSourceOfDrinkingWaters()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch main sources of drinking water from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching main sources of drinking water from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getPostpartumComplicationOptionsFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getPostpartumComplicationOptions()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch postpartum complication options from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching postpartum complication options from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getPregnantMotherStatusesFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getPregnantMotherStatuses()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch pregnant mother statuses from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching pregnant mother statuses from API: ${e.localizedMessage}")
        }
    }

    override suspend fun getSocialAssistanceFacilitationOptionsFromApi(): Resource<List<LookupItemDto>> {
        return try {
            val response = apiService.getSocialAssistanceFacilitationOptions()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch social assistance facilitation options from API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error fetching social assistance facilitation options from API: ${e.localizedMessage}")
        }
    }


    // --- Room Data Methods (Local persistence operations) ---
    override suspend fun saveProvinsisToRoom(provinsis: List<Provinsi>) {
        lookupDao.clearProvinsi() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllProvinsi(provinsis.map { it.toProvinsiEntity() })
    }

    override fun getAllProvinsisFromRoom(): Flow<List<Provinsi>> {
        return lookupDao.getAllProvinsi().map { entities ->
            entities.map { it.toProvinsi() } // Menggunakan alias toProvinsiDomain
        }.catch { emit(emptyList()) }
    }

    override suspend fun saveKabupatensToRoom(kabupatens: List<Kabupaten>) {
        lookupDao.clearKabupaten() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllKabupaten(kabupatens.map { it.toKabupatenEntity() })
    }

    override fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>> {
        return lookupDao.getAllKabupaten().map { entities ->
            entities.map { it.toKabupaten() }
        }.catch { emit(emptyList()) }
    }

    override suspend fun saveKecamatansToRoom(kecamatans: List<Kecamatan>) {
        lookupDao.clearKecamatan() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllKecamatan(kecamatans.map { it.toKecamatanEntity() })
    }

    override fun getAllKecamatansFromRoom(): Flow<List<Kecamatan>> {
        return lookupDao.getAllKecamatan().map { entities ->
            entities.map { it.toKecamatan() }
        }.catch { emit(emptyList()) }
    }

    override fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>> {
        return lookupDao.getKecamatansByKabupatenId(kabupatenId).map { entities ->
            entities.map { it.toKecamatan() }
        }.catch { emit(emptyList()) }
    }

    override suspend fun saveKelurahansToRoom(kelurahans: List<Kelurahan>) {
        lookupDao.clearKelurahan() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllKelurahan(kelurahans.map { it.toKelurahanEntity() })
    }

    override fun getAllKelurahansFromRoom(): Flow<List<Kelurahan>> {
        return lookupDao.getAllKelurahan().map { entities ->
            entities.map { it.toKelurahan() }
        }.catch { emit(emptyList()) }
    }

    override fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>> {
        return lookupDao.getKelurahansByKecamatanId(kecamatanId).map { entities ->
            entities.map { it.toKelurahan() }
        }.catch { emit(emptyList()) }
    }

    override suspend fun saveRWSToRoom(rws: List<Rw>) {
        lookupDao.clearRw() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllRw(rws.map { it.toRwEntity() })
    }

    override fun getAllRWSFromRoom(): Flow<List<Rw>> {
        return lookupDao.getAllRw().map { entities ->
            entities.map { it.toRw() }
        }.catch { emit(emptyList()) }
    }

    override fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>> {
        return lookupDao.getRwsByKelurahanId(kelurahanId).map { entities -> // Memanggil getRwByKelurahanId
            entities.map { it.toRw() }
        }.catch { emit(emptyList()) }
    }

    override suspend fun saveRTSToRoom(rts: List<Rt>) {
        lookupDao.clearRt() // Pastikan method clear ini ada di DAO
        lookupDao.insertAllRt(rts.map { it.toRtEntity() })
    }

    override fun getAllRTSFromRoom(): Flow<List<Rt>> {
        return lookupDao.getAllRt().map { entities ->
            entities.map { it.toRt() }
        }.catch { emit(emptyList()) }
    }

    override fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>> {
        return lookupDao.getRtsByRwId(rwId).map { entities -> // Memanggil getRtByRwId
            entities.map { it.toRt() }
        }.catch { emit(emptyList()) }
    }


    // Preloading/Synchronization Method
    override suspend fun preloadAllLocationAndLookupData(): Resource<Unit> {
        return try {
            coroutineScope {
                // Fetch and save Provinsis
                val provincesResult = async { getProvinsisFromApi() }
                provincesResult.await().data?.let { saveProvinsisToRoom(it) }

                // Fetch and save Kabupatens
                val kabupatensResult = async { getKabupatensFromApi() }
                kabupatensResult.await().data?.let { saveKabupatensToRoom(it) }

                // Fetch and save Kecamatans
                val kecamatansResult = async { getKecamatansFromApi() }
                kecamatansResult.await().data?.let { saveKecamatansToRoom(it) }

                // Fetch and save Kelurahans
                val kelurahansResult = async { getKelurahansFromApi() }
                kelurahansResult.await().data?.let { saveKelurahansToRoom(it) }

                // Fetch and save RWs
                val rwsResult = async { getRWSFromApi() }
                rwsResult.await().data?.let { saveRWSToRoom(it) }

                // Fetch and save RTs
                val rtsResult = async { getRTSFromApi() }
                rtsResult.await().data?.let { saveRTSToRoom(it) }

                // NEW: Hapus panggilan getUserLocationData() karena sudah tidak relevan
                // (kelurahanId kini di UserSession dan detail lokasi dari Room)
                // getUserLocationData() // <-- BARIS INI DIHAPUS

                // Concurrently fetch and cache other lookup lists
                val deferreds = listOf(
                    async { cacheLookupListNamesFromApi(apiService.getBirthAssistants(), SharedPrefsManager.KEY_BIRTH_ASSISTANTS_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getContraceptionOptions(), SharedPrefsManager.KEY_CONTRACEPTION_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getCounselingTypes(), SharedPrefsManager.KEY_COUNSELING_TYPES_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getDefecationFacilities(), SharedPrefsManager.KEY_DEFECATION_FACILITY_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getDeliveryPlaces(), SharedPrefsManager.KEY_DELIVERY_PLACES_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getDiseaseHistories(), SharedPrefsManager.KEY_DISEASE_HISTORY_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getGivenBirthStatuses(), SharedPrefsManager.KEY_GIVEN_BIRTH_STATUSES_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getImmunizationOptions(), SharedPrefsManager.KEY_IMMUNIZATION_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getMainSourceOfDrinkingWaters(), SharedPrefsManager.KEY_DRINKING_WATER_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getPostpartumComplicationOptions(), SharedPrefsManager.KEY_POSTPARTUM_COMPLICATION_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getPregnantMotherStatuses(), SharedPrefsManager.KEY_PREGNANT_MOTHER_STATUSES_OPTIONS) },
                    async { cacheLookupListNamesFromApi(apiService.getSocialAssistanceFacilitationOptions(), SharedPrefsManager.KEY_SOCIAL_ASSISTANCE_OPTIONS) }
                )
                deferreds.awaitAll()
                Resource.Success(Unit)
            }
        } catch (e: Exception) {
            Resource.Error("Failed to preload lookup data: ${e.localizedMessage}")
        }
    }


    // --- Cached Lookup Options (Mengembalikan List<String> dari SharedPrefs) ---
    override fun getDiseaseHistoryOptions(): Flow<List<String>> {
        return flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DISEASE_HISTORY_OPTIONS)) }
            .catch { emit(emptyList()) }
    }

    override fun getMainSourceOfDrinkingWaterOptions(): Flow<List<String>> {
        return flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DRINKING_WATER_OPTIONS)) }
            .catch { emit(emptyList()) }
    }

    override fun getDefecationFacilityOptions(): Flow<List<String>> {
        return flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_DEFECATION_FACILITY_OPTIONS)) }
            .catch { emit(emptyList()) }
    }

    override fun getSocialAssistanceFacilitationOptions(): Flow<List<String>> {
        return flow { emit(sharedPrefsManager.getStringList(SharedPrefsManager.KEY_SOCIAL_ASSISTANCE_OPTIONS)) }
            .catch { emit(emptyList()) }
    }

    // --- Private Helper Functions for Caching ---
    private suspend fun cacheLookupListNamesFromApi(apiCall: retrofit2.Response<List<LookupItemDto>>, prefKey: String) {
        val response = apiCall
        if (response.isSuccessful) {
            response.body()?.let { list ->
                val names = list.map { it.name }
                sharedPrefsManager.saveStringList(prefKey, names)
            }
        } else {
            throw IOException("Failed to cache lookup list for $prefKey: ${response.code()} - ${response.message()}")
        }
    }

    // Implementasi fungsi untuk mendapatkan detail lokasi lengkap
    override suspend fun getLocationDetailsByKelurahanId(kelurahanId: Int): LocationDetails? { // <<< ID sekarang Int
        val kelurahan = lookupDao.getKelurahanById(kelurahanId) // Menggunakan Int
        if (kelurahan == null) {
            Log.w("LookupRepository", "Kelurahan with ID $kelurahanId not found in local DB.")
            return null
        }

        val kecamatan = lookupDao.getKecamatanById(kelurahan.kecamatanId)
        val kabupaten = kecamatan?.let { lookupDao.getKabupatenById(it.kabupatenId) }
        val provinsi = kabupaten?.let { lookupDao.getProvinsiById(it.provinsiId) }

        return LocationDetails(
            provinsiId = provinsi?.id?.toString(), // Konversi Int ke String untuk LocationDetails
            provinsiName = provinsi?.name,
            kabupatenId = kabupaten?.id?.toString(),
            kabupatenName = kabupaten?.name,
            kecamatanId = kecamatan?.id?.toString(),
            kecamatanName = kecamatan?.name,
            kelurahanId = kelurahan.id.toString(), // Konversi Int ke String
            kelurahanName = kelurahan.name
        )
    }
}
