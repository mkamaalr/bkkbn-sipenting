package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

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
    val id: String? = null,
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
    val hemoglobinLevelReason: String?, // ADDED
    val upperArmCircumference: Double?,
    val isTwin: Boolean,
    val numberOfTwins: Int?,
    val isEstimatedFetalWeightChecked: Boolean,
    val tbj: Double?, // ADDED: Taksiran Berat Janin in grams
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
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryPlaceId: Int?,
    val birthAssistantId: Int?,
    val contraceptionOptionId: Int?,
    val imagePath1: String?,
    val imagePath2: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isReceivedMbg: Boolean,
    val isTfuMeasured: Boolean?,
    val tfu: Double?
)
