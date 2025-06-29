package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for all lookup-related entities.
 * This interface provides methods to interact with the location and lookup item tables
 * in the local Room database.
 */
@Dao
interface LookupDao {

    // --- INSERT METHODS (for preloading data) ---

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLookupItems(items: List<LookupItemEntity>)


    // --- CLEAR/DELETE METHODS (for resetting data before preload) ---

    @Query("DELETE FROM provinsi")
    suspend fun clearAllProvinsis()

    @Query("DELETE FROM kabupaten")
    suspend fun clearAllKabupatens()

    @Query("DELETE FROM kecamatan")
    suspend fun clearAllKecamatans()

    @Query("DELETE FROM kelurahan")
    suspend fun clearAllKelurahans()

    @Query("DELETE FROM rw")
    suspend fun clearAllRws()

    @Query("DELETE FROM rt")
    suspend fun clearAllRts()

    @Query("DELETE FROM lookup_item")
    suspend fun clearAllLookupItems()


    // --- GET ALL & FILTERED LIST METHODS (Reactive, using Flow) ---

    @Query("SELECT * FROM provinsi ORDER BY name ASC")
    fun getAllProvinsis(): Flow<List<ProvinsiEntity>>

    @Query("SELECT * FROM kabupaten ORDER BY name ASC")
    fun getAllKabupatens(): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kecamatan WHERE kabupatenId = :kabupatenId ORDER BY name ASC")
    fun getKecamatansByKabupaten(kabupatenId: Int): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kelurahan WHERE kecamatanId = :kecamatanId ORDER BY name ASC")
    fun getKelurahansByKecamatan(kecamatanId: Int): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM rw WHERE kelurahanId = :kelurahanId ORDER BY name ASC")
    fun getRwsByKelurahan(kelurahanId: Int): Flow<List<RwEntity>>

    @Query("SELECT * FROM rt WHERE rwId = :rwId ORDER BY name ASC")
    fun getRtsByRw(rwId: Int): Flow<List<RtEntity>>

    @Query("SELECT * FROM lookup_item WHERE type = :type ORDER BY name ASC")
    fun getLookupItemsByType(type: String): Flow<List<LookupItemEntity>>


    // --- GET SINGLE ITEM BY ID & COUNT METHODS ---

    @Query("SELECT * FROM provinsi WHERE id = :id LIMIT 1")
    suspend fun getProvinsiById(id: Int?): ProvinsiEntity?

    @Query("SELECT * FROM kabupaten WHERE id = :id LIMIT 1")
    suspend fun getKabupatenById(id: Int?): KabupatenEntity?

    @Query("SELECT * FROM kecamatan WHERE id = :id LIMIT 1")
    suspend fun getKecamatanById(id: Int?): KecamatanEntity?

    @Query("SELECT * FROM kelurahan WHERE id = :id LIMIT 1")
    suspend fun getKelurahanById(id: Int?): KelurahanEntity?

    @Query("SELECT COUNT(id) FROM provinsi")
    suspend fun getProvinsiCount(): Int
}