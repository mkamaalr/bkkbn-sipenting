package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherResponse
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PregnantMotherApiService {
    @POST("api/v1/pregnant-mothers") // Sesuaikan endpoint API Anda
    suspend fun uploadPregnantMother(@Body request: PregnantMotherUploadRequest): Response<PregnantMotherUploadResponse>

    // Anda mungkin perlu menambahkan GET endpoint jika ingin mengambil data dari server juga
    // @GET("api/v1/pregnant-mothers")
    // suspend fun getPregnantMothersFromServer(): Response<List<PregnantMotherResponseDto>>

    @POST("pregnant-mothers")
    suspend fun createPregnantMother(@Body request: PregnantMotherUploadRequest): Response<PregnantMotherResponse>

}