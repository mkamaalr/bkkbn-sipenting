package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk mengirim data ibu hamil baru ke server.
 */
data class PregnantMotherRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("nik")
    val nik: String,
    // ... tambahkan field lain yang diperlukan untuk request ...
)
