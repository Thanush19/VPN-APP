package com.safetunnel.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Basic NavHost setup for the application.
 * Destinations will be added by feature modules.
 *
 * @param navController The NavHostController to manage navigation.
 * @param startDestination The starting route for the NavHost.
 * @param modifier Modifier to apply to the NavHost.
 * @param routeBuilder Lambda to build the navigation graph with destinations.
 */
@Composable
fun SafeTunnelNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    routeBuilder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        routeBuilder()
    }
}

/**
 * Extension to add destinations to the navigation graph.
 */
@Composable
fun NavGraphBuilder.destination(
    route: String,
    content: @Composable () -> Unit
) {
    composable(route) {
        content()
    }
}