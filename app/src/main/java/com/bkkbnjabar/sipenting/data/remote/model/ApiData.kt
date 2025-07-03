package com.bkkbnjabar.sipenting.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * A generic response wrapper that your API likely uses for all endpoints.
 */
@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status") val status: String?,
    @Json(name = "message") val message: String?,
    @Json(name = "data") val data: T?
)

/**
 * A generic DTO for all lookup items (dropdowns, chips, etc.).
 */
@JsonClass(generateAdapter = true)
data class LookupItemDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "is_risky") val isRisky: Boolean? = false,
    @Json(name = "description") val description: String? = null
)

/**
 * A DTO that contains all possible lookup lists from the server.
 */
@JsonClass(generateAdapter = true)
data class AllLookupsDto(
    @Json(name = "child_statuses") val childStatuses: List<LookupItemDto>?,
    @Json(name = "kka_results") val kkaResults: List<LookupItemDto>?,
    @Json(name = "contraception_types") val contraceptionTypes: List<LookupItemDto>?,
    @Json(name = "counseling_types") val counselingTypes: List<LookupItemDto>?,
    @Json(name = "immunizations") val immunizations: List<LookupItemDto>?,
    @Json(name = "drinking_water_sources") val drinkingWaterSources: List<LookupItemDto>?,
    @Json(name = "defecation_facilities") val defecationFacilities: List<LookupItemDto>?,
    @Json(name = "social_assistance_options") val socialAssistanceOptions: List<LookupItemDto>?
    // Add any other lookup lists from your API here
)

/**
 * A generic DTO for receiving any "person" (Mother, Child) from the server.
 */
@JsonClass(generateAdapter = true)
data class PersonDto(
    @Json(name = "id") val id: String,
    @Json(name = "mother_id") val motherId: String?, // Only used for ChildDto
    @Json(name = "name") val name: String?,
    @Json(name = "nik") val nik: String?,
    @Json(name = "date_of_birth") val dateOfBirth: String?,
    @Json(name = "phone_number") val phoneNumber: String?,
    @Json(name = "provinsi_name") val provinsiName: String?,
    @Json(name = "provinsi_id") val provinsiId: Int?,
    @Json(name = "kabupaten_name") val kabupatenName: String?,
    @Json(name = "kabupaten_id") val kabupatenId: Int?,
    @Json(name = "kecamatan_name") val kecamatanName: String?,
    @Json(name = "kecamatan_id") val kecamatanId: Int?,
    @Json(name = "kelurahan_name") val kelurahanName: String?,
    @Json(name = "kelurahan_id") val kelurahanId: Int?,
    @Json(name = "rw_name") val rwName: String?,
    @Json(name = "rw_id") val rwId: Int?,
    @Json(name = "rt_name") val rtName: String?,
    @Json(name = "rt_id") val rtId: Int?,
    @Json(name = "full_address") val fullAddress: String?
)

/**
 * A generic DTO for uploading a new/updated "person" to the server.
 */
data class PersonUploadRequest(
    val mother_id: String? = null, // Server ID of the mother, only for child
    val name: String,
    val nik: String,
    val date_of_birth: String,
    val phone_number: String?,
    val provinsi_id: Int,
    val kabupaten_id: Int,
    val kecamatan_id: Int,
    val kelurahan_id: Int,
    val rw_id: Int,
    val rt_id: Int,
    val full_address: String
)

/**
 * A generic DTO for receiving any type of visit from the server.
 */
@JsonClass(generateAdapter = true)
data class VisitDto(
    @Json(name = "id") val id: String,
    @Json(name = "parent_id") val parentId: String,
    @Json(name = "visit_date") val visitDate: String?,
    @Json(name = "pregnancy_age_when_childbirth") val pregnancyAgeWhenChildbirth: String?,
    @Json(name = "weight_birth") val weightBirth: Double?,
    @Json(name = "height_birth") val heightBirth: Double?,
    @Json(name = "is_asi_exclusive") val isAsiExclusive: Boolean?,
    @Json(name = "on_contraception") val onContraception: Boolean?,
    @Json(name = "contraception_type_id") val contraceptionTypeId: Int?,
    @Json(name = "contraception_reason_for_use") val contraceptionReasonForUse: String?,
    @Json(name = "contraception_rejection_reason_id") val contraceptionRTypeId: Int?,
    @Json(name = "measurement_date") val measurementDate: String?,
    @Json(name = "weight_measurement") val weightMeasurement: Double?,
    @Json(name = "height_measurement") val heightMeasurement: Double?,
    @Json(name = "is_ongoing_asi") val isOngoingAsi: Boolean?,
    @Json(name = "is_mpasi") val isMpasi: Boolean?,
    @Json(name = "is_kka_filled") val isKkaFilled: Boolean?,
    @Json(name = "kka_result") val kkaResult: String?,
    @Json(name = "is_exposed_to_cigarettes") val isExposedToCigarettes: Boolean?,
    @Json(name = "is_posyandu_month") val isPosyanduMonth: Boolean?,
    @Json(name = "is_bkb_month") val isBkbMonth: Boolean?,
    @Json(name = "is_counseling_received") val isCounselingReceived: Boolean?,
    @Json(name = "is_received_mbg") val isReceivedMbg: Boolean?,
    @Json(name = "head_circumference") val headCircumference: Double?,
    @Json(name = "counseling_type_id") val counselingTypeId: Int?,
    @Json(name = "immunizations_given") val immunizationsGiven: List<String>?,
    @Json(name = "main_source_of_drinking_water") val mainSourceOfDrinkingWater: List<String>?,
    @Json(name = "main_source_of_drinking_water_other") val mainSourceOfDrinkingWaterOther: String?,
    @Json(name = "defecation_facility") val defecationFacility: List<String>?,
    @Json(name = "defecation_facility_other") val defecationFacilityOther: String?,
    @Json(name = "facilitating_referral_service_status") val facilitatingReferralServiceStatus: String?,
    @Json(name = "facilitating_social_assistance_status") val facilitatingSocialAssistanceStatus: String?,
    @Json(name = "social_assistance_facilitation_options") val socialAssistanceFacilitationOptions: List<String>?,
    @Json(name = "social_assistance_facilitation_options_other") val socialAssistanceFacilitationOptionsOther: String?,
    @Json(name = "pregnant_mother_status_id") val pregnantMotherStatusId: Int?,
    @Json(name = "next_visit_date") val nextVisitDate: String?,
    @Json(name = "tpk_notes") val tpkNotes: String?,
    @Json(name = "image_path_1") val imagePath1: String?,
    @Json(name = "image_path_2") val imagePath2: String?,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?
)

/**
 * A DTO for uploading any type of visit to the server.
 */
data class VisitUploadRequest(
    val parent_id: String, // Server ID of the parent (Child or Mother)
    val visit_date: String?,
    val pregnancy_age_when_childbirth: String?,
    val weight_birth: Double?,
    val height_birth: Double?,
    val is_asi_exclusive: Boolean?,
    val on_contraception: Boolean?,
    val contraception_type_id: Int?,
    val contraception_reason_for_use: String?,
    val contraception_rejection_reason_id: Int?,
    val measurement_date: String?,
    val weight_measurement: Double?,
    val height_measurement: Double?,
    val is_ongoing_asi: Boolean?,
    val is_mpasi: Boolean?,
    val is_kka_filled: Boolean?,
    val kka_result: String?,
    val is_exposed_to_cigarettes: Boolean?,
    val is_posyandu_month: Boolean?,
    val is_bkb_month: Boolean?,
    val is_counseling_received: Boolean?,
    val is_received_mbg: Boolean?,
    val head_circumference: Double?,
    val counseling_type_id: Int?,
    val immunizations_given: List<String>?,
    val main_source_of_drinking_water: List<String>?,
    val main_source_of_drinking_water_other: String?,
    val defecation_facility: List<String>?,
    val defecation_facility_other: String?,
    val facilitating_referral_service_status: String?,
    val facilitating_social_assistance_status: String?,
    val social_assistance_facilitation_options: List<String>?,
    val social_assistance_facilitation_options_other: String?,
    val pregnant_mother_status_id: Int?,
    val next_visit_date: String?,
    val tpk_notes: String?,
    val image_path_1: String?,
    val image_path_2: String?,
    val latitude: Double?,
    val longitude: Double?
)
