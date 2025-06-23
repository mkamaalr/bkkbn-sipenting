package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rt_table",
    foreignKeys = [
        ForeignKey(entity = RwEntity::class,
            parentColumns = ["id"],
            childColumns = ["rwId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["rwId"])]
)
data class RtEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val rwId: Int
)
