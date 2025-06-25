package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

/**
 * Represents the "pregnant_mother_visits" table.
 * Each row is a visit record linked to a mother in the pregnant_mother table.
 */
@Entity(
    tableName = "pregnant_mother_visits",
    foreignKeys = [
        ForeignKey(
            entity = PregnantMotherEntity::class,
            parentColumns = ["localId"],
            childColumns = ["pregnantMotherLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pregnantMotherLocalId"])]
)
data class PregnantMotherVisitsEntity(
    @PrimaryKey(autoGenerate = true)
    val localVisitId: Int = 0,
    val id: String? = null, // ID from server after sync
    val pregnantMotherLocalId: Int,
    val visitDate: String,
    val childNumber: Int?,
    val dateOfBirthLastChild: String?,
    val pregnancyWeekAge: Int?,
    val weightTrimester1: Double?,
    val currentHeight: Double?,
    val currentWeight: Double?,
    val isHbChecked: Boolean,
    val hemoglobinLevel: Double?,
    val upperArmCircumference: Double?,
    val isTwin: Boolean,
    val numberOfTwins: Int?,
    val isEstimatedFetalWeightChecked: Boolean,
    val isExposedToCigarettes: Boolean,
    val isCounselingReceived: Boolean,
    val counselingTypeId: Int?,
    val isIronTablesReceived: Boolean,
    val isIronTablesTaken: Boolean,
    val facilitatingReferralServiceStatus: String?,
    val facilitatingSocialAssistanceStatus: String?,
    val nextVisitDate: String?,
    val tpkNotes: String?,
    val isAlive: Boolean,
    val isGivenBirth: Boolean,
    val givenBirthStatusId: Int?,
    val pregnantMotherStatusId: Int?,
    val diseaseHistory: List<String>?,
    val mainSourceOfDrinkingWater: List<String>?,
    val defecationFacility: List<String>?,
    val socialAssistanceFacilitationOptions: List<String>?,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
