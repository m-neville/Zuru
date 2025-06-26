package com.example.zuru.screens.booking_confirmation



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(navController: NavController, destination: String?) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Trip Booked") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Successfully booked your trip to ${destination ?: "Unknown"}!")
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Return Home")
            }
        }
    }
}