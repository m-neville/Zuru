package com.example.zuru.screens.receiptsScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("SimpleDateFormat")
fun ReceiptScreen(
    navController: NavController,
    destination: String,
    method: String,
    amount: String,
    timestamp: Long,
    travelMode: String,
    vehicleType: String,
    dateofTravel: String,
) {
    val decodedDestination = URLDecoder.decode(destination, StandardCharsets.UTF_8.toString())
    val decodedMethod = URLDecoder.decode(method, StandardCharsets.UTF_8.toString())
    val decodedAmount = URLDecoder.decode(amount, StandardCharsets.UTF_8.toString())
    val decodedTravelMode = URLDecoder.decode(travelMode, StandardCharsets.UTF_8.toString())
    val decodedVehicleType = URLDecoder.decode(vehicleType, StandardCharsets.UTF_8.toString())
    val decodedTravelDate = URLDecoder.decode(dateofTravel, StandardCharsets.UTF_8.toString())

    val formattedTimestamp = try {
        SimpleDateFormat("dd MMM yyyy, hh:mm a").format(Date(timestamp))
    } catch (e: Exception) {
        "Invalid Date"
    }

    if (decodedDestination.isBlank() || decodedMethod.isBlank() || decodedAmount.isBlank() || decodedTravelDate.isBlank()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Payment Receipt", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF004D40))
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color.White)))
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Something went wrong. Please try again.", color = Color.Red)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment Receipt", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF004D40))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color.White)))
                .padding(padding)
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Receipt Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    ReceiptItem(label = "Destination", value = decodedDestination)
                    ReceiptItem(label = "Date of Travel", value = decodedTravelDate)
                    ReceiptItem(label = "Travel Mode", value = decodedTravelMode)
                    if (decodedTravelMode == "Road") {
                        ReceiptItem(label = "Vehicle Type", value = decodedVehicleType)
                    }
                    ReceiptItem(label = "Amount", value = "KES $decodedAmount")
                    ReceiptItem(label = "Payment Method", value = decodedMethod)
                    ReceiptItem(label = "Date & Time", value = formattedTimestamp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("receipt/$destination/$method/$amount/$timestamp/$travelMode/$vehicleType/$dateofTravel") {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Text("Back to Home", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}
