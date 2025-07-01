package com.bkkbnjabar.sipenting.data.model.child

import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

// First, you need the corresponding Data class.
// This should be in its own file like `data/model/child/ChildVisitData.kt`
// but I am including it here for completeness.
data class ChildVisitData(
    val localVisitId: Int = 0,
    val id: String? = null,
    val childId: Int? = null,
    val visitDate: String? = null,
    val pregnancyAgeWhenChildbirth: String? = null,
    val weightBirth: Double? = null,
    val heightBirth: Double? = null,
    val isAsiExclusive: Boolean? = null,
    val onContraception: Boolean? = null,
    val contraceptionTypeId: Int? = null,
    val contraceptionReasonForUse: String? = null,
    val contraceptionRTypeId: Int? = null,
    val measurementDate: String? = null,
    val weightMeasurement: Double? = null,
    val heightMeasurement: Double? = null,
    val isOngoingAsi: Boolean? = null,
    val isMpasi: Boolean? = null,
    val isKkaFilled: Boolean? = null,
    val kkaResult: String? = null,
    val isExposedToCigarettes: Boolean? = null,
    val isPosyanduMonth: Boolean? = null,
    val isBkbMonth: Boolean? = null,
    val isCounselingReceived: Boolean? = null,
    val isReceivedMbg: Boolean? = null,
    val headCircumference: Double? = null,
    val counselingTypeId: Int? = null,
    val immunizationsGiven: List<String>? = null,
    val mainSourceOfDrinkingWater: List<String>? = null,
    val mainSourceOfDrinkingWaterOther: String? = null,
    val defecationFacility: List<String>? = null,
    val defecationFacilityOther: String? = null,
    val facilitatingReferralServiceStatus: String? = null,
    val facilitatingSocialAssistanceStatus: String? = null,
    val socialAssistanceFacilitationOptions: List<String>? = null,
    val socialAssistanceFacilitationOptionsOther: String? = null,
    val pregnantMotherStatusId: Int? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val nextVisitDate: String? = null,
    val tpkNotes: String? = null,
    val imagePath1: String? = null,
    val imagePath2: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null
)