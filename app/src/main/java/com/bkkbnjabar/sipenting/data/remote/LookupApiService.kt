package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.lookup.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service interface for fetching all lookup and location data from the API.
 */
interface LookupApiService {

    @GET("v1/lookups/provinsis")
    suspend fun getProvinsis(): Response<ProvinsiListResponse>

    @GET("v1/lookups/kabupatens")
    suspend fun getKabupatens(): Response<KabupatenListResponse>

    @GET("v1/lookups/kecamatans")
    suspend fun getKecamatans(): Response<KecamatanListResponse>

    @GET("v1/lookups/kelurahans")
    suspend fun getKelurahans(): Response<KelurahanListResponse>

    @GET("v1/lookups/rws")
    suspend fun getRws(): Response<RwListResponse>

    @GET("v1/lookups/rts")
    suspend fun getRts(): Response<RtListResponse>

    /**
     * Fetches a list of generic lookup items based on their type.
     * @param type The type of lookup to fetch (e.g., "riwayat_penyakit").
     */
    @GET("v1/lookups/{type}")
    suspend fun getLookupItem(@Path("type") type: String): Response<LookupItemListResponse>

    /**
     * Mengambil SEMUA data lookup (lokasi dan opsi) dalam satu panggilan.
     * Sangat efisien untuk proses preloading awal.
     */
    @GET("v1/lookups/all")
    suspend fun getAllLookups(): Response<AllLookupsResponse>
}
