package com.safetunnel.feature.auth

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.edit.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringSetPreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.tokenDataStore by preferencesDataStore(name = "token_store")

private val ACCESS_TOKEN_KEY = stringSetPreferenceKey("access_token")
private val REFRESH_TOKEN_KEY = stringSetPreferenceKey("refresh_token")

class TokenDataStore(private val context: Context) {

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = setOf(accessToken)
            preferences[REFRESH_TOKEN_KEY] = setOf(refreshToken)
        }
    }

    suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data.first()
            .map { it[ACCESS_TOKEN_KEY] }
            .firstOrNull()?.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return context.tokenDataStore.data.first()
            .map { it[REFRESH_TOKEN_KEY] }
            .firstOrNull()?.firstOrNull()
    }

    suspend fun clearTokens() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}