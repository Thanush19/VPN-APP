package com.safetunnel.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToServers: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to SafeTunnel",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigateToServers() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("Select Server")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onNavigateToSettings() },
            modifier = Modifier.width(200.dp)
        ) {
            Text("Settings")
        }
    }
}