package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the "provinsi" table in the local Room database.
 */
@Entity(tableName = "provinsi")
data class ProvinsiEntity(
    @PrimaryKey
    val id: Int,
    val name: String
)
