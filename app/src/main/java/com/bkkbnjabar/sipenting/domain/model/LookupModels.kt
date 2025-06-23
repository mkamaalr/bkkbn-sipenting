package com.bkkbnjabar.sipenting.domain.model

// Contoh model untuk data lookup lokasi
data class Provinsi(val id: Int, val name: String)
data class Kabupaten(val id: Int, val name: String, val provinsiId: Int)
data class Kecamatan(val id: Int, val name: String, val kabupatenId: Int)
data class Kelurahan(val id: Int, val name: String, val kecamatanId: Int)
data class Rw(val id: Int, val name: String, val kelurahanId: Int)
data class Rt(val id: Int, val name: String, val rwId: Int)

// Contoh model untuk item lookup umum (misalnya: jenis konseling, riwayat penyakit, dll.)
data class LookupItemDto(val id: Int, val name: String)
