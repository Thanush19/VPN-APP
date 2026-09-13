package com.safetunnel.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safetunnel.core.navigation.NavHost
import com.safetunnel.feature.home.data.*
import com.safetunnel.feature.home.ui.theme.HomeTheme
import com.safetunnel.feature.servers.data.VpnServer
import kotlinx.coroutines.flow.collectLatest

/**
 * Main Home screen displaying VPN connection status and controls.
 *
 * @param viewModel The HomeViewModel to observe state and handle intents.
 * @param onNavigateToServers Callback to navigate to servers screen.
 * @param onNavigateToSettings Callback to navigate to settings screen.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToServers: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    HomeTheme {
        // Collect state as a Flow and convert to State for Compose
        val uiState by viewModel.state.collectAsState()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("SafeTunnel") },
                    actions = {
                        IconButton(onClick = { onNavigateToSettings() }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (uiState.connectionState) {
                    ConnectionState.DISCONNECTED -> DisconnectedContent(
                        onConnectClicked = { viewModel.handleIntent(HomeIntent.Connect) },
                        onServerSelected = { viewModel.handleIntent(HomeIntent.SelectServer(it)) },
                        selectedServer = uiState.selectedServer,
                        isLoading = uiState.isLoading,
                        onNavigateToServers = onNavigateToServers
                    )
                    ConnectionState.CONNECTING -> ConnectingContent(
                        onCancelClicked = { viewModel.handleIntent(HomeIntent.Disconnect) },
                        serverName = uiState.selectedServer?.name ?: "Unknown"
                    )
                    ConnectionState.CONNECTED -> ConnectedContent(
                        onDisconnectClicked = { viewModel.handleIntent(HomeIntent.Disconnect) },
                        connectionDuration = uiState.connectionDuration,
                        downloadSpeed = uiState.downloadBytes, // Placeholder - will be actual speed in later phases
                        uploadSpeed = uiState.uploadBytes, // Placeholder - will be actual speed in later phases
                        serverName = uiState.selectedServer?.name ?: "Unknown"
                    )
                    ConnectionState.DISCONNECTING -> DisconnectingContent(
                        onCancelClicked = { viewModel.handleIntent(HomeIntent.Disconnect) },
                        serverName = uiState.selectedServer?.name ?: "Unknown"
                    )
                    ConnectionState.RECONNECTING -> ReconnectingContent(
                        onCancelClicked = { viewModel.handleIntent(HomeIntent.Disconnect) },
                        serverName = uiState.selectedServer?.name ?: "Unknown"
                    )
                    ConnectionState.ERROR -> ErrorContent(
                        message = uiState.error ?: "An unknown error occurred",
                        onRetryClicked = { viewModel.handleIntent(HomeIntent.Retry) },
                        onDisconnectClicked = { viewModel.handleIntent(HomeIntent.Disconnect) }
                    )
                }
            }
        }
    }
}

/**
 * Content shown when VPN is disconnected.
 */
@Composable
fun DisconnectedContent(
    onConnectClicked: () -> Unit,
    onServerSelected: (VpnServer) -> Unit,
    selectedServer: VpnServer?,
    isLoading: Boolean,
    onNavigateToServers: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // VPN Icon
        Image(
            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
            contentDescription = "VPN Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Status Text
        Text(
            text = "Disconnected",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Server Info
        selectedServer?.let { server ->
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${server.name} - ${server.city}, ${server.country}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                server.loadPercentage?.let { load ->
                    Text(
                        text = "Load: $load%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Text(
                text = "No server selected",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Server Selection Button
        Button(
            onClick = {
                onNavigateToServers()
            },
            enabled = !isLoading,
            modifier = Modifier
                .width(200.dp)
                .padding(bottom = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Select Server")
            }
        }

        // Connect Button
        Button(
            onClick = { onConnectClicked() },
            enabled = selectedServer != null && !isLoading,
            modifier = Modifier.width(200.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("CONNECT")
            }
        }
    }
}

/**
 * Content shown when VPN is connecting.
 */
@Composable
fun ConnectingContent(
    onCancelClicked: () -> Unit,
    serverName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connecting Icon (animated in later phases)
        Image(
            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
            contentDescription = "Connecting Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Status Text
        Text(
            text = "Connecting...",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Server Info
        Text(
            text = "Connecting to $serverName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cancel Button
        Button(
            onClick = { onCancelClicked() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("CANCEL")
        }
    }
}

/**
 * Content shown when VPN is connected.
 */
@Composable
fun ConnectedContent(
    onDisconnectClicked: () -> Unit,
    connectionDuration: Long,
    downloadSpeed: Long,
    uploadSpeed: Long,
    serverName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connected Icon
        Image(
            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
            contentDescription = "Connected Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Status Text
        Text(
            text = "Connected",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Server Info
        Text(
            text = "Connected to $serverName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Connection Duration
        Text(
            text = formatDuration(connectionDuration),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Statistics Row
        Row(
            modifier = Modifier
                .width(200.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Download",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatSpeed(downloadSpeed)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column {
                Text(
                    text = "Upload",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatSpeed(uploadSpeed)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Disconnect Button
        Button(
            onClick = { onDisconnectClicked() },
            modifier = Modifier.width(200.dp)
                .padding(top = 24.dp)
        ) {
            Text("DISCONNECT")
        }
    }
}

/**
 * Content shown when VPN is disconnecting.
 */
@Composable
fun DisconnectingContent(
    onCancelClicked: () -> Unit,
    serverName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Disconnecting Icon
        Image(
            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
            contentDescription = "Disconnecting Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Status Text
        Text(
            text = "Disconnecting...",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Server Info
        Text(
            text = "Disconnecting from $serverName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cancel Button
        Button(
            onClick = { onCancelClicked() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("CANCEL")
        }
    }
}

/**
 * Content shown when VPN is reconnecting.
 */
@Composable
fun ReconnectingContent(
    onCancelClicked: () -> Unit,
    serverName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reconnecting Icon
        Image(
            painter = painterResource(id = android.R.drawable.ic_lock_idle_lock),
            contentDescription = "Reconnecting Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Status Text
        Text(
            text = "Reconnecting...",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Server Info
        Text(
            text = "Reconnecting to $serverName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Cancel Button
        Button(
            onClick = { onCancelClicked() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("CANCEL")
        }
    }
}

/**
 * Content shown when VPN encounters an error.
 */
@Composable
fun ErrorContent(
    message: String,
    onRetryClicked: () -> Unit,
    onDisconnectClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error Icon
        Image(
            painter = painterResource(id = android.R.drawable.ic_dialog_alert),
            contentDescription = "Error Icon",
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        // Error Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            textAlign = align.Center
        )

        Button(
            onClick = { onRetryClicked() },
            modifier = Modifier.width(150.dp)
                .padding(bottom = 8.dp)
        ) {
            Text("RETRY")
        }

        Button(
            onClick = { onDisconnectClicked() },
            modifier = Modifier.width(150.dp)
        ) {
            Text("DISCONNECT")
        }
    }
}

/**
 * Helper function to format duration in milliseconds to HH:mm:ss format.
 */
private fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

/**
 * Helper function to format speed in bytes per second to human readable format.
 */
private fun formatSpeed(bytesPerSecond: Long): String {
    val kbps = bytesPerSecond / 1024.0
    if (kbps < 1) {
        return "${bytesPerSecond} B/s"
    }
    val mbps = kbps / 1024.0
    if (mbps < 1) {
        return "%.1f KB/s".format(kbps)
    }
    return "%.1f MB/s".format(mbps)
}