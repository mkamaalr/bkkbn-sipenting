package com.bkkbnjabar.sipenting.di

import com.bkkbnjabar.sipenting.domain.repository.AuthRepository
import com.bkkbnjabar.sipenting.domain.repository.LookupRepository
import com.bkkbnjabar.sipenting.domain.repository.PregnantMotherRepository
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCase
import com.bkkbnjabar.sipenting.domain.usecase.common.GetKecamatansUseCase
import com.bkkbnjabar.sipenting.domain.usecase.common.GetKabupatensUseCase // Import ini
import com.bkkbnjabar.sipenting.domain.usecase.common.GetKelurahansUseCase // Import ini
import com.bkkbnjabar.sipenting.domain.usecase.common.GetProvinsisUseCase // Import ini
import com.bkkbnjabar.sipenting.domain.usecase.common.GetRTSUseCase // Import ini
import com.bkkbnjabar.sipenting.domain.usecase.common.GetRWSUseCase // Import ini
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.JvmStatic

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    companion object {

        @Provides
        @Singleton
        @JvmStatic
        fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
            return LoginUseCase(authRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideValidateTokenUseCase(authRepository: AuthRepository): ValidateTokenUseCase {
            return ValidateTokenUseCase(authRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetProvinsisUseCase(lookupRepository: LookupRepository): GetProvinsisUseCase {
            return GetProvinsisUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetKabupatensUseCase(lookupRepository: LookupRepository): GetKabupatensUseCase {
            return GetKabupatensUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetKecamatansUseCase(lookupRepository: LookupRepository): GetKecamatansUseCase {
            return GetKecamatansUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetKelurahansUseCase(lookupRepository: LookupRepository): GetKelurahansUseCase {
            return GetKelurahansUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetRWSUseCase(lookupRepository: LookupRepository): GetRWSUseCase {
            return GetRWSUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideGetRTSUseCase(lookupRepository: LookupRepository): GetRTSUseCase {
            return GetRTSUseCase(lookupRepository)
        }

        @Provides
        @Singleton
        @JvmStatic
        fun provideCreatePregnantMotherUseCase(pregnantMotherRepository: PregnantMotherRepository): CreatePregnantMotherUseCase {
            return CreatePregnantMotherUseCase(pregnantMotherRepository)
        }
    }
}
