package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherVisitsEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherRegistrationData
import com.bkkbnjabar.sipenting.data.model.breastfeedingmother.BreastfeedingMotherVisitData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.PregnantMotherRegistrationData

fun BreastfeedingMotherRegistrationData.toEntity(): BreastfeedingMotherEntity {
    return BreastfeedingMotherEntity(
        localId = this.localId ?: 0,
        name = this.name ?: "",
        nik = this.nik ?: "",
        dateOfBirth = this.dateOfBirth ?: "",
        phoneNumber = this.phoneNumber,
        provinsiName = this.provinsiName ?: "",
        provinsiId = this.provinsiId ?: 0,
        kabupatenName = this.kabupatenName ?: "",
        kabupatenId = this.kabupatenId ?: 0,
        kecamatanName = this.kecamatanName ?: "",
        kecamatanId = this.kecamatanId ?: 0,
        kelurahanName = this.kelurahanName ?: "",
        kelurahanId = this.kelurahanId ?: 0,
        rwName = this.rwName ?: "",
        rwId = this.rwId ?: 0,
        rtName = this.rtName ?: "",
        rtId = this.rtId ?: 0,
        fullAddress = this.fullAddress ?: "",
        syncStatus = this.syncStatus
    )
}

fun BreastfeedingMotherVisitData.toEntity(): BreastfeedingMotherVisitsEntity {
    return BreastfeedingMotherVisitsEntity(
        id = this.id ?: 0,
        breastfeedingMotherId = this.breastfeedingMotherId ?: 0,
        visitDate = this.visitDate ?: "",
        lastBirthDate = this.lastBirthDate ?: "",
        deliveryPlaceId = this.deliveryPlaceId,
        birthAssistantId = this.birthAssistantId,
        contraceptionOptionId = this.contraceptionOptionId,
        modeOfDelivery = this.modeOfDelivery,
        isTwin = this.isTwin ?: false,
        babyStatus = this.babyStatus,
        isPostpartumComplication = this.isPostpartumComplication ?: false,
        onContraception = this.onContraception ?: false,
        isExposedToCigarettes = this.isExposedToCigarettes ?: false,
        isCounselingReceived = this.isCounselingReceived ?: false,
        counselingTypeId = this.counselingTypeId,
        isIronTablesReceived = this.isIronTablesReceived ?: false,
        isIronTablesTaken = this.isIronTablesTaken ?: false,
        isReceivedMbg = this.isReceivedMbg ?: false,
        isAsiExclusive = this.isAsiExclusive ?: false,
        currentHeight = this.currentHeight,
        currentWeight = this.currentWeight,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        tpkNotes = this.tpkNotes
    )
}

/**
 * ADDED: Converts a database entity object into a registration form data object.
 * This is useful when editing or adding a new visit to an existing mother.
 */
fun BreastfeedingMotherEntity.toRegistrationData(): BreastfeedingMotherRegistrationData {
    return BreastfeedingMotherRegistrationData(
        localId = this.localId,
        name = this.name,
        nik = this.nik,
        dateOfBirth = this.dateOfBirth,
        phoneNumber = this.phoneNumber,
        provinsiName = this.provinsiName,
        provinsiId = this.provinsiId,
        kabupatenName = this.kabupatenName,
        kabupatenId = this.kabupatenId,
        kecamatanName = this.kecamatanName,
        kecamatanId = this.kecamatanId,
        kelurahanName = this.kelurahanName,
        kelurahanId = this.kelurahanId,
        rwName = this.rwName,
        rwId = this.rwId,
        rtName = this.rtName,
        rtId = this.rtId,
        fullAddress = this.fullAddress,
        syncStatus = this.syncStatus,
        createdAt = this.createdAt.toString()
    )
}