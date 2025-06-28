package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val id: Int = 0,
    val breastfeedingMotherId: Int,
    val visitDate: String,
    val lastBirthDate: String,
    val deliveryPlaceId: Int?, // MOVED HERE
    val birthAssistantId: Int?, // MOVED HERE
    val contraceptionOptionId: Int?, // MOVED HERE
    val modeOfDelivery: String?,
    val isTwin: Boolean,
    val babyStatus: String?, // e.g., "Hidup", "Meninggal"
    val isPostpartumComplication: Boolean,
    val onContraception: Boolean,
    val isExposedToCigarettes: Boolean,
    val isCounselingReceived: Boolean,
    val counselingTypeId: Int?,
    val isIronTablesReceived: Boolean,
    val isIronTablesTaken: Boolean,
    val isReceivedMbg: Boolean,
    val isAsiExclusive: Boolean,
    val currentHeight: Double?,
    val currentWeight: Double?,
    val facilitatingReferralServiceStatus: String?,
    val tpkNotes: String?,
    val createdAt: Long = System.currentTimeMillis()
)