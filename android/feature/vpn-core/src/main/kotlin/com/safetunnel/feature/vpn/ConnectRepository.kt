package com.safetunnel.feature.vpn
import com.safetunnel.feature.auth.AuthApi
import com.safetunnel.feature.auth.AuthRepository
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import javax.inject.Inject

class ConnectRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api = retrofit.create(ConnectApi::class.java)

    suspend fun connect(clientPublicKey: String): ConnectResponse = api.connect(ConnectRequest(clientPublicKey))
}
