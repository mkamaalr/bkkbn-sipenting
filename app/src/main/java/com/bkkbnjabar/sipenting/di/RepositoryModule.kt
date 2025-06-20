package com.bkkbnjabar.sipenting.di

import com.bkkbnjabar.sipenting.data.repository.AuthRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.BreastfeedingMotherRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.ChildRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.LookupRepositoryImpl
import com.bkkbnjabar.sipenting.data.repository.PregnantMotherRepositoryImpl
import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.domain.repository.BreastfeedingMotherRepository
import com.bkkbnjabar.sipenting.domain.repository.ChildRepository
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPregnantMotherRepository(
        pregnantMotherRepositoryImpl: PregnantMotherRepositoryImpl
    ): PregnantMotherRepository

    @Binds
    @Singleton
    abstract fun bindBreastfeedingMotherRepository(
        breastfeedingMotherRepositoryImpl: BreastfeedingMotherRepositoryImpl
    ): BreastfeedingMotherRepository

    @Binds
    @Singleton
    abstract fun bindChildRepository(
        childRepositoryImpl: ChildRepositoryImpl
    ): ChildRepository

    @Binds
    @Singleton
    abstract fun bindLookupRepository(
        lookupRepositoryImpl: LookupRepositoryImpl
    ): LookupRepository
}
