package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import com.bkkbnjabar.sipenting.domain.model.Kabupaten
import com.bkkbnjabar.sipenting.domain.model.Kecamatan
import com.bkkbnjabar.sipenting.domain.model.Kelurahan
import com.bkkbnjabar.sipenting.domain.model.Provinsi
import com.bkkbnjabar.sipenting.domain.model.Rw
import com.bkkbnjabar.sipenting.domain.model.Rt

// Mappers from Domain Models to Room Entities
fun Provinsi.toProvinsiEntity(): ProvinsiEntity =
    ProvinsiEntity(id = this.id, name = this.name)

fun Kabupaten.toKabupatenEntity(): KabupatenEntity =
    KabupatenEntity(id = this.id, name = this.name, provinsiId = this.provinsiId) // Perhatikan nama kolom Room: provinsi_id

fun Kecamatan.toKecamatanEntity(): KecamatanEntity =
    KecamatanEntity(id = this.id, name = this.name, kabupatenId = this.kabupatenId) // Perhatikan nama kolom Room: kabupaten_id

fun Kelurahan.toKelurahanEntity(): KelurahanEntity =
    KelurahanEntity(id = this.id, name = this.name, kecamatanId = this.kecamatanId) // Perhatikan nama kolom Room: kecamatan_id

fun Rw.toRwEntity(): RwEntity =
    RwEntity(id = this.id, name = this.name, kelurahanId = this.kelurahanId) // Perhatikan nama kolom Room: kelurahan_id

fun Rt.toRtEntity(): RtEntity =
    RtEntity(id = this.id, name = this.name, rwId = this.rwId) // Perhatikan nama kolom Room: rw_id


// Mappers from Room Entities to Domain Models
fun ProvinsiEntity.toProvinsi(): Provinsi =
    Provinsi(id = this.id, name = this.name)

fun KabupatenEntity.toKabupaten(): Kabupaten =
    Kabupaten(id = this.id, name = this.name, provinsiId = this.provinsiId) // Perhatikan nama kolom Room: provinsi_id

fun KecamatanEntity.toKecamatan(): Kecamatan =
    Kecamatan(id = this.id, name = this.name, kabupatenId = this.kabupatenId) // Perhatikan nama kolom Room: kabupaten_id

fun KelurahanEntity.toKelurahan(): Kelurahan =
    Kelurahan(id = this.id, name = this.name, kecamatanId = this.kecamatanId) // Perhatikan nama kolom Room: kecamatan_id

fun RwEntity.toRw(): Rw =
    Rw(id = this.id, name = this.name, kelurahanId = this.kelurahanId) // Perhatikan nama kolom Room: kelurahan_id

fun RtEntity.toRt(): Rt =
    Rt(id = this.id, name = this.name, rwId = this.rwId) // Perhatikan nama kolom Room: rw_id
