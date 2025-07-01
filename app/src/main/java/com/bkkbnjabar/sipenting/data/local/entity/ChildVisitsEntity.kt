package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(
    tableName = "child_visits",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["localId"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["childId"])]
)
data class ChildVisitsEntity(
    @PrimaryKey(autoGenerate = true)
    val localVisitId: Int = 0,

    val id: String? = null, // Server ID
    val childId: Int, // Foreign key to ChildEntity's localId

    val visitDate: String?,
    val pregnancyAgeWhenChildbirth: String?,
    val weightBirth: Double?,
    val heightBirth: Double?,
    val isAsiExclusive: Boolean?,
    val onContraception: Boolean,
    val contraceptionTypeId: Int?,
    val contraceptionReasonForUse: String?,
    val contraceptionRTypeId: Int?,
    val measurementDate: String?,
    val weightMeasurement: Double?,
    val heightMeasurement: Double?,
    val isOngoingAsi: Boolean?,
    val isMpasi: Boolean?,
    val isKkaFilled: Boolean?,
    val kkaResult: String?,
    val isExposedToCigarettes: Boolean?,
    val isPosyanduMonth: Boolean?,
    val isBkbMonth: Boolean?,
    val isCounselingReceived: Boolean?,
    val isReceivedMbg: Boolean?,
    val headCircumference: Double?,
    val counselingTypeId: Int?,
    val immunizationsGiven: List<String>?,
    val mainSourceOfDrinkingWater: List<String>?,
    val mainSourceOfDrinkingWaterOther: String?,
    val defecationFacility: List<String>?,
    val defecationFacilityOther: String?,
    val facilitatingReferralServiceStatus: String?,
    val facilitatingSocialAssistanceStatus: String?,
    val socialAssistanceFacilitationOptions: List<String>?,
    val socialAssistanceFacilitationOptionsOther: String?,
    val pregnantMotherStatusId: Int?,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val nextVisitDate: String?,
    val tpkNotes: String?,
    val imagePath1: String?,
    val imagePath2: String?,
    val latitude: Double?,
    val longitude: Double?,
    val createdAt: Long?,
    val updatedAt: Long?
)
