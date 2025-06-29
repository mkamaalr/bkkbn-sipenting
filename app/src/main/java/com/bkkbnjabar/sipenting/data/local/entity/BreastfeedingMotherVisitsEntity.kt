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
    val breastfeedingMotherStatusId: Int?,
    val lastBirthDate: String,
    val deliveryPlaceId: Int?,
    val birthAssistantId: Int?,
    val modeOfDelivery: String?,
    val isTwin: Boolean,
    val babyStatus: String?, // e.g., "Hidup", "Meninggal"
    val isPostpartumComplication: Boolean,
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
    val createdAt: Long = System.currentTimeMillis(),

    // --- ADJUSTED CONTRACEPTION FIELDS ---

    // This field corresponds to the main "Yes/No" radio button.
    val onContraception: Boolean,

    // If 'onContraception' is TRUE, this stores the ID of the selected contraception type (e.g., IUD, Pil).
    // This was changed from List<String> to a single Int? to match the dropdown menu.
    val contraceptionTypeId: Int?,

    // If 'onContraception' is TRUE, this stores the selection from the reason dropdown
    // (e.g., "Ingin Anak di Tunda", "Tidak Ingin Anak Lagi"). Stored as a String.
    val contraceptionReasonForUse: String?,

    // If 'onContraception' is FALSE, this stores the ID of the selected rejection reason.
    val contraceptionRTypeId: Int?
)