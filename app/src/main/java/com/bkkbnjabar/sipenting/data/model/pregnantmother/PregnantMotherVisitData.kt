package com.bkkbnjabar.sipenting.data.model.pregnantmother

import android.os.Parcelable
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
data class PregnantMotherVisitData(
    val localVisitId: Int? = null, // ID lokal untuk kunjungan (Room Primary Key)
    val pregnantMotherLocalId: Int? = null, // Foreign Key ke Ibu Hamil
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
    val counselingTypeId: Int? = null, // Ini mungkin lookup ID
    val isIronTablesReceived: Boolean? = null,
    val isIronTablesTaken: Boolean? = null,
    val facilitatingReferralServiceStatus: String? = null, // Ini mungkin lookup String
    val facilitatingSocialAssistanceStatus: String? = null, // Ini mungkin lookup String
    val nextVisitDate: String? = null,
    val tpkNotes: String? = null,
    val isAlive: Boolean? = null,
    val isGivenBirth: Boolean? = null,
    val givenBirthStatusId: Int? = null, // Ini mungkin lookup ID
    val pregnantMotherStatusId: Int? = null, // Ini mungkin lookup ID
    val diseaseHistory: List<String>? = null, // List of selected strings (checkboxes)
    val mainSourceOfDrinkingWater: List<String>? = null,
    val defecationFacility: List<String>? = null,
    val socialAssistanceFacilitationOptions: List<String>? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD, // Sync status untuk kunjungan
    val createdAt: String? = null
) : Parcelable
