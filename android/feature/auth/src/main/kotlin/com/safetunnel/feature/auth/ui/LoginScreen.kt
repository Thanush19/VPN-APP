package com.safetunnel.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.safetunnel.feature.auth.AuthViewModel
import com.safetunnel.feature.auth.AuthState
import com.safetunnel.feature.auth.AuthRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            if (state.isSuccess) {
                // Navigate to Home after successful login
                LaunchedEffect(Unit) {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            }
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
