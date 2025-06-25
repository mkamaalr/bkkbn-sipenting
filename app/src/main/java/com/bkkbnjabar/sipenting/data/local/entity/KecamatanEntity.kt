package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the "kecamatan" table in the local Room database.
 * It has a foreign key relationship with the KabupatenEntity.
 */
@Entity(
    tableName = "kecamatan",
    foreignKeys = [
        ForeignKey(
            entity = KabupatenEntity::class,
            parentColumns = ["id"],
            childColumns = ["kabupatenId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kabupatenId"])]
)
data class KecamatanEntity(
    @PrimaryKey
    val id: Int,
    val kabupatenId: Int?,
    val name: String
)
