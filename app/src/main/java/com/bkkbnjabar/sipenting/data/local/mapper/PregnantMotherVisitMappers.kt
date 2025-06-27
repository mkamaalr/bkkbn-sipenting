package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData

fun PregnantMotherVisitData.toEntity(): PregnantMotherVisitsEntity {
    return PregnantMotherVisitsEntity(
        localVisitId = this.localVisitId ?: 0,
        pregnantMotherLocalId = this.pregnantMotherLocalId ?: 0,
        visitDate = this.visitDate ?: "",
        childNumber = this.childNumber,
        dateOfBirthLastChild = this.dateOfBirthLastChild,
        pregnancyWeekAge = this.pregnancyWeekAge,
        weightTrimester1 = this.weightTrimester1,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        isHbChecked = this.isHbChecked ?: false,
        hemoglobinLevel = this.hemoglobinLevel,
        hemoglobinLevelReason = this.hemoglobinLevelReason, // ADDED
        upperArmCircumference = this.upperArmCircumference,
        isTwin = this.isTwin ?: false,
        numberOfTwins = this.numberOfTwins,
        isEstimatedFetalWeightChecked = this.isEstimatedFetalWeightChecked ?: false,
        tbj = this.tbj, // ADDED
        isExposedToCigarettes = this.isExposedToCigarettes ?: false,
        isCounselingReceived = this.isCounselingReceived ?: false,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived ?: false,
        isIronTablesTaken = this.isIronTablesTaken ?: false,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        isAlive = this.isAlive ?: true,
        isGivenBirth = this.isGivenBirth ?: false,
        givenBirthStatusId = this.givenBirthStatusId,
        pregnantMotherStatusId = this.pregnantMotherStatusId,
        diseaseHistory = this.diseaseHistory,
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
        defecationFacility = this.defecationFacility,
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
        syncStatus = this.syncStatus,
        deliveryPlaceId = this.deliveryPlaceId,
        birthAssistantId = this.birthAssistantId,
        contraceptionOptionId = this.contraceptionOptionId,
        imagePath1 = this.imagePath1,
        imagePath2 = this.imagePath2,
        latitude = this.latitude,
        longitude = this.longitude,
        isReceivedMbg = this.isReceivedMbg ?: false,
        isTfuMeasured = this.isTfuMeasured ?: false,
        tfu = this.tfu
    )
}
