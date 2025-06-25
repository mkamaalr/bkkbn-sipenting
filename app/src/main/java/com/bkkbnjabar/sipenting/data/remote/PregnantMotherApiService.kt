package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherResponse
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadRequest
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit service interface for API calls related to pregnant mothers.
 */
interface PregnantMotherApiService {

    /**
     * Fetches a list of all pregnant mothers.
     */
    @GET("pregnant-mothers")
    suspend fun getAllPregnantMothers(): Response<List<PregnantMotherResponse>>

    /**
     * Submits a new pregnant mother record along with her first visit data.
     */
    @POST("pregnant-mothers/upload")
    suspend fun uploadPregnantMotherData(@Body uploadRequest: PregnantMotherUploadRequest): Response<PregnantMotherUploadResponse>
}