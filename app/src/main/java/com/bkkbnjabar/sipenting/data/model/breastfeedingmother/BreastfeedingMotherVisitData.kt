package com.bkkbnjabar.sipenting.data.model.breastfeedingmother

/**
 * Data class that holds the state for the Breastfeeding Mother's visit form.
 * This has been adjusted to match the latest database entity and UI logic.
 */
data class BreastfeedingMotherVisitData(
    val localVisitId: Int? = null,
    val id: String? = null,
    val breastfeedingMotherId: Int? = null,
    val breastfeedingMotherStatusId: Int? = null,
    val visitDate: String? = null,
    val lastBirthDate: String? = null,
    val deliveryPlaceId: Int? = null,
    val birthAssistantId: Int? = null,
    val modeOfDelivery: String? = null,
    val isTwin: Boolean? = null,
    val babyStatus: String? = null,
    val isPostpartumComplication: Boolean? = null,
    val postpartumComplication: List<String>? = null,
    val postpartumComplicationOther: String? = null,
    val isExposedToCigarettes: Boolean? = null,
    val mainSourceOfDrinkingWater: List<String>? = null,
    val mainSourceOfDrinkingWaterOther: String? = null,
    val defecationFacility: List<String>? = null,
    val defecationFacilityOther: String? = null,
    val isCounselingReceived: Boolean? = null,
    val counselingTypeId: Int? = null,
    val isIronTablesReceived: Boolean? = null,
    val isIronTablesTaken: Boolean? = null,
    val isReceivedMbg: Boolean? = null,
    val isAsiExclusive: Boolean? = null,
    val currentHeight: Double? = null,
    val currentWeight: Double? = null,
    val facilitatingReferralServiceStatus: String? = null,
    val facilitatingSocialAssistanceStatus: String? = null,
    val socialAssistanceFacilitationOptions: List<String>? = null,
    val socialAssistanceFacilitationOptionsOther: String? = null,
    val nextVisitDate: String? = null,
    val tpkNotes: String? = null,
    val imagePath1: String? = null,
    val imagePath2: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long? = null,

    // --- ADJUSTED CONTRACEPTION FIELDS ---

    // This field corresponds to the main "Yes/No" radio button.
    val onContraception: Boolean? = null,

    // If 'onContraception' is TRUE, this stores the ID of the selected contraception type.
    val contraceptionTypeId: Int? = null,

    // If 'contraceptionTypeId' corresponds to "Lainnya", this field stores the text.
    val contraceptionOther: String? = null,

    // If 'onContraception' is TRUE, this stores the selection from the reason dropdown.
    val contraceptionReasonForUse: String? = null,

    // If 'onContraception' is FALSE, this stores the ID of the selected rejection reason.
    val contraceptionRejectionReasonId: Int? = null
)