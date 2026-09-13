package com.safetunnel.feature.settings.ui

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
import com.safetunnel.feature.settings.data.*
import com.safetunnel.feature.settings.ui.theme.SettingsTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * Screen displaying application settings.
 *
 * @param viewModel The SettingsViewModel to observe state and handle intents.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsTheme {
        // Collect state as a Flow and convert to State for Compose
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Settings") },
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
                    uiState == SettingsUiState(autoConnect = false, darkMode = true, version = "1.0.0") && !viewModel::loadSettings.isInitialized -> LoadingContent()
                    // In a real app, we would have a loading state, but for now we assume settings load instantly.
                    else -> SettingsContent(
                        autoConnect = uiState.autoConnect,
                        darkMode = uiState.darkMode,
                        version = uiState.version,
                        onAutoConnectToggle = { viewModel.handleIntent(SettingsIntent.ToggleAutoConnect(it)) },
                        onDarkModeToggle = { viewModel.handleIntent(SettingsIntent.ToggleDarkMode(it)) },
                        onRetryClicked = { viewModel.handleIntent(SettingsIntent.Retry) }
                    )
                }
            }
        }
    }
}

/**
 * Content shown while loading settings (placeholder).
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
            text = "Loading settings...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Main settings content.
 */
@Composable
fun SettingsContent(
    autoConnect: Boolean,
    darkMode: Boolean,
    version: String,
    onAutoConnectToggle: (Boolean) -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onRetryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalSpacing = 24.dp
    ) {
        // Auto Connect Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Auto Connect",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = autoConnect,
                onChange = { onAutoConnectToggle(it) },
                thumbContent = { /* default thumb */ }
            )
        }
        Divider()

        // Dark Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = darkMode,
                onChange = { onDarkModeToggle(it) },
                thumbContent = { /* default thumb */ }
            )
        }
        Divider()

        // Version Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Version",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = version,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Divider()

        // Retry Button (for demonstration)
        Button(
            onClick = { onRetryClicked() },
            modifier = Modifier.width(200.dp)
                .align(Alignment.End)
        ) {
            Text("RETRY")
        }
    }
}