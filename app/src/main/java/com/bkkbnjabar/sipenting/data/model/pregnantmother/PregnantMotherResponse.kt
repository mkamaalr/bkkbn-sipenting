package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk data ibu hamil yang diterima dari server.
 */
data class PregnantMotherResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nik")
    val nik: String,
    @SerializedName("kelurahan_name")
    val kelurahanName: String?,
    @SerializedName("sync_status")
    val syncStatus: String // e.g., "DONE"
    // ... tambahkan field lain yang diterima dari response ...
)
