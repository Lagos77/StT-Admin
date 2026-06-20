package com.example.stadmin.screens.trace.di

import com.example.stadmin.screens.trace.data.ImageRepository
import com.example.stadmin.screens.trace.data.TraceRepository
import com.example.stadmin.screens.trace.domain.ImageInterface
import com.example.stadmin.screens.trace.domain.TraceInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TraceModule {

    @Binds
    @Singleton
    abstract fun bindTraceRepository(
        traceRepository: TraceRepository
    ): TraceInterface

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepository: ImageRepository
    ): ImageInterface
}