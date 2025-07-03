package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.child.*
import com.bkkbnjabar.sipenting.data.remote.model.ApiResponse
import com.bkkbnjabar.sipenting.data.remote.model.PersonDto
import com.bkkbnjabar.sipenting.data.remote.model.PersonUploadRequest
import com.bkkbnjabar.sipenting.data.remote.model.VisitDto
import com.bkkbnjabar.sipenting.data.remote.model.VisitUploadRequest
import retrofit2.Response
import retrofit2.http.*

interface ChildApiService {

    // --- Child Mother Endpoints ---

    @GET("v1/child-mothers")
    suspend fun getAllMothers(@Query("page") page: Int? = null): Response<ApiResponse<List<PersonDto>>>

    @POST("v1/child-mothers")
    suspend fun createMother(@Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    @PUT("v1/child-mothers/{id}")
    suspend fun updateMother(@Path("id") id: String, @Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    // --- Child Endpoints ---

    @GET("v1/children")
    suspend fun getAllChildren(@Query("page") page: Int? = null): Response<ApiResponse<List<PersonDto>>>

    @POST("v1/children")
    suspend fun createChild(@Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    @PUT("v1/children/{id}")
    suspend fun updateChild(@Path("id") id: String, @Body request: PersonUploadRequest): Response<ApiResponse<PersonDto>>

    // --- Child Visit Endpoints ---

    @GET("v1/child-visits")
    suspend fun getAllChildVisits(@Query("page") page: Int? = null): Response<ApiResponse<List<VisitDto>>>

    @POST("v1/child-visits")
    suspend fun createChildVisit(@Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>

    @PUT("v1/child-visits/{id}")
    suspend fun updateChildVisit(@Path("id") id: String, @Body request: VisitUploadRequest): Response<ApiResponse<VisitDto>>
}
