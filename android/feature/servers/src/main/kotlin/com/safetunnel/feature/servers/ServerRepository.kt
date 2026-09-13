package com.safetunnel.feature.servers

import javax.inject.Inject
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context

class ServerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(ServerApi::class.java)

    suspend fun getServers(): List<ServerResponse> = api.getServers()
    suspend fun getServerById(id: String): ServerResponse = api.getServerById(id)
}