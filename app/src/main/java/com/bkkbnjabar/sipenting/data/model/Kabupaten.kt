package com.bkkbnjabar.sipenting.data.model

// Model domain untuk Kabupaten
data class Kabupaten(
    val id: Int,
    val name: String,
    val provinsiId: Int,
    val provinsi: Provinsi? // Nested domain model
)
