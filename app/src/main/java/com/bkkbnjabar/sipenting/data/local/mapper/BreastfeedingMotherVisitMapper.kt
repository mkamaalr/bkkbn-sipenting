package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData

/**
 * Maps the database Entity to the Data class used by the ViewModel and UI.
 */
fun BreastfeedingMotherVisitsEntity.toData(): BreastfeedingMotherVisitData {
    return BreastfeedingMotherVisitData(
        localVisitId = this.localVisitId,
        id = this.id,
        breastfeedingMotherId = this.breastfeedingMotherId,
        breastfeedingMotherStatusId = this.breastfeedingMotherStatusId,
        visitDate = this.visitDate,
        lastBirthDate = this.lastBirthDate,
        deliveryPlaceId = this.deliveryPlaceId,
        birthAssistantId = this.birthAssistantId,
        modeOfDelivery = this.modeOfDelivery,
        isTwin = this.isTwin,
        babyStatus = this.babyStatus,
        isPostpartumComplication = this.isPostpartumComplication,
        postpartumComplication = this.postpartumComplication,
        postpartumComplicationOther = this.postpartumComplicationOther,
        isExposedToCigarettes = this.isExposedToCigarettes,
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
        mainSourceOfDrinkingWaterOther = this.mainSourceOfDrinkingWaterOther,
        defecationFacility = this.defecationFacility,
        defecationFacilityOther = this.defecationFacilityOther,
        isCounselingReceived = this.isCounselingReceived,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived,
        isIronTablesTaken = this.isIronTablesTaken,
        isReceivedMbg = this.isReceivedMbg,
        isAsiExclusive = this.isAsiExclusive,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
        socialAssistanceFacilitationOptionsOther = this.socialAssistanceFacilitationOptionsOther,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        imagePath1 = this.imagePath1,
        imagePath2 = this.imagePath2,
        latitude = this.latitude,
        longitude = this.longitude,
        createdAt = this.createdAt,

        // --- CORRECTED CONTRACEPTION MAPPING ---
        onContraception = this.onContraception,
        contraceptionTypeId = this.contraceptionTypeId,
        contraceptionReasonForUse = this.contraceptionReasonForUse,
        contraceptionRejectionReasonId = this.contraceptionRTypeId
    )
}

/**
 * Maps the Data class from the UI/ViewModel to the database Entity.
 */
fun BreastfeedingMotherVisitData.toEntity(): BreastfeedingMotherVisitsEntity {
    return BreastfeedingMotherVisitsEntity(
        localVisitId = this.localVisitId ?: 0,
        id = this.id ?: 0,
        breastfeedingMotherId = this.breastfeedingMotherId ?: 0,
        breastfeedingMotherStatusId = this.breastfeedingMotherStatusId,
        visitDate = this.visitDate ?: "",
        lastBirthDate = this.lastBirthDate ?: "",
        deliveryPlaceId = this.deliveryPlaceId,
        birthAssistantId = this.birthAssistantId,
        modeOfDelivery = this.modeOfDelivery,
        isTwin = this.isTwin ?: false,
        babyStatus = this.babyStatus,
        isPostpartumComplication = this.isPostpartumComplication ?: false,
        postpartumComplication = this.postpartumComplication,
        postpartumComplicationOther = this.postpartumComplicationOther,
        isExposedToCigarettes = this.isExposedToCigarettes ?: false,
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
        mainSourceOfDrinkingWaterOther = this.mainSourceOfDrinkingWaterOther,
        defecationFacility = this.defecationFacility,
        defecationFacilityOther = this.defecationFacilityOther,
        isCounselingReceived = this.isCounselingReceived ?: false,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived ?: false,
        isIronTablesTaken = this.isIronTablesTaken ?: false,
        isReceivedMbg = this.isReceivedMbg ?: false,
        isAsiExclusive = this.isAsiExclusive ?: false,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
        socialAssistanceFacilitationOptionsOther = this.socialAssistanceFacilitationOptionsOther,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        imagePath1 = this.imagePath1,
        imagePath2 = this.imagePath2,
        latitude = this.latitude,
        longitude = this.longitude,
        createdAt = this.createdAt ?: System.currentTimeMillis(),

        // --- CORRECTED CONTRACEPTION MAPPING ---
        onContraception = this.onContraception ?: false,
        contraceptionTypeId = this.contraceptionTypeId,
        contraceptionReasonForUse = this.contraceptionReasonForUse,
        contraceptionRTypeId = this.contraceptionRejectionReasonId
    )
}