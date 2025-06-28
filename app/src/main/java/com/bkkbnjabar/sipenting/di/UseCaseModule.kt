package com.bkkbnjabar.sipenting.di


import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCase
import com.bkkbnjabar.sipenting.domain.usecase.auth.LoginUseCaseImpl
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCase
import com.bkkbnjabar.sipenting.domain.usecase.auth.ValidateTokenUseCaseImpl
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherUseCase
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherUseCaseImpl
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherVisitUseCase
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.CreatePregnantMotherVisitUseCaseImpl
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.UpdatePregnantMotherVisitUseCase
import com.bkkbnjabar.sipenting.domain.usecase.pregnantmother.UpdatePregnantMotherVisitUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {

    @Binds
    @ViewModelScoped
    abstract fun bindLoginUseCase(
        loginUseCaseImpl: LoginUseCaseImpl
    ): LoginUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindValidateTokenUseCase(
        validateTokenUseCaseImpl: ValidateTokenUseCaseImpl
    ): ValidateTokenUseCase

    @Binds
    @ViewModelScoped // Scope untuk use case ini
    abstract fun bindCreatePregnantMotherUseCase(
        createPregnantMotherUseCaseImpl: CreatePregnantMotherUseCaseImpl
    ): CreatePregnantMotherUseCase

    @Binds
    @ViewModelScoped // FIXED: Binding untuk use case kunjungan
    abstract fun bindCreatePregnantMotherVisitUseCase(
        createPregnantMotherVisitUseCaseImpl: CreatePregnantMotherVisitUseCaseImpl
    ): CreatePregnantMotherVisitUseCase

    @Binds
    @ViewModelScoped
    abstract fun bindUpdatePregnantMotherVisitUseCase(
        updatePregnantMotherVisitUseCaseImpl: UpdatePregnantMotherVisitUseCaseImpl
    ): UpdatePregnantMotherVisitUseCase
}
