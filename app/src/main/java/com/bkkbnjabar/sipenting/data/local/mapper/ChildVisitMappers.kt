package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildVisitsEntity
import com.bkkbnjabar.sipenting.data.model.child.ChildData
import com.bkkbnjabar.sipenting.data.model.child.ChildRegistrationData
import com.bkkbnjabar.sipenting.data.model.child.ChildMotherData
import com.bkkbnjabar.sipenting.data.model.child.ChildVisitData
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

// --- Child Mappers ---
//
//fun ChildEntity.toDomain(): ChildRegistrationData {
//    return ChildRegistrationData(
//        localId = this.localId,
//        motherId = this.motherId,
//        name = this.name,
//        nik = this.nik,
//        dateOfBirth = this.dateOfBirth,
//        phoneNumber = this.phoneNumber,
//        provinsiName = this.provinsiName,
//        provinsiId = this.provinsiId,
//        kabupatenName = this.kabupatenName,
//        kabupatenId = this.kabupatenId,
//        kecamatanName = this.kecamatanName,
//        kecamatanId = this.kecamatanId,
//        kelurahanName = this.kelurahanName,
//        kelurahanId = this.kelurahanId,
//        rwName = this.rwName,
//        rwId = this.rwId,
//        rtName = this.rtName,
//        rtId = this.rtId,
//        fullAddress = this.fullAddress,
//        syncStatus = this.syncStatus,
//    )
//}


fun ChildEntity.toDomain(): ChildData {
    return ChildData(
        localId = this.localId,
        motherId = this.motherId,
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
    )
}

// --- Child's Mother Mappers ---

fun ChildMotherEntity.toDomain(): ChildMotherData {
    return ChildMotherData(
        localId = this.localId,
        id = this.id,
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
        createdAt = this.createdAt
    )
}

fun ChildMotherData.toEntity(): ChildMotherEntity {
    return ChildMotherEntity(
        localId = this.localId,
        id = this.id,
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
        syncStatus = this.syncStatus ?: SyncStatus.PENDING,
        createdAt = this.createdAt ?: System.currentTimeMillis()
    )
}

fun ChildVisitData.toEntity(): ChildVisitsEntity {
    return ChildVisitsEntity(
        localVisitId = this.localVisitId,
        id = this.id,
        childId = this.childId ?: 0,
        visitDate = this.visitDate,
        pregnancyAgeWhenChildbirth = this.pregnancyAgeWhenChildbirth,
        weightBirth = this.weightBirth,
        heightBirth = this.heightBirth,
        isAsiExclusive = this.isAsiExclusive,
        onContraception = this.onContraception ?: false,
        contraceptionTypeId = this.contraceptionTypeId,
        contraceptionReasonForUse = this.contraceptionReasonForUse,
        contraceptionRTypeId = this.contraceptionRTypeId,
        measurementDate = this.measurementDate,
        weightMeasurement = this.weightMeasurement,
        heightMeasurement = this.heightMeasurement,
        isOngoingAsi = this.isOngoingAsi,
        isMpasi = this.isMpasi,
        isKkaFilled = this.isKkaFilled,
        kkaResult = this.kkaResult,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isPosyanduMonth = this.isPosyanduMonth,
        isBkbMonth = this.isBkbMonth,
        isCounselingReceived = this.isCounselingReceived,
        isReceivedMbg = this.isReceivedMbg,
        headCircumference = this.headCircumference,
        counselingTypeId = this.counselingTypeId,
        immunizationsGiven = this.immunizationsGiven,
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
        mainSourceOfDrinkingWaterOther = this.mainSourceOfDrinkingWaterOther,
        defecationFacility = this.defecationFacility,
        defecationFacilityOther = this.defecationFacilityOther,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
        socialAssistanceFacilitationOptionsOther = this.socialAssistanceFacilitationOptionsOther,
        syncStatus = this.syncStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        imagePath1 = this.imagePath1,
        imagePath2 = this.imagePath2,
        latitude = this.latitude,
        longitude = this.longitude,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        pregnantMotherStatusId = this.pregnantMotherStatusId
    )
}

/**
 * Converts a [ChildVisitsEntity] from the local database into a [ChildVisitData]
 * domain model, which is safer to use in the UI and business logic layers.
 */
fun ChildVisitsEntity.toDomain(): ChildVisitData {
    return ChildVisitData(
        localVisitId = this.localVisitId,
        id = this.id,
        childId = this.childId,
        visitDate = this.visitDate,
        pregnancyAgeWhenChildbirth = this.pregnancyAgeWhenChildbirth,
        weightBirth = this.weightBirth,
        heightBirth = this.heightBirth,
        isAsiExclusive = this.isAsiExclusive,
        onContraception = this.onContraception,
        contraceptionTypeId = this.contraceptionTypeId,
        contraceptionReasonForUse = this.contraceptionReasonForUse,
        contraceptionRTypeId = this.contraceptionRTypeId,
        measurementDate = this.measurementDate,
        weightMeasurement = this.weightMeasurement,
        heightMeasurement = this.heightMeasurement,
        isOngoingAsi = this.isOngoingAsi,
        isMpasi = this.isMpasi,
        isKkaFilled = this.isKkaFilled,
        kkaResult = this.kkaResult,
        isExposedToCigarettes = this.isExposedToCigarettes,
        isPosyanduMonth = this.isPosyanduMonth,
        isBkbMonth = this.isBkbMonth,
        isCounselingReceived = this.isCounselingReceived,
        isReceivedMbg = this.isReceivedMbg,
        headCircumference = this.headCircumference,
        counselingTypeId = this.counselingTypeId,
        immunizationsGiven = this.immunizationsGiven,
        mainSourceOfDrinkingWater = this.mainSourceOfDrinkingWater,
        mainSourceOfDrinkingWaterOther = this.mainSourceOfDrinkingWaterOther,
        defecationFacility = this.defecationFacility,
        defecationFacilityOther = this.defecationFacilityOther,
        facilitatingReferralServiceStatus = this.facilitatingReferralServiceStatus,
        facilitatingSocialAssistanceStatus = this.facilitatingSocialAssistanceStatus,
        socialAssistanceFacilitationOptions = this.socialAssistanceFacilitationOptions,
        socialAssistanceFacilitationOptionsOther = this.socialAssistanceFacilitationOptionsOther,
        syncStatus = this.syncStatus,
        nextVisitDate = this.nextVisitDate,
        tpkNotes = this.tpkNotes,
        imagePath1 = this.imagePath1,
        imagePath2 = this.imagePath2,
        latitude = this.latitude,
        longitude = this.longitude,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        pregnantMotherStatusId = this.pregnantMotherStatusId
    )
}