package com.example.stadmin.core.deepl.di

import com.example.stadmin.BuildConfig
import com.example.stadmin.core.deepl.DeepDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeepLModule {

    @Provides
    @Singleton
    fun provideDeepDataSource(): DeepDataSource {
        return DeepDataSource(apiKey = BuildConfig.DEEPL_API_KEY)
    }
}