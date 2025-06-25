package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the "rt" table in the local Room database.
 * It has a foreign key relationship with the RwEntity.
 */
@Entity(
    tableName = "rt",
    foreignKeys = [
        ForeignKey(
            entity = RwEntity::class,
            parentColumns = ["id"],
            childColumns = ["rwId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["rwId"])]
)
data class RtEntity(
    @PrimaryKey
    val id: Int,
    val rwId: Int?,
    val name: String
)
