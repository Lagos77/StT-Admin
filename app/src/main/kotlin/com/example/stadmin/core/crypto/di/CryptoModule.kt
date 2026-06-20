package com.example.stadmin.core.crypto.di

import android.content.Context
import com.example.stadmin.core.crypto.KeyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CryptoModule {

    @Provides
    @Singleton
    fun provideKeyManager(@ApplicationContext context: Context): KeyManager {
        return KeyManager(context)
    }
}