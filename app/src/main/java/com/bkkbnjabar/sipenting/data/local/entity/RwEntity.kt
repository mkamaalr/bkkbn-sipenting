package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rw_table",
    foreignKeys = [
        ForeignKey(entity = KelurahanEntity::class,
            parentColumns = ["id"],
            childColumns = ["kelurahanId"],
            onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["kelurahanId"])]
)
data class RwEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val kelurahanId: Int
)
