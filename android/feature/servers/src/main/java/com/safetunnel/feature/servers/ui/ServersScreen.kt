package com.safetunnel.feature.servers.ui

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
import com.safetunnel.feature.servers.data.*
import com.safetunnel.feature.servers.ui.theme.ServersTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen displaying list of available VPN servers.
 *
 * @param viewModel The ServersViewModel to observe state and handle intents.
 * @param onServerSelected Callback when a server is selected.
 */
@Composable
fun ServersScreen(
    viewModel: ServersViewModel = hiltViewModel(),
    onServerSelected: (VpnServer) -> Unit
) {
    ServersTheme {
        // Collect state as a Flow and convert to State for Compose
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Select Server") },
                    navigationIcon = {
                        IconButton(onClick = { /* TODO: Navigate back */ }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
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
                when {
                    uiState.isLoading && uiState.servers.isEmpty() -> LoadingContent()
                    uiState.error != null -> ErrorContent(
                        message = uiState.error,
                        onRetryClicked = { viewModel.loadServers() }
                    )
                    uiState.servers.isEmpty() -> EmptyContent(
                        onRetryClicked = { viewModel.loadServers() }
                    )
                    else -> ServersList(
                        servers = uiState.servers,
                        onServerSelected = onServerSelected
                    )
                }
            }
        }
    }
}

/**
 * Content shown while loading servers.
 */
@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Loading servers...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Content shown when no servers are available.
 */
@Composable
fun EmptyContent(
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_dialog_alert),
            contentDescription = "No Servers Icon",
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "No servers available",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Please check your connection and try again",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            textAlign = align.Center
        )

        Button(
            onClick = { onRetryClicked() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("RETRY")
        }
    }
}

/**
 * List of servers.
 */
@Composable
fun ServersList(
    servers: List<VpnServer>,
    onServerSelected: (VpnServer) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(servers) { server ->
            ServerItem(
                server = server,
                onClick = { onServerSelected(server) }
            )
            Divider()
        }
    }
}

/**
 * Individual server item.
 */
@Composable
fun ServerItem(
    server: VpnServer,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = server.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${server.city}, ${server.country}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            server.loadPercentage?.let { load ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Load: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$load%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (load < 50) MaterialTheme.colorScheme.primary else
                                if (load < 80) MaterialTheme.colorScheme.secondary else
                                MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Status indicator
        when (server.status) {
            "ONLINE" -> {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = "Online",
                    tint = MaterialTheme.colorScheme.success,
                    modifier = Modifier.size(12.dp)
                )
            }
            "MAINTENANCE" -> {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Maintenance",
                    tint = MaterialTheme.colorScheme.warning,
                    modifier = Modifier.size(12.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Helper object for status colors.
 */
private object StatusColors {
    val online = MaterialTheme.colorScheme.success
    val maintenance = MaterialTheme.colorScheme.warning
    val offline = MaterialTheme.colorScheme.error
}