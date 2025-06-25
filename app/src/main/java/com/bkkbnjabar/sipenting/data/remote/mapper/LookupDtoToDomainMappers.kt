package com.bkkbnjabar.sipenting.data.remote.mapper

import com.bkkbnjabar.sipenting.data.local.entity.*
import com.bkkbnjabar.sipenting.data.model.lookup.*

// --- DTO to Entity Mappers ---

fun ProvinsiDto.toEntity(): ProvinsiEntity {
    return ProvinsiEntity(id = this.id, name = this.name)
}

fun KabupatenDto.toEntity(): KabupatenEntity {
    return KabupatenEntity(id = this.id, provinsiId = this.provinsiId, name = this.name)
}

fun KecamatanDto.toEntity(): KecamatanEntity {
    return KecamatanEntity(id = this.id, kabupatenId = this.kabupatenId, name = this.name)
}

fun KelurahanDto.toEntity(): KelurahanEntity {
    return KelurahanEntity(id = this.id, kecamatanId = this.kecamatanId, name = this.name)
}

fun RwDto.toEntity(): RwEntity {
    return RwEntity(id = this.id, kelurahanId = this.kelurahanId, name = this.name)
}

fun RtDto.toEntity(): RtEntity {
    return RtEntity(id = this.id, rwId = this.rwId, name = this.name)
}
