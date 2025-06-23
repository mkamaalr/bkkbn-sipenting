package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.lookup.KecamatanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.ProvinsiListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KabupatenListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanDto
import com.bkkbnjabar.sipenting.data.model.lookup.RwListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.RtListResponse
import com.bkkbnjabar.sipenting.data.model.lookup.SingleKelurahanResponse
import com.bkkbnjabar.sipenting.data.model.lookup.LookupItemListResponse
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.LookupItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LookupApiService {
    @GET("v1/lookups/provinsis")
    suspend fun getProvinsis(): Response<ProvinsiListResponse>

    @GET("v1/lookups/kabupatens")
    suspend fun getKabupatens(@Query("provinsi_id") provinsiId: Int? = null): Response<KabupatenListResponse>

    @GET("v1/lookups/kecamatans")
    suspend fun getKecamatans(@Query("kabupaten_id") kabupatenId: Int? = null): Response<KecamatanListResponse>

    @GET("v1/lookups/kelurahans")
    suspend fun getKelurahans(@Query("kecamatan_id") kecamatanId: Int? = null): Response<KelurahanListResponse>

    @GET("v1/lookups/rws")
    suspend fun getRWS(@Query("kelurahan_id") kelurahanId: Int? = null): Response<RwListResponse>

    @GET("v1/lookups/rts")
    suspend fun getRTS(@Query("rw_id") rwId: Int? = null): Response<RtListResponse>

    @GET("v1/lookups/user-location")
    // FIXED: Mengembalikan Response<KelurahanDto> karena ini DTO dari API
    suspend fun getUserLocation(): Response<Kelurahan>

    @GET("v1/lookups/birth-assistants")
    suspend fun getBirthAssistants(): Response<List<LookupItemDto>>

    @GET("v1/lookups/contraception-options")
    suspend fun getContraceptionOptions(): Response<List<LookupItemDto>>

    @GET("v1/lookups/counseling-types")
    suspend fun getCounselingTypes(): Response<List<LookupItemDto>>

    @GET("v1/lookups/defecation-facilities")
    suspend fun getDefecationFacilities(): Response<List<LookupItemDto>>

    @GET("v1/lookups/delivery-places")
    suspend fun getDeliveryPlaces(): Response<List<LookupItemDto>>

    @GET("v1/lookups/disease-histories")
    suspend fun getDiseaseHistories(): Response<List<LookupItemDto>>

    @GET("v1/lookups/given-birth-statuses")
    suspend fun getGivenBirthStatuses(): Response<List<LookupItemDto>>

    @GET("v1/lookups/immunization-options")
    suspend fun getImmunizationOptions(): Response<List<LookupItemDto>>

    @GET("v1/lookups/main-source-of-drinking-waters")
    suspend fun getMainSourceOfDrinkingWaters(): Response<List<LookupItemDto>>

    @GET("v1/lookups/postpartum-complication-options")
    suspend fun getPostpartumComplicationOptions(): Response<List<LookupItemDto>>

    @GET("v1/lookups/pregnant-mother-statuses")
    suspend fun getPregnantMotherStatuses(): Response<List<LookupItemDto>>

    @GET("v1/lookups/social-assistance-facilitation-options")
    suspend fun getSocialAssistanceFacilitationOptions(): Response<List<LookupItemDto>>
}
