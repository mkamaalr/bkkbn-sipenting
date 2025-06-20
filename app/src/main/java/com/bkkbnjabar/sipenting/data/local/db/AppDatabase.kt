package com.bkkbnjabar.sipenting.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bkkbnjabar.sipenting.data.local.dao.KabupatenDao
import com.bkkbnjabar.sipenting.data.local.dao.KecamatanDao
import com.bkkbnjabar.sipenting.data.local.dao.KelurahanDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.ProvinsiDao
import com.bkkbnjabar.sipenting.data.local.dao.RtDao
import com.bkkbnjabar.sipenting.data.local.dao.RwDao
import com.bkkbnjabar.sipenting.data.local.entity.KabupatenEntity
import com.bkkbnjabar.sipenting.data.local.entity.KecamatanEntity
import com.bkkbnjabar.sipenting.data.local.entity.KelurahanEntity
import com.bkkbnjabar.sipenting.data.local.entity.PregnantMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.ProvinsiEntity
import com.bkkbnjabar.sipenting.data.local.entity.RtEntity
import com.bkkbnjabar.sipenting.data.local.entity.RwEntity
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao // Import ini
import com.bkkbnjabar.sipenting.data.local.entity.BreastfeedingMotherEntity
import com.bkkbnjabar.sipenting.data.local.entity.ChildEntity

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
        ChildEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun provinsiDao(): ProvinsiDao
    abstract fun kabupatenDao(): KabupatenDao
    abstract fun kecamatanDao(): KecamatanDao
    abstract fun kelurahanDao(): KelurahanDao
    abstract fun rwDao(): RwDao
    abstract fun rtDao(): RtDao
    abstract fun pregnantMotherDao(): PregnantMotherDao
    abstract fun breastfeedingMotherDao(): BreastfeedingMotherDao
    abstract fun childDao(): ChildDao
}
