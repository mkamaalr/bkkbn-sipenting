package com.bkkbnjabar.sipenting.data.model.pregnantmother

import android.os.Parcelable
import com.bkkbnjabar.sipenting.data.local.entity.SyncStatus // Pastikan SyncStatus diimpor
import kotlinx.parcelize.Parcelize

// Data class ini digunakan untuk menampung data pendaftaran ibu hamil
// saat dikumpulkan di UI sebelum dikirim ke repository atau disimpan secara lokal.
// Ini memungkinkan data dari beberapa langkah form dikonsolidasi.
@Parcelize
data class PregnantMotherRegistrationData(
    val localId: Long? = null, // ID unik di database lokal Room (auto-generated)
    val remoteId: String? = null, // ID unik dari server setelah berhasil diunggah
    val syncStatus: SyncStatus = SyncStatus.PENDING_UPLOAD, // Status sinkronisasi default

    // Bagian 1: Data Dasar Ibu Hamil
    val name: String? = null,
    val nik: String? = null,
    val dateOfBirth: String? = null, // Format: dd/MM/yyyy
    val phoneNumber: String? = null,

    // Bagian 1: Data Lokasi Domisili (dari user/location API atau pemilihan)
    val provinsiId: Int? = null,
    val provinsiName: String? = null,
    val kabupatenId: Int? = null,
    val kabupatenName: String? = null,
    val kecamatanId: Int? = null,
    val kecamatanName: String? = null,
    val kelurahanId: Int? = null,
    val kelurahanName: String? = null,
    val rwId: Int? = null,
    val rwName: String? = null,
    val rtId: Int? = null,
    val rtName: String? = null,

    // Bagian 2: Data Suami dan Alamat
    val husbandName: String? = null,
    val fullAddress: String? = null,

    // Tanggal pendaftaran (misalnya, diisi otomatis saat registrasi)
    val registrationDate: String? = null // Format ISO_LOCAL_DATE: YYYY-MM-DD

    // TODO: Tambahkan properti lain yang relevan untuk proses pendaftaran ibu hamil
    // Contoh:
    // val lastMenstrualPeriod: String? = null,
    // val estimatedDeliveryDate: String? = null,
    // val numberOfPregnancies: Int? = null,
    // val birthAssistantId: Int? = null,
    // val contraceptionOptionId: Int? = null,
    // ...
) : Parcelable
