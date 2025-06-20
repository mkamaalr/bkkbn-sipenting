package com.bkkbnjabar.sipenting.data.model

// Ini adalah model data yang akan digunakan di lapisan domain dan UI.
// Properti 'name' di sini akan sesuai dengan apa yang diharapkan oleh ArrayAdapter.
data class Kecamatan(
    val id: Int,
    val name: String, // Menggunakan 'name' untuk konsistensi di lapisan domain/UI
    val kabupatenId: Int,
    val kabupaten: Kabupaten? // Bisa juga menggunakan Kabupaten domain model di sini
)
