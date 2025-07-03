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
 * Retrofit service interface for all endpoints related to the Breastfeeding Mother module.
 */
interface BreastfeedingMotherApiService {

    // --- Breastfeeding Mother Endpoints ---

    /**
     * Fetches a paginated list of all breastfeeding mothers from the server.
     */
    @GET("v1/breastfeeding-mothers")
    suspend fun getAllBreastfeedingMothers(@Query("page") page: Int? = null): Response<ApiResponse<List<PersonDto>>>

    /**
     * Creates a new breastfeeding mother record on the server.
     */
    @POST("v1/breastfeeding-mothers")
    suspend fun createBreastfeedingMother(@Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    /**
     * Updates an existing breastfeeding mother record on the server.
     */
    @PUT("v1/breastfeeding-mothers/{id}")
    suspend fun updateBreastfeedingMother(@Path("id") id: String, @Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    // --- Breastfeeding Mother Visit Endpoints ---

    /**
     * Fetches a paginated list of all breastfeeding mother visits from the server.
     */
    @GET("v1/breastfeeding-mother-visits")
    suspend fun getAllBreastfeedingMotherVisits(@Query("page") page: Int? = null): Response<ApiResponse<List<VisitDto>>>

    /**
     * Creates a new breastfeeding mother visit record on the server.
     */
    @POST("v1/breastfeeding-mother-visits")
    suspend fun createBreastfeedingMotherVisit(@Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>

    /**
     * Updates an existing breastfeeding mother visit record on the server.
     */
    @PUT("v1/breastfeeding-mother-visits/{id}")
    suspend fun updateBreastfeedingMotherVisit(@Path("id") id: String, @Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>
}
