package com.bkkbnjabar.sipenting.data.local.mapper

import com.bkkbnjabar.sipenting.data.local.entity.*
import com.bkkbnjabar.sipenting.domain.model.*
import kotlin.jvm.JvmName

// --- Entity to Domain Mappers (for single objects) ---

fun ProvinsiEntity.toDomain(): Provinsi = Provinsi(id = this.id, name = this.name)
fun KabupatenEntity.toDomain(): Kabupaten = Kabupaten(id = this.id, provinsiId = this.provinsiId, name = this.name)
fun KecamatanEntity.toDomain(): Kecamatan = Kecamatan(id = this.id, kabupatenId = this.kabupatenId, name = this.name)
fun KelurahanEntity.toDomain(): Kelurahan = Kelurahan(id = this.id, kecamatanId = this.kecamatanId, name = this.name)
fun RwEntity.toDomain(): Rw = Rw(id = this.id, kelurahanId = this.kelurahanId, name = this.name)
fun RtEntity.toDomain(): Rt = Rt(id = this.id, rwId = this.rwId, name = this.name)


// --- Entity List to Domain List Mappers (for lists of objects) ---

// Menggunakan @JvmName untuk memberikan nama unik pada level JVM
@JvmName("provinsiEntityToDomain")
fun List<ProvinsiEntity>.toDomain(): List<Provinsi> = this.map { it.toDomain() }

@JvmName("kabupatenEntityToDomain")
fun List<KabupatenEntity>.toDomain(): List<Kabupaten> = this.map { it.toDomain() }

@JvmName("kecamatanEntityToDomain")
fun List<KecamatanEntity>.toDomain(): List<Kecamatan> = this.map { it.toDomain() }

@JvmName("kelurahanEntityToDomain")
fun List<KelurahanEntity>.toDomain(): List<Kelurahan> = this.map { it.toDomain() }

@JvmName("rwEntityToDomain")
fun List<RwEntity>.toDomain(): List<Rw> = this.map { it.toDomain() }

@JvmName("rtEntityToDomain")
fun List<RtEntity>.toDomain(): List<Rt> = this.map { it.toDomain() }