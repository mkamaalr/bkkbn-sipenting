package com.bkkbnjabar.sipenting.di

import android.content.Context
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherDao
import com.bkkbnjabar.sipenting.data.local.dao.BreastfeedingMotherVisitsDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildDao
import com.bkkbnjabar.sipenting.data.local.dao.ChildMotherDao
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
import com.bkkbnjabar.sipenting.data.local.db.AppDatabase
import com.bkkbnjabar.sipenting.data.remote.BreastfeedingMotherApiService
import com.bkkbnjabar.sipenting.data.remote.ChildApiService
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.ChildMotherRepository
import com.bkkbnjabar.sipenting.data.repository.ChildMotherRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.ChildRepository
import com.bkkbnjabar.sipenting.data.repository.ChildRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.SyncRepositoryImpl
import com.bkkbnjabar.sipenting.domain.repository.SyncRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPrefsManager(@ApplicationContext context: Context): SharedPrefsManager {
        return SharedPrefsManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        sharedPrefsManager: SharedPrefsManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApiService, sharedPrefsManager)
    }

    @Provides
    @Singleton
    fun provideLookupRepository(
        lookupApiService: LookupApiService,
        lookupDao: LookupDao,
        database: AppDatabase
    ): LookupRepository {
        return LookupRepositoryImpl(
            lookupApiService,
            lookupDao,
            database
        )
    }

    @Provides
    @Singleton
    fun providePregnantMotherRepository(
        motherDao: PregnantMotherDao,
        visitsDao: PregnantMotherVisitsDao,
        apiService: PregnantMotherApiService
    ): PregnantMotherRepository {
        return PregnantMotherRepositoryImpl(motherDao, visitsDao, apiService)
    }

    @Provides
    @Singleton
    fun provideBreastfeedingMotherRepository(
        motherDao: BreastfeedingMotherDao,
        visitsDao: BreastfeedingMotherVisitsDao,
        apiService: BreastfeedingMotherApiService
    ): BreastfeedingMotherRepository {
        return BreastfeedingMotherRepositoryImpl(motherDao, visitsDao, apiService)
    }

    @Provides
    @Singleton
    fun provideChildMotherRepository(
        dao: ChildMotherDao,
        apiService: ChildApiService
    ): ChildMotherRepository {
        return ChildMotherRepositoryImpl(dao, apiService)
    }

    @Provides
    @Singleton
    fun provideChildRepository(
        childDao: ChildDao,
        childMotherDao: ChildMotherDao,
        visitsDao: ChildVisitsDao,
        apiService: ChildApiService
    ): ChildRepository {
        return ChildRepositoryImpl(childDao, childMotherDao, visitsDao, apiService)
    }

    @Provides
    @Singleton
    fun provideSyncRepository(
        pregnantMotherRepository: PregnantMotherRepository,
        breastfeedingMotherRepository: BreastfeedingMotherRepository,
        childRepository: ChildRepository,
        childMotherRepository: ChildMotherRepository,
        lookupRepository: LookupRepository
    ): SyncRepository {
        return SyncRepositoryImpl(
            pregnantMotherRepository,
            breastfeedingMotherRepository,
            childRepository,
            childMotherRepository,
            lookupRepository
        )
    }
}
