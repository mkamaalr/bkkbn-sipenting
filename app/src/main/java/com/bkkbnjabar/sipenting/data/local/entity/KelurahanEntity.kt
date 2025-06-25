package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the "kelurahan" table in the local Room database.
 * It has a foreign key relationship with the KecamatanEntity.
 */
@Entity(
    tableName = "kelurahan",
    foreignKeys = [
        ForeignKey(
            entity = KecamatanEntity::class,
            parentColumns = ["id"],
            childColumns = ["kecamatanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kecamatanId"])]
)
data class KelurahanEntity(
    @PrimaryKey
    val id: Int,
    val kecamatanId: Int?,
    val name: String
)
