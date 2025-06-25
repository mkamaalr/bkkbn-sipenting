package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the "kabupaten" table in the local Room database.
 * It has a foreign key relationship with the ProvinsiEntity.
 */
@Entity(
    tableName = "kabupaten",
    foreignKeys = [
        ForeignKey(
            entity = ProvinsiEntity::class,
            parentColumns = ["id"],
            childColumns = ["provinsiId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["provinsiId"])]
)
data class KabupatenEntity(
    @PrimaryKey
    val id: Int,
    val provinsiId: Int?,
    val name: String
)
