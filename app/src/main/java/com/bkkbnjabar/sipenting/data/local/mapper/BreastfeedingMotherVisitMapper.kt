package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData

fun BreastfeedingMotherVisitsEntity.toData(): BreastfeedingMotherVisitData {
    return BreastfeedingMotherVisitData(
        localVisitId = this.localVisitId,
        id = this.id,
//        pregnantMotherLocalId = this.pregnantMotherLocalId,
        visitDate = this.visitDate,
//        childNumber = this.childNumber,
//        dateOfBirthLastChild = this.dateOfBirthLastChild,
//        pregnancyWeekAge = this.pregnancyWeekAge,
//        weightTrimester1 = this.weightTrimester1,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
//        isHbChecked = this.isHbChecked,
//        hemoglobinLevel = this.hemoglobinLevel,
//        hemoglobinLevelReason = this.hemoglobinLevelReason,
//        upperArmCircumference = this.upperArmCircumference,
        isTwin = this.isTwin,
//        numberOfTwins = this.numberOfTwins,
//        isEstimatedFetalWeightChecked = this.isEstimatedFetalWeightChecked,
//        tbj = this.tbj,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isCounselingReceived = this.isCounselingReceived,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived,
        isIronTablesTaken = this.isIronTablesTaken,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
//        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
//        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
//        isAlive = this.isAlive,
//        isGivenBirth = this.isGivenBirth,
//        givenBirthStatusId = this.givenBirthStatusId,
//        pregnantMotherStatusId = this.pregnantMotherStatusId,
//        diseaseHistory = this.diseaseHistory,
//        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
//        mainSourceOfDrinkingWaterOther = this.mainSourceOfDrinkingWaterOther,
//        defecationFacility = this.defecationFacility,
//        defecationFacilityOther = this.defecationFacilityOther,
//        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
//        socialAssistanceFacilitationOptionsOther = this.socialAssistanceFacilitationOptionsOther,
//        syncStatus = this.syncStatus,
//        createdAt = this.createdAt.toString(),
//        imagePath1 = this.imagePath1,
//        imagePath2 = this.imagePath2,
//        latitude = this.latitude,
//        longitude = this.longitude,
        isReceivedMbg = this.isReceivedMbg
//        isTfuMeasured = this.isTfuMeasured,
//        tfu = this.tfu
    )
}