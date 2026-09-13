package com.safetunnel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safetunnel.app.ui.HomeScreen
import com.safetunnel.app.ui.ServersScreen
import com.safetunnel.app.ui.SettingsScreen
import com.safetunnel.core.designsystem.ui.theme.SafeTunnelTheme
import com.safetunnel.core.navigation.SafeTunnelNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeTunnelTheme {
                val navController = rememberNavController()
                SafeTunnelNavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            onNavigateToServers = { navController.navigate("servers") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("servers") {
                        ServersScreen(
                            onServerSelected = { server ->
                                navController.popBackStack()
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}