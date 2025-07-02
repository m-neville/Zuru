package com.example.zuru.screens.upcoming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zuru.screens.receiptsScreen.ReceiptItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpcomingTripsScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var upcomingTrips by remember { mutableStateOf<List<ReceiptItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayAtMidnight = calendar.time

    LaunchedEffect(user) {
        if (user?.email == null) {
            errorMessage = "User not logged in."
            isLoading = false
            return@LaunchedEffect
        }

        firestore.collection("payments")
            .whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                val allTrips = documents.mapNotNull { doc ->
                    try {
                        doc.toObject(ReceiptItem::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val todayDate = sdf.parse(today)

                upcomingTrips = allTrips.filter { trip ->
                    try {
                        val tripDate = sdf.parse(trip.dateofTravel)
                        tripDate != null && tripDate.after(todayAtMidnight)
                    } catch (e: Exception) {
                        false
                    }
                }.sortedBy { sdf.parse(it.dateofTravel) }

                isLoading = false
            }
            .addOnFailureListener {
                errorMessage = "Failed to load trips: ${it.message}"
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Trips", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00796B))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE0F2F1), Color.White)
                    )
                )
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF00796B))
                    }
                }
                errorMessage != null -> {
                    Text(errorMessage ?: "Unknown error", color = Color.Red)
                }
                upcomingTrips.isEmpty() -> {
                    Text("No upcoming trips found.", color = Color.Gray)
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(upcomingTrips) { trip ->
                            UpcomingTripCard(trip)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpcomingTripCard(trip: ReceiptItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trip to ${trip.destination}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF004D40)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Date of Travel: ${trip.dateofTravel}", color = Color.DarkGray)
            Text("Travel Mode: ${trip.travelMode}", color = Color.DarkGray)
            if (trip.travelMode == "Road") {
                Text("Vehicle Type: ${trip.vehicleType}", color = Color.DarkGray)
            }
            Text("Amount: KES ${trip.amount}", color = Color.DarkGray)
            Text("Method: ${trip.method}", color = Color.DarkGray)
        }
    }
}
