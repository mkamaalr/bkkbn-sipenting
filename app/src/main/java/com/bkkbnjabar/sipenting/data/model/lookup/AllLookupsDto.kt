package com.bkkbnjabar.sipenting.data.model.lookup

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Kelas utama yang membungkus semua data
@JsonClass(generateAdapter = true)
data class AllLookupsResponse(
    @Json(name = "data")
    val data: AllLookupsData?
)

// Kelas yang berisi semua list data
@JsonClass(generateAdapter = true)
data class AllLookupsData(
    @Json(name = "provinsis") val provinsis: List<ProvinsiDto>?,
    @Json(name = "kabupatens") val kabupatens: List<KabupatenDto>?,
    @Json(name = "kecamatans") val kecamatans: List<KecamatanDto>?,
    @Json(name = "kelurahans") val kelurahans: List<KelurahanDto>?,
    @Json(name = "rws") val rws: List<RwDto>?,
    @Json(name = "rts") val rts: List<RtDto>?,
    @Json(name = "birth_assistants") val birthAssistants: List<LookupItemDto>?,
    @Json(name = "contraception_options") val contraceptionOptions: List<LookupItemDto>?,
    @Json(name = "counseling_types") val counselingTypes: List<LookupItemDto>?,
    @Json(name = "defecation_facilities") val defecationFacilities: List<LookupItemDto>?,
    @Json(name = "delivery_places") val deliveryPlaces: List<LookupItemDto>?,
    @Json(name = "disease_histories") val diseaseHistories: List<LookupItemDto>?,
    @Json(name = "given_birth_statuses") val givenBirthStatuses: List<LookupItemDto>?,
    @Json(name = "immunization_options") val immunizationOptions: List<LookupItemDto>?,
    @Json(name = "main_sources_of_drinking_water") val mainSourcesOfDrinkingWater: List<LookupItemDto>?,
    @Json(name = "postpartum_complication_options") val postpartumComplicationOptions: List<LookupItemDto>?,
    @Json(name = "pregnant_mother_statuses") val pregnantMotherStatuses: List<LookupItemDto>?,
    @Json(name = "social_assistance_facilitation_options") val socialAssistanceFacilitationOptions: List<LookupItemDto>?
)