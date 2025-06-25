package com.bkkbnjabar.sipenting.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas generik untuk menyimpan semua jenis item lookup (pilihan dropdown).
 * Kolom 'type' digunakan untuk membedakan antar jenis lookup,
 * misal: 'disease_histories', 'counseling_types', dll.
 */
@Entity(
    tableName = "lookup_item",
    indices = [Index(value = ["type"])] // Tambahkan index untuk performa query by type
)
data class LookupItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serverId: Int?, // ID asli dari server, jika ada
    val name: String,
    val type: String // Kunci untuk membedakan jenis lookup
)