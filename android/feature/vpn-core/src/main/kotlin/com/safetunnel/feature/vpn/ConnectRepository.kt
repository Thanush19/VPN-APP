package com.safetunnel.feature.vpn

import javax.inject.Inject

class ConnectRepository @Inject constructor(
    private val api: ConnectApi,
    private val keyPairManager: VpnKeyPairManager
) {
    suspend fun connect(clientPublicKey: String): ConnectResponse = api.connect(ConnectRequest(clientPublicKey))
}
