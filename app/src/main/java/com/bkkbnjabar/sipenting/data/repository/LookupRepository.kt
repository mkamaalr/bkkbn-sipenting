package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.domain.model.LookupItemDto
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.LocationDetails
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Rt
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LookupRepository {
    // --- API Fetch Methods (Mengembalikan Domain Model) ---
    suspend fun getProvinsisFromApi(): Resource<List<Provinsi>>
    suspend fun getKabupatensFromApi(provinsiId: Int? = null): Resource<List<Kabupaten>>
    suspend fun getKecamatansFromApi(kabupatenId: Int? = null): Resource<List<Kecamatan>>
    suspend fun getKelurahansFromApi(kecamatanId: Int? = null): Resource<List<Kelurahan>>
    suspend fun getRWSFromApi(kelurahanId: Int? = null): Resource<List<Rw>>
    suspend fun getRTSFromApi(rwId: Int? = null): Resource<List<Rt>>

    // API Fetch methods for generic lookup lists (returning DTOs directly as per your code)
    suspend fun getBirthAssistantsFromApi(): Resource<List<LookupItemDto>>
    suspend fun getContraceptionOptionsFromApi(): Resource<List<LookupItemDto>>
    suspend fun getCounselingTypesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getDefecationFacilitiesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getDeliveryPlacesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getDiseaseHistoriesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getGivenBirthStatusesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getImmunizationOptionsFromApi(): Resource<List<LookupItemDto>>
    suspend fun getMainSourceOfDrinkingWatersFromApi(): Resource<List<LookupItemDto>>
    suspend fun getPostpartumComplicationOptionsFromApi(): Resource<List<LookupItemDto>>
    suspend fun getPregnantMotherStatusesFromApi(): Resource<List<LookupItemDto>>
    suspend fun getSocialAssistanceFacilitationOptionsFromApi(): Resource<List<LookupItemDto>>


    // --- Room Data Methods (Local persistence operations) ---
    suspend fun saveProvinsisToRoom(provinsis: List<Provinsi>)
    fun getAllProvinsisFromRoom(): Flow<List<Provinsi>>
    suspend fun saveKabupatensToRoom(kabupatens: List<Kabupaten>)
    fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>>
    suspend fun saveKecamatansToRoom(kecamatans: List<Kecamatan>)
    fun getAllKecamatansFromRoom(): Flow<List<Kecamatan>>
    fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>>
    suspend fun saveKelurahansToRoom(kelurahans: List<Kelurahan>)
    fun getAllKelurahansFromRoom(): Flow<List<Kelurahan>>
    fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>>
    suspend fun saveRWSToRoom(rws: List<Rw>)
    fun getAllRWSFromRoom(): Flow<List<Rw>>
    fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>>
    suspend fun saveRTSToRoom(rts: List<Rt>)
    fun getAllRTSFromRoom(): Flow<List<Rt>>
    fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>>


    // --- Preloading/Synchronization Method (fungsi komprehensif Anda) ---
    suspend fun preloadAllLocationAndLookupData(): Resource<Unit>

    // --- Lookup Details for User's Location (from Room, based on kelurahanId) ---
    suspend fun getLocationDetailsByKelurahanId(kelurahanId: Int): LocationDetails? // <<< BERUBAH KE INT!


    // --- Cached Lookup Options (Mengembalikan List<String> dari SharedPrefs) ---
    fun getDiseaseHistoryOptions(): Flow<List<String>>
    fun getMainSourceOfDrinkingWaterOptions(): Flow<List<String>>
    fun getDefecationFacilityOptions(): Flow<List<String>>
    fun getSocialAssistanceFacilitationOptions(): Flow<List<String>>
}
