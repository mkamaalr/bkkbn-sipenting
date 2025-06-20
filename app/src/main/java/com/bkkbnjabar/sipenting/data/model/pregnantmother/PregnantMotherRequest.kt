package com.bkkbnjabar.sipenting.data.model.pregnantmother

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PregnantMotherRequest(
    @Json(name = "name") val name: String,
    @Json(name = "nik") val nik: String,
    @Json(name = "date_of_birth") val dateOfBirth: String, // String untuk format DD/MM/YYYY
    @Json(name = "phone_number") val phoneNumber: String?, // PERBAIKAN: Menjadikan ini nullable
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "kelurahan_id") val kelurahanId: Int?,
    @Json(name = "rw_id") val rwId: Int?,
    @Json(name = "rt_id") val rtId: Int?,
    @Json(name = "full_address") val fullAddress: String,
    @Json(name = "registration_date") val registrationDate: String,
    @Json(name = "maternal_age") val maternalAge: Int? = null,
    @Json(name = "last_menstrual_period") val lastMenstrualPeriod: String? = null,
    @Json(name = "estimated_delivery_date") val estimatedDeliveryDate: String? = null,
    @Json(name = "number_of_children") val numberOfChildren: Int? = null,
    @Json(name = "number_of_abortions") val numberOfAbortions: Int? = null,
    @Json(name = "husband_name") val husbandName: String?, // PERBAIKAN: Menjadikan ini nullable
    @Json(name = "husband_nik") val husbandNik: String? = null,
    @Json(name = "husband_phone_number") val husbandPhoneNumber: String? = null,
    @Json(name = "is_risky") val isRisky: Boolean? = null,
    @Json(name = "risk_factors") val riskFactors: String? = null,
    @Json(name = "registered_by_user_id") val registeredByUserId: Int? = null // Harus diambil dari SharedPrefs
)
