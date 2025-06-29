package com.bkkbnjabar.sipenting.di

import android.content.Context
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildVisitsDao
import com.bkkbnjabar.sipenting.data.remote.AuthApiService
import com.bkkbnjabar.sipenting.data.remote.LookupApiService
import com.bkkbnjabar.sipenting.data.remote.PregnantMotherApiService
import com.bkkbnjabar.sipenting.data.repository.AuthRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.LookupRepository
import com.bkkbnjabar.sipenting.data.repository.LookupRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepositoryImpl
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.utils.SharedPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.bkkbnjabar.sipenting.data.local.dao.LookupDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.PregnantMotherVisitsDao
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.data.repository.ChildRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Shared Preferences Manager ---
    @Provides
    @Singleton
    fun provideSharedPrefsManager(@ApplicationContext context: Context): SharedPrefsManager {
        return SharedPrefsManager(context)
    }

    // --- Repositories ---
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
        lookupDao: LookupDao,               // Disediakan dari DatabaseModule
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
        pregnantMotherDao: PregnantMotherDao,           // Disediakan dari DatabaseModule
        pregnantMotherVisitsDao: PregnantMotherVisitsDao // Disediakan dari DatabaseModule
    ): PregnantMotherRepository {
        return PregnantMotherRepositoryImpl(pregnantMotherDao, pregnantMotherVisitsDao)
    }

    @Provides
    @Singleton
    fun provideBreastfeedingMotherRepository(
        breastfeedingMotherDao: BreastfeedingMotherDao,
        breastfeedingMotherVisitsDao: BreastfeedingMotherVisitsDao
    ): BreastfeedingMotherRepository {
        return BreastfeedingMotherRepositoryImpl(breastfeedingMotherDao, breastfeedingMotherVisitsDao)
    }

    @Provides
    @Singleton
    fun provideChildRepository(
        childDao: ChildDao,
        childVisitsDao: ChildVisitsDao
    ): ChildRepository {
        return ChildRepositoryImpl(childDao, childVisitsDao)
    }
}
