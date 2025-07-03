package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.remote.model.ApiResponse
import com.bkkbnjabar.sipenting.data.remote.model.PersonDto
import com.bkkbnjabar.sipenting.data.remote.model.PersonUploadRequest
import com.bkkbnjabar.sipenting.data.remote.model.VisitDto
import com.bkkbnjabar.sipenting.data.remote.model.VisitUploadRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit service interface for all endpoints related to the Pregnant Mother module.
 */
interface PregnantMotherApiService {

    // --- Pregnant Mother Endpoints ---

    /**
     * Fetches a paginated list of all pregnant mothers from the server.
     */
    @GET("v1/pregnant-mothers")
    suspend fun getAllPregnantMothers(@Query("page") page: Int? = null): Response<ApiResponse<List<PersonDto>>>

    /**
     * Creates a new pregnant mother record on the server.
     */
    @POST("v1/pregnant-mothers")
    suspend fun createPregnantMother(@Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    /**
     * Updates an existing pregnant mother record on the server.
     */
    @PUT("pregnant-mothers/{id}")
    suspend fun updatePregnantMother(@Path("id") id: String, @Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    // --- Pregnant Mother Visit Endpoints ---

    /**
     * Fetches a paginated list of all pregnant mother visits from the server.
     */
    @GET("v1/pregnant-mother-visits")
    suspend fun getAllPregnantMotherVisits(@Query("page") page: Int? = null): Response<ApiResponse<List<VisitDto>>>

    /**
     * Creates a new pregnant mother visit record on the server.
     */
    @POST("v1/pregnant-mother-visits")
    suspend fun createPregnantMotherVisit(@Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>

    /**
     * Updates an existing pregnant mother visit record on the server.
     */
    @PUT("v1/pregnant-mother-visits/{id}")
    suspend fun updatePregnantMotherVisit(@Path("id") id: String, @Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>
}
