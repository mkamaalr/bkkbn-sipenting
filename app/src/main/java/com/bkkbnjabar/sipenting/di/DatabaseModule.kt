package com.bkkbnjabar.sipenting.di

import android.content.Context
import androidx.room.Room
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildVisitsDao
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Menyediakan instance tunggal (Singleton) dari Room database.
     * Menggunakan fallbackToDestructiveMigration() untuk mempermudah pengembangan
     * dengan menghapus database lama saat ada perubahan skema.
     * Untuk produksi, perlu implementasi migrasi yang lebih tepat.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sipenting_database" // Nama database Anda
        )
            // .addTypeConverter(ListStringConverter()) // TypeConverter dipindah ke anotasi @Database
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Menyediakan instance LookupDao.
     */
    @Provides
    @Singleton // Pastikan DAO juga singleton jika database-nya singleton
    fun provideLookupDao(database: AppDatabase): LookupDao {
        return database.lookupDao()
    }

    /**
     * Menyediakan instance PregnantMotherDao.
     */
    @Provides
    @Singleton
    fun providePregnantMotherDao(database: AppDatabase): PregnantMotherDao {
        return database.pregnantMotherDao()
    }

    /**
     * Menyediakan instance BreastfeedingMotherDao.
     */
    @Provides
    @Singleton
    fun provideBreastfeedingMotherDao(database: AppDatabase): BreastfeedingMotherDao {
        return database.breastfeedingMotherDao()
    }

    /**
     * Menyediakan instance ChildDao.
     */
    @Provides
    @Singleton
    fun provideChildDao(database: AppDatabase): ChildDao {
        return database.childDao()
    }

    /**
     * Menyediakan instance PregnantMotherVisitsDao.
     */
    @Provides
    @Singleton
    fun providePregnantMotherVisitsDao(database: AppDatabase): PregnantMotherVisitsDao {
        return database.pregnantMotherVisitsDao()
    }

    @Provides
    @Singleton
    fun provideBreastfeedingMotherVisitsDao(database: AppDatabase): BreastfeedingMotherVisitsDao {
        return database.breastfeedingMotherVisitsDao()
    }

    @Provides
    @Singleton
    fun provideChildVisitsDao(database: AppDatabase): ChildVisitsDao {
        return database.childVisitsDao()
    }
}
