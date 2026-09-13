package com.safetunnel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.compose.rememberNavController
import com.safetunnel.app.ui.theme.SafeTunnelTheme
import com.safetunnel.core.navigation.SafeTunnelNavHost
import com.safetunnel.feature.home.ui.HomeScreen
import com.safetunnel.feature.servers.ui.ServersScreen
import com.safetunnel.feature.settings.ui.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val navController: NavHostController by rememberNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeTunnelTheme {
                // A surface container using the 'background' color from the theme
                SafeTunnelNavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    // Home destination
                    composable("home") {
                        HomeScreen(
                            onNavigateToServers = { navController.navigate("servers") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    // Servers destination
                    composable("servers") {
                        ServersScreen(
                            onServerSelected = { server ->
                                // TODO: Handle server selection - for now just go back
                                navController.popBackStack()
                                // In a real app, we would update the home screen state
                            }
                        )
                    }
                    // Settings destination
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