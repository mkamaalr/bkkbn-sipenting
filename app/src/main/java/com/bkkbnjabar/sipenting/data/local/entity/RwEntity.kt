package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the "rw" table in the local Room database.
 * It has a foreign key relationship with the KelurahanEntity.
 */
@Entity(
    tableName = "rw",
    foreignKeys = [
        ForeignKey(
            entity = KelurahanEntity::class,
            parentColumns = ["id"],
            childColumns = ["kelurahanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kelurahanId"])]
)
data class RwEntity(
    @PrimaryKey
    val id: Int,
    val kelurahanId: Int?,
    val name: String
)
