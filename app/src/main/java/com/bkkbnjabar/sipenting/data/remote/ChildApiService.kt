package com.bkkbnjabar.sipenting.data.remote

import com.bkkbnjabar.sipenting.data.model.child.ChildUploadRequest
import com.bkkbnjabar.sipenting.data.model.child.ChildUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ChildApiService {
    @POST("v1/children") // Sesuaikan endpoint API Anda
    suspend fun uploadChild(@Body request: ChildUploadRequest): Response<ChildUploadResponse>

    // Anda mungkin perlu menambahkan GET endpoint jika ingin mengambil daftar dari server
    // @GET("api/v1/children")
    // suspend fun getChildrenFromServer(): Response<List<ChildResponseDto>>
}
