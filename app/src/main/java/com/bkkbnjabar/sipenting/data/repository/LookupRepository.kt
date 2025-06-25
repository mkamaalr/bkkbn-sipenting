package com.bkkbnjabar.sipenting.data.repository

import com.bkkbnjabar.sipenting.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk repository yang menangani data lookup (lokasi, opsi, dll).
 */
interface LookupRepository {
    suspend fun preloadAllLookupData()
    fun getAllProvinsisFromRoom(): Flow<List<Provinsi>>
    fun getAllKabupatensFromRoom(): Flow<List<Kabupaten>>
    fun getKecamatansByKabupatenFromRoom(kabupatenId: Int): Flow<List<Kecamatan>>
    fun getKelurahansByKecamatanFromRoom(kecamatanId: Int): Flow<List<Kelurahan>>
    fun getRWSByKelurahanFromRoom(kelurahanId: Int): Flow<List<Rw>>
    fun getRTSByRwFromRoom(rwId: Int): Flow<List<Rt>>
    // Fungsi untuk mengambil opsi dinamis dari database
    fun getLookupOptions(type: String): Flow<List<LookupItem>>
    suspend fun getLocationDetailsByKelurahanId(kelurahanId: Int): LocationDetails?
    fun getDiseaseHistoryOptions(): Flow<List<String>>
    fun getMainSourceOfDrinkingWaterOptions(): Flow<List<String>>
    fun getDefecationFacilityOptions(): Flow<List<String>>
    fun getSocialAssistanceFacilitationOptions(): Flow<List<String>>
}