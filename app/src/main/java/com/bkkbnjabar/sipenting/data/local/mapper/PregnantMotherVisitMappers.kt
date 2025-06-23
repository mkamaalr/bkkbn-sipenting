package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherVisitData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * Fungsi ekstensi untuk mengkonversi PregnantMotherVisitData (model domain/UI)
 * menjadi PregnantMotherVisitsEntity (entitas Room database).
 */
fun PregnantMotherVisitData.toPregnantMotherVisitsEntity(): PregnantMotherVisitsEntity {
    val converter = ListStringConverter()

    return PregnantMotherVisitsEntity(
        localVisitId = this.localVisitId,
        pregnantMotherLocalId = this.pregnantMotherLocalId ?: 0,
        visitDate = this.visitDate,
        childNumber = this.childNumber,
        dateOfBirthLastChild = this.dateOfBirthLastChild,
        pregnancyWeekAge = this.pregnancyWeekAge,
        weightTrimester1 = this.weightTrimester1,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        isHbChecked = this.isHbChecked,
        hemoglobinLevel = this.hemoglobinLevel,
        upperArmCircumference = this.upperArmCircumference,
        isTwin = this.isTwin,
        numberOfTwins = this.numberOfTwins,
        isEstimatedFetalWeightChecked = this.isEstimatedFetalWeightChecked,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isCounselingReceived = this.isCounselingReceived,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived,
        isIronTablesTaken = this.isIronTablesTaken,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        isAlive = this.isAlive,
        isGivenBirth = this.isGivenBirth,
        givenBirthStatusId = this.givenBirthStatusId,
        pregnantMotherStatusId = this.pregnantMotherStatusId,
        diseaseHistory = converter.fromStringList(this.diseaseHistory),
        mainSourceOfDrinkingWater = converter.fromStringList(this.mainSourceOfDrinkingWater),
        defecationFacility = converter.fromStringList(this.defecationFacility),
        socialAssistanceFacilitationOptions = converter.fromStringList(this.socialAssistanceFacilitationOptions),
        syncStatus = this.syncStatus, // Enum langsung
        createdAt = this.createdAt ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )
}

/**
 * Fungsi ekstensi untuk mengkonversi PregnantMotherVisitsEntity (entitas Room database)
 * menjadi PregnantMotherVisitData (model domain/UI).
 */
fun PregnantMotherVisitsEntity.toPregnantMotherVisitData(): PregnantMotherVisitData {
    val converter = ListStringConverter()

    return PregnantMotherVisitData(
        localVisitId = this.localVisitId,
        pregnantMotherLocalId = this.pregnantMotherLocalId,
        visitDate = this.visitDate,
        childNumber = this.childNumber,
        dateOfBirthLastChild = this.dateOfBirthLastChild,
        pregnancyWeekAge = this.pregnancyWeekAge,
        weightTrimester1 = this.weightTrimester1,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        isHbChecked = this.isHbChecked,
        hemoglobinLevel = this.hemoglobinLevel,
        upperArmCircumference = this.upperArmCircumference,
        isTwin = this.isTwin,
        numberOfTwins = this.numberOfTwins,
        isEstimatedFetalWeightChecked = this.isEstimatedFetalWeightChecked,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isCounselingReceived = this.isCounselingReceived,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived,
        isIronTablesTaken = this.isIronTablesTaken,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        isAlive = this.isAlive,
        isGivenBirth = this.isGivenBirth,
        givenBirthStatusId = this.givenBirthStatusId,
        pregnantMotherStatusId = this.pregnantMotherStatusId,
        diseaseHistory = converter.toStringList(this.diseaseHistory),
        mainSourceOfDrinkingWater = converter.toStringList(this.mainSourceOfDrinkingWater),
        defecationFacility = converter.toStringList(this.defecationFacility),
        socialAssistanceFacilitationOptions = converter.toStringList(this.socialAssistanceFacilitationOptions),
        syncStatus = this.syncStatus, // Enum langsung
        createdAt = this.createdAt
    )
}

fun PregnantMotherVisitData.toPregnantMotherVisitEntity(): PregnantMotherVisitsEntity {
    // Pastikan pregnantMotherLocalId tidak null saat mengkonversi ke Entity
    val motherId = this.pregnantMotherLocalId ?: throw IllegalStateException("Pregnant mother local ID must not be null for visit entity.")
    // Pastikan SyncStatus tidak null, berikan default jika perlu
    val defaultSyncStatus = SyncStatus.PENDING_UPLOAD
    return PregnantMotherVisitsEntity(
        localVisitId = this.localVisitId, // Biarkan null jika autoGenerate
        pregnantMotherLocalId = motherId,
        visitDate = this.visitDate,
        childNumber = this.childNumber,
        dateOfBirthLastChild = this.dateOfBirthLastChild,
        pregnancyWeekAge = this.pregnancyWeekAge,
        weightTrimester1 = this.weightTrimester1,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        isHbChecked = this.isHbChecked,
        hemoglobinLevel = this.hemoglobinLevel,
        upperArmCircumference = this.upperArmCircumference,
        isTwin = this.isTwin,
        numberOfTwins = this.numberOfTwins,
        isEstimatedFetalWeightChecked = this.isEstimatedFetalWeightChecked,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isCounselingReceived = this.isCounselingReceived,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived,
        isIronTablesTaken = this.isIronTablesTaken,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        isAlive = this.isAlive,
        isGivenBirth = this.isGivenBirth,
        givenBirthStatusId = this.givenBirthStatusId,
        pregnantMotherStatusId = this.pregnantMotherStatusId,
        diseaseHistory = this.diseaseHistory.toJsonString(), // Konversi List<String> ke JSON String
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater.toJsonString(),
        defecationFacility = this.defecationFacility.toJsonString(),
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions.toJsonString(),
        syncStatus = this.syncStatus ?: defaultSyncStatus, // Enum langsung
        createdAt = this.createdAt
    )
}
