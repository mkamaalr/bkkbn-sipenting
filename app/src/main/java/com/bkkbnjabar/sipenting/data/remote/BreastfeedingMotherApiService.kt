package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherUploadRequest
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BreastfeedingMotherApiService {
    @POST("v1/breastfeeding-mothers") // Sesuaikan endpoint API Anda
    suspend fun uploadBreastfeedingMother(@Body request: BreastfeedingMotherUploadRequest): Response<BreastfeedingMotherUploadResponse>

    // Anda mungkin perlu menambahkan GET endpoint jika ingin mengambil daftar dari server
    // @GET("api/v1/breastfeeding-mothers")
    // suspend fun getBreastfeedingMothersFromServer(): Response<List<BreastfeedingMotherResponseDto>>
}