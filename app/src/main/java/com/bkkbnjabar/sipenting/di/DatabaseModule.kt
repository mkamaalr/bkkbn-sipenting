package com.bkkbnjabar.sipenting.di

import android.content.Context
import androidx.room.Room
import com.bkkbnjabar.sipenting.data.local.dao.KecamatanDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao // Import ini
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao // Import ini
import com.bkkbnjabar.sipenting.data.local.dao.KabupatenDao
import com.bkkbnjabar.sipenting.data.local.dao.KelurahanDao
import com.bkkbnjabar.sipenting.data.local.dao.ProvinsiDao
import com.bkkbnjabar.sipenting.data.local.dao.RtDao
import com.bkkbnjabar.sipenting.data.local.dao.RwDao
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

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sipenting_database"
        ).fallbackToDestructiveMigration() // Ini akan menghapus data lama jika versi DB naik tanpa migrasi manual
            .build()
    }

    @Provides
    @Singleton
    fun provideProvinsiDao(database: AppDatabase): ProvinsiDao {
        return database.provinsiDao()
    }

    @Provides
    @Singleton
    fun provideKabupatenDao(database: AppDatabase): KabupatenDao {
        return database.kabupatenDao()
    }

    @Provides
    @Singleton
    fun provideKecamatanDao(database: AppDatabase): KecamatanDao {
        return database.kecamatanDao()
    }

    @Provides
    @Singleton
    fun provideKelurahanDao(database: AppDatabase): KelurahanDao {
        return database.kelurahanDao()
    }

    @Provides
    @Singleton
    fun provideRwDao(database: AppDatabase): RwDao {
        return database.rwDao()
    }

    @Provides
    @Singleton
    fun provideRtDao(database: AppDatabase): RtDao {
        return database.rtDao()
    }

    @Provides
    @Singleton
    fun providePregnantMotherDao(database: AppDatabase): PregnantMotherDao {
        return database.pregnantMotherDao()
    }

    @Provides
    @Singleton
    fun provideBreastfeedingMotherDao(database: AppDatabase): BreastfeedingMotherDao {
        return database.breastfeedingMotherDao()
    }

    @Provides
    @Singleton
    fun provideChildDao(database: AppDatabase): ChildDao {
        return database.childDao()
    }
}
