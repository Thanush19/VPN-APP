package com.safetunnel.feature.auth.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.safetunnel.feature.auth.AuthRepository
import com.safetunnel.feature.auth.TokenDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepository): AuthRepository

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context): TokenDataStore {
        return TokenDataStore(context)
    }
}