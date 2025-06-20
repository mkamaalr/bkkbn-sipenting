package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "kelurahans",
    foreignKeys = [
        ForeignKey(entity = KecamatanEntity::class,
            parentColumns = ["id"],
            childColumns = ["kecamatanId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["kecamatanId"])]
)
data class KelurahanEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val kecamatanId: Int
)
