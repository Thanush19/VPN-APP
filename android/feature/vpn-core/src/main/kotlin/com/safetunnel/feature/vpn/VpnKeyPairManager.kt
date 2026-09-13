package com.safetunnel.feature.vpn

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.string
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStore.PrivateKeyEntry
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.security.spec.ECGenParameterSpec
import java.security.cert.Certificate
import java.util.Base64

private const val KEY_ALIAS = "vpn_private_key"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vpn_keys")

class VpnKeyPairManager(private val context: Context) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    suspend fun hasKeyPair(): Boolean = withContext(Dispatchers.IO) {
        keyStore.containsAlias(KEY_ALIAS) && context.dataStore.data.firstOrNull { it.contains(Preferences.Key("public_key")) } != null
    }

    suspend fun getOrGenerateKeyPair(): KeyPair = withContext(Dispatchers.IO) {
        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? PrivateKeyEntry
            val privateKey = entry?.privateKey
            if (privateKey != null) {
                val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
                return@withContext KeyPair(publicKey, privateKey)
            }
        }
        // generate new key pair
        val kpg = KeyPairGenerator.getInstance("EC", "AndroidKeyStore")
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).apply {
            setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            setUserAuthenticationRequired(false)
        }.build()
        kpg.initialize(spec)
        val pair = kpg.generateKeyPair()
        // store public key in DataStore for easy access
        context.dataStore.edit { prefs ->
            val pub = Base64.getEncoder().encodeToString(pair.public.encoded)
            prefs[Preferences.Key("public_key")] = pub
        }
        pair
    }

    suspend fun getPublicKeyBase64(): String? = withContext(Dispatchers.IO) {
        context.dataStore.data.firstOrNull { it.contains(Preferences.Key("public_key")) }?.get(Preferences.Key("public_key"))
    }
}