package com.bkkbnjabar.sipenting.data.remote.mapper

import com.bkkbnjabar.sipenting.data.model.lookup.KabupatenDto
import com.bkkbnjabar.sipenting.data.model.lookup.KecamatanDto
import com.bkkbnjabar.sipenting.data.model.lookup.KelurahanDto
import com.bkkbnjabar.sipenting.data.model.lookup.ProvinsiDto
import com.bkkbnjabar.sipenting.data.model.lookup.RwDto
import com.bkkbnjabar.sipenting.data.model.lookup.RtDto
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.domain.model.Rt

// Mappers from DTOs (from API response) to Domain Models

fun ProvinsiDto.toProvinsi(): Provinsi {
    return Provinsi(
        id = this.id ?: 0, // Default ke 0 atau tangani kasus null dengan tepat
        name = this.name ?: "Unknown" // Default ke "Unknown" atau tangani null
    )
}

fun KabupatenDto.toKabupaten(): Kabupaten {
    return Kabupaten(
        id = this.id ?: 0,
        name = this.name ?: "Unknown",
        provinsiId = this.provinsiId ?: 0
    )
}

fun KecamatanDto.toKecamatan(): Kecamatan {
    return Kecamatan(
        id = this.id ?: 0,
        name = this.name ?: "Unknown",
        kabupatenId = this.kabupatenId ?: 0
    )
}

fun KelurahanDto.toKelurahan(): Kelurahan {
    return Kelurahan(
        id = this.id ?: 0,
        name = this.name ?: "Unknown",
        kecamatanId = this.kecamatanId ?: 0
    )
}

fun RwDto.toRw(): Rw {
    return Rw(
        id = this.id ?: 0,
        name = this.name ?: "Unknown",
        kelurahanId = this.kelurahanId ?: 0
    )
}

fun RtDto.toRt(): Rt {
    return Rt(
        id = this.id ?: 0,
        name = this.name ?: "Unknown",
        rwId = this.rwId ?: 0
    )
}
