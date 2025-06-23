package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "kecamatan_table",
    foreignKeys = [
        ForeignKey(entity = KabupatenEntity::class,
            parentColumns = ["id"],
            childColumns = ["kabupatenId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["kabupatenId"])]
)
data class KecamatanEntity(
    @PrimaryKey val id: Int,
    val name: String, // Menggunakan 'name' agar konsisten dengan model domain
    val kabupatenId: Int // Foreign key ke KabupatenEntity
)