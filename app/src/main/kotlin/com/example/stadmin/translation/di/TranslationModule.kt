package com.example.stadmin.translation.di

import com.example.stadmin.translation.data.TraceTranslationRepository
import com.example.stadmin.translation.domain.TraceTranslationInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationModule {

    @Binds
    @Singleton
    abstract fun bindTraceTranslationRepository(
        traceTranslationRepository: TraceTranslationRepository
    ): TraceTranslationInterface
}