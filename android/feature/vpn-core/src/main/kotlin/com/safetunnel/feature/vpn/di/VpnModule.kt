package com.safetunnel.feature.vpn.di

import android.content.Context
import com.safetunnel.feature.vpn.ConnectApi
import com.safetunnel.feature.vpn.ConnectRepository
import com.safetunnel.feature.vpn.VpnKeyPairManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@Module
@InstallIn(SingletonComponent::class)
object VpnModule {

    @Provides
    @Singleton
    fun provideVpnKeyPairManager(@ApplicationContext context: Context): VpnKeyPairManager {
        return VpnKeyPairManager(context)
    }

    @Provides
    @Singleton
    fun provideConnectApi(@ApplicationContext context: Context): ConnectApi {
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(ConnectApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConnectRepository(
        api: ConnectApi,
        keyPairManager: VpnKeyPairManager
    ): ConnectRepository {
        return ConnectRepository(api, keyPairManager)
    }
}