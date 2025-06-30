package com.example.zuru.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zuru.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Welcome to Zuru") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.pics), // Replace with your image resource
                contentDescription = "App Logo",
                modifier = Modifier.size(350.dp) // Adjust size as needed
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text("Plan and Pay for Your Trips with Ease!", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Explore destinations, make secure payments, and view your travel receipts.", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }) {
                Text("Continue to Home")
            }
        }
    }
}
