package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk membungkus data ibu dan kunjungannya saat akan diunggah ke server.
 */
data class PregnantMotherUploadRequest(
    @SerializedName("mother_data")
    val motherData: PregnantMotherRegistrationData,
    @SerializedName("visit_data")
    val visitData: PregnantMotherVisitData
)
