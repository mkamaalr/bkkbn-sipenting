package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "kabupatens",
    foreignKeys = [
        ForeignKey(entity = ProvinsiEntity::class,
            parentColumns = ["id"],
            childColumns = ["provinsiId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["provinsiId"])]
)
data class KabupatenEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val provinsiId: Int
)
