package com.bkkbnjabar.sipenting.data.model

data class Kelurahan(
    val id: Int,
    val name: String,
    val kecamatanId: Int,
    val kecamatan: Kecamatan? // Nested domain model
)