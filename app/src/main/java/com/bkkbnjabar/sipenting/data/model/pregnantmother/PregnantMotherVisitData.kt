package com.bkkbnjabar.sipenting.data.model.pregnantmother

/**
 * Data class yang menampung state dari formulir kunjungan ibu hamil.
 * Digunakan oleh ViewModel untuk menyimpan data kunjungan yang sedang diisi oleh pengguna.
 */
data class PregnantMotherVisitData(
    val localVisitId: Int? = null,
    val pregnantMotherLocalId: Int? = null,
    val visitDate: String? = null,
    val childNumber: Int? = null,
    val dateOfBirthLastChild: String? = null,
    val pregnancyWeekAge: Int? = null,
    val weightTrimester1: Double? = null,
    val currentHeight: Double? = null,
    val currentWeight: Double? = null,
    val isHbChecked: Boolean? = null,
    val hemoglobinLevel: Double? = null,
    val upperArmCircumference: Double? = null,
    val isTwin: Boolean? = null,
    val numberOfTwins: Int? = null,
    val isEstimatedFetalWeightChecked: Boolean? = null,
    val isExposedToCigarettes: Boolean? = null,
    val isCounselingReceived: Boolean? = null,
    val counselingTypeId: Int? = null,
    val isIronTablesReceived: Boolean? = null,
    val isIronTablesTaken: Boolean? = null,
    val facilitatingReferralServiceStatus: String? = null,
    val facilitatingSocialAssistanceStatus: String? = null,
    val nextVisitDate: String? = null,
    val tpkNotes: String? = null,
    val isAlive: Boolean? = null,
    val isGivenBirth: Boolean? = null,
    val givenBirthStatusId: Int? = null,
    val pregnantMotherStatusId: Int? = null,
    val diseaseHistory: List<String>? = null,
    val mainSourceOfDrinkingWater: List<String>? = null,
    val defecationFacility: List<String>? = null,
    val socialAssistanceFacilitationOptions: List<String>? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: String? = null,
    // Menambahkan field yang sebelumnya hilang untuk dropdown
    val deliveryPlaceId: Int? = null,
    val birthAssistantId: Int? = null,
    val contraceptionOptionId: Int? = null,
    val imagePath1: String? = null,
    val imagePath2: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isReceivedMbg: Boolean? = null,
    val isTfuMeasured: Boolean? = null,
    val tfu: Double? = null
)