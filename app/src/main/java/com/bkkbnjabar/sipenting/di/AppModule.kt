package com.bkkbnjabar.sipenting.di

import android.content.Context
import androidx.room.Room
import com.bkkbnjabar.sipenting.data.local.converter.ListStringConverter
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.db.AppDatabase
import com.bkkbnjabar.sipenting.data.repository.AuthRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.LookupRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepositoryImpl
import com.bkkbnjabar.sipenting.data.remote.AuthApiService // Diimpor, tapi tidak disediakan di sini
import com.bkkbnjabar.sipenting.data.remote.LookupApiService // Diimpor, tapi tidak disediakan di sini
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService // Diimpor, tapi tidak disediakan di sini
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Shared Preferences Manager ---
    @Provides
    @Singleton
    fun provideSharedPrefsManager(@ApplicationContext context: Context): SharedPrefsManager {
        return SharedPrefsManager(context)
    }

    // --- Room Database ---
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sipenting_database"
        )
//            .addTypeConverter(ListStringConverter::class.java)
            .fallbackToDestructiveMigration() // Gunakan ini hanya untuk pengembangan
            .build()
    }

    // --- DAOs ---
    @Provides
    fun provideLookupDao(database: AppDatabase): LookupDao {
        return database.lookupDao()
    }

    @Provides
    fun providePregnantMotherDao(database: AppDatabase): PregnantMotherDao {
        return database.pregnantMotherDao()
    }

    @Provides
    fun provideBreastfeedingMotherDao(database: AppDatabase): BreastfeedingMotherDao {
        return database.breastfeedingMotherDao()
    }

    @Provides
    fun provideChildDao(database: AppDatabase): ChildDao {
        return database.childDao()
    }

    @Provides
    fun providePregnantMotherVisitsDao(database: AppDatabase): PregnantMotherVisitsDao {
        return database.pregnantMotherVisitsDao()
    }

    // --- Repositories ---
    // Repositories akan mengkonsumsi (menerima sebagai parameter) API Services dari NetworkModule
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService, // Disediakan dari NetworkModule
        sharedPrefsManager: SharedPrefsManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApiService, sharedPrefsManager)
    }

    @Provides
    @Singleton
    fun provideLookupRepository(
        lookupApiService: LookupApiService, // Disediakan dari NetworkModule
        lookupDao: LookupDao,
        sharedPrefsManager: SharedPrefsManager
    ): LookupRepository {
        return LookupRepositoryImpl(
            lookupApiService,
            lookupDao,
            sharedPrefsManager
        )
    }

    @Provides
    @Singleton
    fun providePregnantMotherRepository(
        pregnantMotherDao: PregnantMotherDao,
        pregnantMotherVisitsDao: PregnantMotherVisitsDao
    ): PregnantMotherRepository {
        return PregnantMotherRepositoryImpl(pregnantMotherDao, pregnantMotherVisitsDao)
    }
}
