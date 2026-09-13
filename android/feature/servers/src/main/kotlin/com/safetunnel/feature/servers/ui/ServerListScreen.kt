package com.safetunnel.feature.servers.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.safetunnel.feature.servers.ServerResponse
import com.safetunnel.feature.servers.ServersViewModel
import com.safetunnel.feature.servers.ui.ServerListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(navController: NavController = rememberNavController(), viewModel: ServersViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.refresh() },
                        modifier = Modifier.top(8.dp)
                    ) {
                        Text("Retry")
                    }
                }
            }
            else -> {
                // If there are no servers, show a message
                if (uiState.servers.isEmpty()) {
                    Text(
                        text = "No servers available",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(uiState.servers) { server ->
                            ServerItem(
                                server = server,
                                onClick = {
                                    // TODO: Navigate to server detail or connect
                                    // For now, just show a toast or do nothing
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerItem(server: ServerResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text(
                    text = server.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${server.city}, ${server.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Latency indicator (stub using loadPercentage)
            Column(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalAlignment = Alignment.End
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Latency",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${server.loadPercentage} ms",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        )
    }
}