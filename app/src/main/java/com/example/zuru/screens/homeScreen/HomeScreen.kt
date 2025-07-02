package com.example.zuru.screens.homeScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zuru.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date // Added
import java.util.Locale

// Data Class for Popup Info
data class UpcomingTripPopupInfo(
    val id: String,
    val destination: String,
    val date: String // This will still be the "dd/MM/yyyy" string for display
)

@Composable
fun HomeScreen(navController: NavController) {
    var showWelcomeText by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // State Variables for the Popup
    var showTripPopup by remember { mutableStateOf(false) }
    var tripForPopup by remember { mutableStateOf<UpcomingTripPopupInfo?>(null) }
    var popupLogicTriggered by remember { mutableStateOf(false) } // To run logic once

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Launch animations on composition (existing logic)
    LaunchedEffect(true) {
        showWelcomeText = true
        delay(500)
        showButtons = true
    }

    // LaunchedEffect to Fetch Next Upcoming Trip for Popup
    LaunchedEffect(Unit) { // Use Unit to run once per composition lifecycle
        val currentUser = auth.currentUser
        if (currentUser?.email != null && !popupLogicTriggered) {
            fetchNextUpcomingTripForPopup(firestore, currentUser.email!!) { tripInfo ->
                if (tripInfo != null) {
                    tripForPopup = tripInfo
                    showTripPopup = true
                }
            }
            popupLogicTriggered = true // Mark that the logic has been triggered
        }
    }


    Box(
        modifier = Modifier // <caret> was here, keeping existing modifiers
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFFFF9C4)) // Light Blue to Cream
                )
            )
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.glasses),
            contentDescription = "Background",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x40000000), Color(0x80000000)) // Adjusted overlay for lighter background
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Welcome Text Section with animation
            Column {
                Spacer(modifier = Modifier.height(64.dp))

                AnimatedVisibility(
                    visible = showWelcomeText,
                    enter = slideInVertically(
                        animationSpec = tween(durationMillis = 700),
                        initialOffsetY = { -100 }
                    ) + fadeIn(animationSpec = tween(700))
                ) {
                    Column {
                        Text(
                            text = "Welcome to",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black, // Changed text color for better readability on light background
                            fontSize = 22.sp
                        )
                        Text(
                            text = "Zuru Travel App",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black, // Changed text color
                            fontSize = 32.sp,
                        )
                    }
                }
            }

            // Buttons Section with slide-in animation
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (showButtons) {
                    AnimatedButton(
                        delayMillis = 0,
                        title = "Explore",
                        icon = painterResource(id = R.drawable.explore)
                    ) { navController.navigate("explore") }

                    AnimatedButton(
                        delayMillis = 100,
                        title = "My Profile",
                        icon = painterResource(id = R.drawable.profile)
                    ) { navController.navigate("profile") }

                    AnimatedButton(
                        delayMillis = 200,
                        title = "Contact Us",
                        icon = painterResource(id = R.drawable.contact_us)
                    ) { navController.navigate("contact") }
                    AnimatedButton(
                        delayMillis = 300,
                        title = "Settings",
                        icon = painterResource(id = R.drawable.settings)
                    ) { navController.navigate("settings") }
                }
            }
        }

        // AlertDialog Composable for Upcoming Trip
        if (showTripPopup && tripForPopup != null) {
            AlertDialog(
                onDismissRequest = {
                    showTripPopup = false
                },
                title = { Text("Upcoming Trip Reminder!") }, // Updated title
                text = {
                    Column {
                        // Updated text to reflect 24-hour condition
                        Text("Your trip to ${tripForPopup!!.destination} on ${tripForPopup!!.date} is within the next 24 hours!")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showTripPopup = false
                        navController.navigate("upcomingTripsScreen") // Ensure this route is correct
                    }) {
                        Text("View Details")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showTripPopup = false
                    }) {
                        Text("Dismiss")
                    }
                },
                shape = RoundedCornerShape(12.dp) // Retaining existing styling for consistency
            )
        }
    }
}

@Composable
fun AnimatedButton(
    delayMillis: Int,
    title: String,
    icon: Painter,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 500),
            initialOffsetY = { it + 100 }
        ) + fadeIn(animationSpec = tween(500))
    ) {
        HomeButton(title = title, icon = icon, onClick = onClick)
    }
}

@Composable
fun HomeButton(title: String, icon: Painter, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        color = Color(0xFF00796B), // Adjusted button color for better contrast
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}

// Helper function to fetch the next upcoming trip (with 24-hour logic)
private fun fetchNextUpcomingTripForPopup(
    db: FirebaseFirestore,
    userEmail: String,
    onResult: (UpcomingTripPopupInfo?) -> Unit
) {
    // Assuming 'dateofTravel' is "dd/MM/yyyy".
    // We assume the trip starts at 00:00:00 on that day for the 24-hour check.
    val displaySdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // For parsing and display

    val now = Calendar.getInstance()
    val currentTimeMillis = now.timeInMillis

    val twentyFourHoursFromNow = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, 24)
    }
    val twentyFourHoursFromNowMillis = twentyFourHoursFromNow.timeInMillis

    db.collection("payments") // Or your trips collection name
        .whereEqualTo("email", userEmail)
        .orderBy("dateofTravel") // Helps get potentially relevant trips; client-side sort is primary
        .limit(10) // Fetch a few candidates
        .get()
        .addOnSuccessListener { documents ->
            var tripToShow: UpcomingTripPopupInfo? = null
            val potentialTrips = mutableListOf<Pair<Date, UpcomingTripPopupInfo>>()

            for (doc in documents) {
                val tripId = doc.id
                val destination = doc.getString("destination")
                val dateOfTravelStr = doc.getString("dateofTravel") // e.g., "25/12/2023"

                if (destination != null && dateOfTravelStr != null) {
                    try {
                        val parsedDateOnly = displaySdf.parse(dateOfTravelStr)
                        if (parsedDateOnly != null) {
                            val tripDateCal = Calendar.getInstance()
                            tripDateCal.time = parsedDateOnly
                            // Set time to 00:00:00 for the trip's start if only date is given
                            tripDateCal.set(Calendar.HOUR_OF_DAY, 0)
                            tripDateCal.set(Calendar.MINUTE, 0)
                            tripDateCal.set(Calendar.SECOND, 0)
                            tripDateCal.set(Calendar.MILLISECOND, 0)

                            val tripStartTimeMillis = tripDateCal.timeInMillis

                            // Condition: Trip start time must be:
                            // 1. After the current time (not in the past)
                            // 2. Before 24 hours from the current time
                            if (tripStartTimeMillis > currentTimeMillis && tripStartTimeMillis < twentyFourHoursFromNowMillis) {
                                potentialTrips.add(
                                    Pair(tripDateCal.time, UpcomingTripPopupInfo(tripId, destination, dateOfTravelStr))
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println("Error parsing date for popup: $dateOfTravelStr for doc ${doc.id} - ${e.message}")
                    }
                }
            }

            // Sort the valid trips by their date to get the soonest one
            potentialTrips.sortBy { it.first } // it.first is the Date object
            tripToShow = potentialTrips.firstOrNull()?.second

            onResult(tripToShow)
        }
        .addOnFailureListener { exception ->
            println("Error fetching trips for popup: ${exception.message}")
            onResult(null)
        }
}