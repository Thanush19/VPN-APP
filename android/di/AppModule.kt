package com.safetunnel.di

import com.safetunnel.feature.home.data.HomeRepository
import com.safetunnel.feature.servers.data.ServersRepository
import com.safetunnel.feature.settings.data.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHomeRepository(): HomeRepository {
        return HomeRepository()
    }

    @Provides
    @Singleton
    fun provideServersRepository(): ServersRepository {
        return ServersRepository()
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepository()
    }
}