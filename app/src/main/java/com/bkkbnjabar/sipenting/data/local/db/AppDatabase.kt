package com.bkkbnjabar.sipenting.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.local.dao.*
import com.bkkbnjabar.sipenting.data.local.entity.*


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
        BreastfeedingMotherVisitsEntity::class,
        ChildEntity::class,
        ChildMotherEntity::class,
        ChildVisitsEntity::class,
        LookupItemEntity::class,
        PregnantMotherVisitsEntity::class // FIXED: Tambahkan Entity ini
    ],
    version = 2, // FIXED: MENINGKATKAN VERSI DATABASE! Naikkan dari 1 ke 10
    exportSchema = false
)
@TypeConverters(ListStringConverter::class) // FIXED: Aktifkan TypeConverter ini jika Anda menyimpan List<String>
abstract class AppDatabase : RoomDatabase() {
    abstract fun lookupDao(): LookupDao

    // DAO untuk data ibu hamil, menyusui, dan anak (tetap sama)
    abstract fun pregnantMotherDao(): PregnantMotherDao
    abstract fun breastfeedingMotherDao(): BreastfeedingMotherDao // Asumsi ada DAO ini
    abstract fun childDao(): ChildDao // Asumsi ada DAO ini
    abstract fun childMotherDao(): ChildMotherDao // Asumsi ada DAO ini
    abstract fun breastfeedingMotherVisitsDao(): BreastfeedingMotherVisitsDao // ADDED
    abstract fun childVisitsDao(): ChildVisitsDao // ADDED
    abstract fun pregnantMotherVisitsDao(): PregnantMotherVisitsDao
    abstract fun provinsiDao(): ProvinsiDao
    abstract fun kabupatenDao(): KabupatenDao
    abstract fun kecamatanDao(): KecamatanDao
    abstract fun kelurahanDao(): KelurahanDao
    abstract fun rwDao(): RwDao
    abstract fun rtDao(): RtDao
}
