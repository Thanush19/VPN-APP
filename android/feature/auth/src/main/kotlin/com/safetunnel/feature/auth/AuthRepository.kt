package com.safetunnel.feature.auth

import javax.inject.Inject
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.net.Uri

class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(AuthApi::class.java)

    suspend fun register(request: AuthRequest): AuthResponse = api.register(request)
    suspend fun login(request: AuthRequest): AuthResponse = api.login(request)
    suspend fun refresh(request: RefreshTokenRequest): AuthResponse = api.refresh(request)
}
