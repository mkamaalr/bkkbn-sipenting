package com.bkkbnjabar.sipenting.domain.model

// Model domain ini akan menampung detail lokasi yang akan digunakan untuk pre-fill UI
data class LocationDetails(
    val provinsiId: String?,
    val provinsiName: String?,
    val kabupatenId: String?,
    val kabupatenName: String?,
    val kecamatanId: String?,
    val kecamatanName: String?,
    val kelurahanId: String?,
    val kelurahanName: String?
) {
}