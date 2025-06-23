package com.bkkbnjabar.sipenting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LookupDao {

    // --- Provinsi Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProvinsi(provinsis: List<ProvinsiEntity>)

    @Query("SELECT * FROM provinsi_table ORDER BY name ASC")
    fun getAllProvinsi(): Flow<List<ProvinsiEntity>>

    @Query("SELECT * FROM provinsi_table WHERE id = :provinsiId LIMIT 1")
    suspend fun getProvinsiById(provinsiId: Int): ProvinsiEntity?

    @Query("DELETE FROM provinsi_table")
    suspend fun clearProvinsi()

    // --- Kabupaten Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKabupaten(kabupatens: List<KabupatenEntity>)

    @Query("SELECT * FROM kabupaten_table ORDER BY name ASC")
    fun getAllKabupaten(): Flow<List<KabupatenEntity>>

    @Query("SELECT * FROM kabupaten_table WHERE id = :kabupatenId LIMIT 1")
    suspend fun getKabupatenById(kabupatenId: Int): KabupatenEntity?

    @Query("DELETE FROM kabupaten_table")
    suspend fun clearKabupaten()

    // --- Kecamatan Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKecamatan(kecamatans: List<KecamatanEntity>)

    @Query("SELECT * FROM kecamatan_table ORDER BY name ASC")
    fun getAllKecamatan(): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatan_table WHERE kabupatenId = :kabupatenId ORDER BY name ASC")
    fun getKecamatansByKabupatenId(kabupatenId: Int): Flow<List<KecamatanEntity>>

    @Query("SELECT * FROM kecamatan_table WHERE id = :kecamatanId LIMIT 1")
    suspend fun getKecamatanById(kecamatanId: Int): KecamatanEntity?

    @Query("DELETE FROM kecamatan_table")
    suspend fun clearKecamatan()

    // --- Kelurahan Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKelurahan(kelurahans: List<KelurahanEntity>)

    @Query("SELECT * FROM kelurahan_table ORDER BY name ASC")
    fun getAllKelurahan(): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahan_table WHERE kecamatanId = :kecamatanId ORDER BY name ASC")
    fun getKelurahansByKecamatanId(kecamatanId: Int): Flow<List<KelurahanEntity>>

    @Query("SELECT * FROM kelurahan_table WHERE id = :kelurahanId LIMIT 1")
    suspend fun getKelurahanById(kelurahanId: Int): KelurahanEntity? // <<< TAMBAHKAN INI

    @Query("DELETE FROM kelurahan_table")
    suspend fun clearKelurahan()

    // --- RW Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRw(rws: List<RwEntity>)

    @Query("SELECT * FROM rw_table ORDER BY name ASC")
    fun getAllRw(): Flow<List<RwEntity>>

    @Query("SELECT * FROM rw_table WHERE kelurahanId = :kelurahanId ORDER BY name ASC")
    fun getRwsByKelurahanId(kelurahanId: Int): Flow<List<RwEntity>>

    @Query("DELETE FROM rw_table")
    suspend fun clearRw()

    // --- RT Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRt(rts: List<RtEntity>)

    @Query("SELECT * FROM rt_table ORDER BY name ASC")
    fun getAllRt(): Flow<List<RtEntity>>

    @Query("SELECT * FROM rt_table WHERE rwId = :rwId ORDER BY name ASC")
    fun getRtsByRwId(rwId: Int): Flow<List<RtEntity>>

    @Query("DELETE FROM rt_table")
    suspend fun clearRt()

}
