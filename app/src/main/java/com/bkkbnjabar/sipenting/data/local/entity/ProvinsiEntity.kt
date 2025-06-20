package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provinsis")
data class ProvinsiEntity(
    @PrimaryKey val id: Int,
    val name: String,
)