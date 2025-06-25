package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk respons setelah berhasil mengunggah data.
 */
data class PregnantMotherUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("server_id")
    val serverId: String? // ID data yang baru dibuat di server
)
