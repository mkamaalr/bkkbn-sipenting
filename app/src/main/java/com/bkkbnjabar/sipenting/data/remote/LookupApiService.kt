package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.lookup.KecamatanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.ProvinsiListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KabupatenListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.RwListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.RtListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.SingleKelurahanResponse
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemDto // Untuk item lookup lainnya
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LookupApiService {
    @GET("v1/lookups/provinsis")
    suspend fun getProvinsis(): Response<ProvinsiListResponse>

    @GET("v1/lookups/kabupatens")
    suspend fun getKabupatens(@Query("provinsi_id") provinsiId: Int? = null): Response<KabupatenListResponse>

    @GET("v1/lookups/kecamatans")
    suspend fun getKecamatans(@Query("kabupaten_id") kabupatenId: Int? = null): Response<KecamatanListResponse> // Menambahkan parameter kabupaten_id

    @GET("v1/lookups/kelurahans")
    suspend fun getKelurahans(@Query("kecamatan_id") kecamatanId: Int? = null): Response<KelurahanListResponse>

    @GET("v1/lookups/rws")
    suspend fun getRWS(@Query("kelurahan_id") kelurahanId: Int? = null): Response<RwListResponse>

    @GET("v1/lookups/rts")
    suspend fun getRTS(@Query("rw_id") rwId: Int? = null): Response<RtListResponse>

    @GET("v1/user/location")
    suspend fun getUserLocationDataFromApi(): Response<SingleKelurahanResponse>

    // Endpoint untuk LookupItemDto lainnya
    @GET("v1/lookups/birth_assistants")
    suspend fun getBirthAssistants(): Response<LookupItemListResponse> // Pastikan ini juga dibungkus jika API mengembalikannya dalam 'data'
    @GET("v1/lookups/contraception_options")
    suspend fun getContraceptionOptions(): Response<LookupItemListResponse>
    @GET("v1/lookups/counseling_types")
    suspend fun getCounselingTypes(): Response<LookupItemListResponse>
    @GET("v1/lookups/defecation_facilities")
    suspend fun getDefecationFacilities(): Response<LookupItemListResponse>
    @GET("v1/lookups/delivery_places")
    suspend fun getDeliveryPlaces(): Response<LookupItemListResponse>
    @GET("v1/lookups/disease_histories")
    suspend fun getDiseaseHistories(): Response<LookupItemListResponse>
    @GET("v1/lookups/given_birth_statuses")
    suspend fun getGivenBirthStatuses(): Response<LookupItemListResponse>
    @GET("v1/lookups/immunization_options")
    suspend fun getImmunizationOptions(): Response<LookupItemListResponse>
    @GET("v1/lookups/main_source_of_drinking_waters")
    suspend fun getMainSourceOfDrinkingWaters(): Response<LookupItemListResponse>
    @GET("v1/lookups/postpartum_complication_options")
    suspend fun getPostpartumComplicationOptions(): Response<LookupItemListResponse>
    @GET("v1/lookups/pregnant_mother_statuses")
    suspend fun getPregnantMotherStatuses(): Response<LookupItemListResponse>
    @GET("v1/lookups/social_assistance_facilitation_options")
    suspend fun getSocialAssistanceFacilitationOptions(): Response<LookupItemListResponse>
}
