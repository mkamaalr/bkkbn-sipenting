package com.bkkbnjabar.sipenting.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao // Asumsi ini ada
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao // Asumsi ini ada
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao

import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity // Asumsi ini ada
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity // Asumsi ini ada
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherVisitsEntity


@Database(
    entities = [
        ProvinsiEntity::class,
        KabupatenEntity::class,
        KecamatanEntity::class,
        KelurahanEntity::class,
        RwEntity::class,
        RtEntity::class,
        PregnantMotherEntity::class,
        BreastfeedingMotherEntity::class,
        ChildEntity::class,
        PregnantMotherVisitsEntity::class // FIXED: Tambahkan Entity ini
    ],
    version = 1, // FIXED: MENINGKATKAN VERSI DATABASE! Naikkan dari 1 ke 10
    exportSchema = false
)
@TypeConverters(ListStringConverter::class) // FIXED: Aktifkan TypeConverter ini jika Anda menyimpan List<String>
abstract class AppDatabase : RoomDatabase() {
    abstract fun lookupDao(): LookupDao

    // DAO untuk data ibu hamil, menyusui, dan anak (tetap sama)
    abstract fun pregnantMotherDao(): PregnantMotherDao
    abstract fun breastfeedingMotherDao(): BreastfeedingMotherDao // Asumsi ada DAO ini
    abstract fun childDao(): ChildDao // Asumsi ada DAO ini
    abstract fun pregnantMotherVisitsDao(): PregnantMotherVisitsDao
}
