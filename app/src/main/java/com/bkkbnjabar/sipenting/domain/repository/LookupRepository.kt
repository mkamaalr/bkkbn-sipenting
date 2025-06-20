package com.bkkbnjabar.sipenting.domain.repository

import com.bkkbnjabar.sipenting.data.model.Kabupaten
import com.bkkbnjabar.sipenting.data.model.Kecamatan
import com.bkkbnjabar.sipenting.data.model.Kelurahan
import com.bkkbnjabar.sipenting.data.model.Provinsi
import com.bkkbnjabar.sipenting.data.model.Rt
import com.bkkbnjabar.sipenting.data.model.Rw
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemDto // Import DTO generik
import com.bkkbnjabar.sipenting.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LookupRepository {
    // API Fetch Methods (Suspending functions for network operations)
    suspend fun getProvinsisFromApi(): Resource<List<Provinsi>>
    suspend fun getKabupatensFromApi(provinsiId: Int? = null): Resource<List<Kabupaten>> // Tambah parameter opsional
    suspend fun getKecamatansFromApi(kabupatenId: Int? = null): Resource<List<Kecamatan>> // Tambah parameter opsional
    suspend fun getKelurahansFromApi(kecamatanId: Int? = null): Resource<List<Kelurahan>>
    suspend fun getRWSFromApi(kelurahanId: Int? = null): Resource<List<Rw>>
    suspend fun getRTSFromApi(rwId: Int? = null): Resource<List<Rt>>
    suspend fun getUserLocationDataFromApi(): Resource<Kelurahan>

    // Room Data Methods (Local persistence operations)
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

    // Preloading/Synchronization Method
    suspend fun preloadAllLocationData(): Resource<Unit>

    // Other Lookup Items (sesuai yang sudah ada)
    suspend fun getBirthAssistants(): Resource<List<LookupItemDto>>
    suspend fun getContraceptionOptions(): Resource<List<LookupItemDto>>
    suspend fun getCounselingTypes(): Resource<List<LookupItemDto>>
    suspend fun getDefecationFacilities(): Resource<List<LookupItemDto>>
    suspend fun getDeliveryPlaces(): Resource<List<LookupItemDto>>
    suspend fun getDiseaseHistories(): Resource<List<LookupItemDto>>
    suspend fun getGivenBirthStatuses(): Resource<List<LookupItemDto>>
    suspend fun getImmunizationOptions(): Resource<List<LookupItemDto>>
    suspend fun getMainSourceOfDrinkingWaters(): Resource<List<LookupItemDto>>
    suspend fun getPostpartumComplicationOptions(): Resource<List<LookupItemDto>>
    suspend fun getPregnantMotherStatuses(): Resource<List<LookupItemDto>>
    suspend fun getSocialAssistanceFacilitationOptions(): Resource<List<LookupItemDto>>
}
