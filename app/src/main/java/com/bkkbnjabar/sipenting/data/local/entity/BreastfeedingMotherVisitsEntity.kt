package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(
    tableName = "breastfeeding_mother_visits",
    foreignKeys = [
        ForeignKey(
            entity = BreastfeedingMotherEntity::class,
            parentColumns = ["localId"],
            childColumns = ["breastfeedingMotherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["breastfeedingMotherId"])]
)
data class BreastfeedingMotherVisitsEntity(
    @PrimaryKey(autoGenerate = true)
    val localVisitId: Int = 0,
    val id: String? = null, // Server ID
    val breastfeedingMotherId: Int,
    val visitDate: String?,
    val breastfeedingMotherStatusId: Int?,
    val lastBirthDate: String?,
    val deliveryPlaceId: Int?,
    val birthAssistantId: Int?,
    val modeOfDelivery: String?,
    val isTwin: Boolean?,
    val babyStatus: String?, // e.g., "Hidup", "Meninggal"
    val isPostpartumComplication: Boolean?,
    val postpartumComplication: List<String>?,
    val postpartumComplicationOther: String?,
    val isExposedToCigarettes: Boolean,
    val mainSourceOfDrinkingWater: List<String>?,
    val mainSourceOfDrinkingWaterOther: String?,
    val defecationFacility: List<String>?,
    val defecationFacilityOther: String?,
    val isCounselingReceived: Boolean,
    val counselingTypeId: Int?,
    val isIronTablesReceived: Boolean,
    val isIronTablesTaken: Boolean,
    val isReceivedMbg: Boolean,
    val isAsiExclusive: Boolean,
    val currentHeight: Double?,
    val currentWeight: Double?,
    val facilitatingReferralServiceStatus: String?,
    val facilitatingSocialAssistanceStatus: String?,
    val socialAssistanceFacilitationOptions: List<String>?,
    val socialAssistanceFacilitationOptionsOther: String?,
    val nextVisitDate: String?,
    val tpkNotes: String?,
    val imagePath1: String?,
    val imagePath2: String?,
    val latitude: Double?,
    val longitude: Double?,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val onContraception: Boolean,
    val contraceptionTypeId: Int?,
    val contraceptionReasonForUse: String?,
    val contraceptionRTypeId: Int?
)