package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherResponse
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PregnantMotherApiService {
    @GET("pregnant-mothers")
    suspend fun getPregnantMothers(): Response<List<Any>> // Ganti Any dengan model respons yang sebenarnya
}