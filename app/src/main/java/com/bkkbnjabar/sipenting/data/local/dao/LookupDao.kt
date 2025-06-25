package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for all lookup-related entities.
 * This interface provides methods to interact with the location tables
 * (provinsi, kabupaten, kecamatan, etc.) in the local Room database.
 */
@Dao
interface LookupDao {

    // --- INSERT METHODS (for preloading data from API) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProvinsis(provinsis: List<ProvinsiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKabupatens(kabupatens: List<KabupatenEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKecamatans(kecamatans: List<KecamatanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKelurahans(kelurahans: List<KelurahanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRws(rws: List<RwEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRts(rts: List<RtEntity>)


    // --- GET ALL METHODS (Reactive, using Flow) ---

    @Query("SELECT * FROM provinsi ORDER BY name ASC")
    fun getAllProvinsis(): Flow<List<ProvinsiEntity>>

    @Query("SELECT * FROM kabupaten ORDER BY name ASC")
    fun getAllKabupatens(): Flow<List<KabupatenEntity>>


    // --- GET FILTERED LIST METHODS (Reactive, using Flow) ---

    @Query("SELECT * FROM kecamatan WHERE kabupatenId = :kabupatenId ORDER BY name ASC")
    fun getKecamatansByKabupaten(kabupatenId: Int): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kelurahan WHERE kecamatanId = :kecamatanId ORDER BY name ASC")
    fun getKelurahansByKecamatan(kecamatanId: Int): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM rw WHERE kelurahanId = :kelurahanId ORDER BY name ASC")
    fun getRwsByKelurahan(kelurahanId: Int): Flow<List<RwEntity>>

    @Query("SELECT * FROM rt WHERE rwId = :rwId ORDER BY name ASC")
    fun getRtsByRw(rwId: Int): Flow<List<RtEntity>>


    // --- GET SINGLE ITEM BY ID METHODS (One-shot, suspend) ---

    @Query("SELECT * FROM provinsi WHERE id = :id LIMIT 1")
    suspend fun getProvinsiById(id: Int?): ProvinsiEntity?

    @Query("SELECT * FROM kabupaten WHERE id = :id LIMIT 1")
    suspend fun getKabupatenById(id: Int?): KabupatenEntity?

    @Query("SELECT * FROM kecamatan WHERE id = :id LIMIT 1")
    suspend fun getKecamatanById(id: Int?): KecamatanEntity?

    @Query("SELECT * FROM kelurahan WHERE id = :id LIMIT 1")
    suspend fun getKelurahanById(id: Int?): KelurahanEntity?


    // ================== FUNGSI BARU UNTUK MEMERIKSA DATA ==================
    /**
     * Menghitung jumlah provinsi di database.
     * Digunakan untuk memeriksa apakah data lookup sudah pernah diunduh.
     * @return Jumlah baris di tabel provinsi.
     */
    @Query("SELECT COUNT(id) FROM provinsi")
    suspend fun getProvinsiCount(): Int
    // ====================================================================
    // ================== FUNGSI BARU UNTUK LOOKUP ITEMS ==================

    /**
     * Menyimpan daftar LookupItemEntity ke database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLookupItems(items: List<LookupItemEntity>)

    /**
     * Mengambil daftar LookupItemEntity dari database berdasarkan tipenya.
     * @param type Jenis lookup yang ingin diambil (misal: "disease_histories").
     * @return Flow yang berisi daftar item yang sesuai.
     */
    @Query("SELECT * FROM lookup_item WHERE type = :type ORDER BY name ASC")
    fun getLookupItemsByType(type: String): Flow<List<LookupItemEntity>>

    // ====================================================================
}
