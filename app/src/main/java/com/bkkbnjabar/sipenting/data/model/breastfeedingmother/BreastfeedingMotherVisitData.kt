package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

/**
 * Data class that holds the state for the Breastfeeding Mother's visit form (page 2).
 */
data class BreastfeedingMotherVisitData(
    val localVisitId: Int? = null,
    val id: Int? = null,
    val breastfeedingMotherId: Int? = null,
    val visitDate: String? = null,
    val lastBirthDate: String? = null,
    val deliveryPlaceId: Int? = null,
    val birthAssistantId: Int? = null,
    val contraceptionOptionId: Int? = null,
    val modeOfDelivery: String? = null,
    val isTwin: Boolean? = null,
    val babyStatus: String? = null,
    val isPostpartumComplication: Boolean? = null,
    val onContraception: Boolean? = null,
    val isExposedToCigarettes: Boolean? = null,
    val isCounselingReceived: Boolean? = null,
    val counselingTypeId: Int? = null,
    val isIronTablesReceived: Boolean? = null,
    val isIronTablesTaken: Boolean? = null,
    val isReceivedMbg: Boolean? = null,
    val isAsiExclusive: Boolean? = null,
    val currentHeight: Double? = null,
    val currentWeight: Double? = null,
    val facilitatingReferralServiceStatus: String? = null,
    val tpkNotes: String? = null
)