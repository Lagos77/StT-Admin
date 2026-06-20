package com.example.stadmin.screens.login.di

import com.example.stadmin.screens.login.data.AuthRepository
import com.example.stadmin.screens.login.domain.AuthInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): AuthInterface
}