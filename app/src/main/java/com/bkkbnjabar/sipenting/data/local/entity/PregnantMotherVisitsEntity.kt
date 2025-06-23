package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bkkbnjabar.sipenting.data.model.pregnantmother.SyncStatus

@Entity(
    tableName = "pregnant_mother_visits",
    // Menambahkan Foreign Key ke PregnantMotherEntity
    foreignKeys = [ForeignKey(
        entity = PregnantMotherEntity::class,
        parentColumns = ["localId"], // Kolom di PregnantMotherEntity
        childColumns = ["pregnantMotherLocalId"], // Kolom di PregnantMotherVisitsEntity
        onDelete = ForeignKey.CASCADE // Jika PregnantMother dihapus, kunjungan ikut dihapus
    )],
    // Menambahkan index untuk performa query berdasarkan pregnantMotherLocalId
    indices = [Index(value = ["pregnantMotherLocalId"])]
)
data class PregnantMotherVisitsEntity(
    @PrimaryKey(autoGenerate = true)
    val localVisitId: Int? = null, // ID unik untuk setiap kunjungan
    val pregnantMotherLocalId: Int, // Foreign key ke PregnantMotherEntity
    val visitDate: String?, // Tanggal kunjungan
    val childNumber: Int?, // Anak ke-berapa
    val dateOfBirthLastChild: String?, // Tanggal lahir anak terakhir (jika ada)
    val pregnancyWeekAge: Int?, // Usia kehamilan (minggu)
    val weightTrimester1: Double?, // Berat badan pada trimester 1
    val currentHeight: Double?, // Tinggi badan saat ini
    val currentWeight: Double?, // Berat badan saat ini
    val isHbChecked: Boolean?, // Apakah Hb sudah diperiksa
    val hemoglobinLevel: Double?, // Tingkat hemoglobin
    val upperArmCircumference: Double?, // Lingkar lengan atas
    val isTwin: Boolean?, // Apakah kembar
    val numberOfTwins: Int?, // Jumlah kembar
    val isEstimatedFetalWeightChecked: Boolean?, // Apakah taksiran berat janin sudah diperiksa
    val isExposedToCigarettes: Boolean?, // Apakah terpapar rokok
    val isCounselingReceived: Boolean?, // Apakah sudah menerima konseling
    val counselingTypeId: Int?, // ID jenis konseling (akan diperlakukan sebagai single ID untuk saat ini)
    val isIronTablesReceived: Boolean?, // Apakah sudah menerima tablet zat besi
    val isIronTablesTaken: Boolean?, // Apakah tablet zat besi sudah diminum
    val facilitatingReferralServiceStatus: String?, // Status fasilitas layanan rujukan (string untuk saat ini)
    val facilitatingSocialAssistanceStatus: String?, // Status fasilitas bantuan sosial (string untuk saat ini)
    val nextVisitDate: String?, // Tanggal kunjungan berikutnya
    val tpkNotes: String?, // Catatan TPK
    val isAlive: Boolean?, // Apakah ibu masih hidup
    val isGivenBirth: Boolean?, // Apakah sudah melahirkan
    val givenBirthStatusId: Int?, // ID status melahirkan
    val pregnantMotherStatusId: Int?, // ID status ibu hamil
    // Bidang untuk data multiple, disimpan sebagai JSON string di database
    val diseaseHistory: String?,
    val mainSourceOfDrinkingWater: String?,
    val defecationFacility: String?,
    val socialAssistanceFacilitationOptions: String?,
    val createdAt: String?, // Waktu pembuatan record kunjungan
    val syncStatus: SyncStatus // FIXED: Tipe Enum langsung di sini
)
