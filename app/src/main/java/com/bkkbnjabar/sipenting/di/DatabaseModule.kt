package com.bkkbnjabar.sipenting.di

import android.content.Context
import androidx.room.Room
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao // Import LookupDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao // Import BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao // Import ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao // Import PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    /**
//     * Menyediakan instance tunggal (Singleton) dari Room database.
//     * Menggunakan fallbackToDestructiveMigration() untuk mempermudah pengembangan
//     * dengan menghapus database lama saat ada perubahan skema.
//     * Untuk produksi, perlu implementasi migrasi yang lebih tepat.
//     */
//    @Provides
//    @Singleton
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(
//            context,
//            AppDatabase::class.java,
//            "sipenting_database" // Nama database Anda
//        )
//            .addTypeConverter(ListStringConverter::class.java) // Pastikan converter ini ada
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    /**
//     * Menyediakan instance LookupDao.
//     * @param database Instance AppDatabase.
//     * @return Instance LookupDao.
//     */
//    @Provides
//    fun provideLookupDao(database: AppDatabase): LookupDao {
//        return database.lookupDao()
//    }
//
//    /**
//     * Menyediakan instance PregnantMotherDao.
//     * @param database Instance AppDatabase.
//     * @return Instance PregnantMotherDao.
//     */
//    @Provides
//    fun providePregnantMotherDao(database: AppDatabase): PregnantMotherDao {
//        return database.pregnantMotherDao()
//    }
//
//    /**
//     * Menyediakan instance BreastfeedingMotherDao.
//     * @param database Instance AppDatabase.
//     * @return Instance BreastfeedingMotherDao.
//     */
//    @Provides
//    fun provideBreastfeedingMotherDao(database: AppDatabase): BreastfeedingMotherDao {
//        return database.breastfeedingMotherDao()
//    }
//
//    /**
//     * Menyediakan instance ChildDao.
//     * @param database Instance AppDatabase.
//     * @return Instance ChildDao.
//     */
//    @Provides
//    fun provideChildDao(database: AppDatabase): ChildDao {
//        return database.childDao()
//    }
//
//    /**
//     * Menyediakan instance PregnantMotherVisitsDao.
//     * @param database Instance AppDatabase.
//     * @return Instance PregnantMotherVisitsDao.
//     */
//    @Provides
//    fun providePregnantMotherVisitsDao(database: AppDatabase): PregnantMotherVisitsDao {
//        return database.pregnantMotherVisitsDao()
//    }
//}
