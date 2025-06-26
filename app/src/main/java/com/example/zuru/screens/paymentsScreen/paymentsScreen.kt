package com.example.zuru.screens.paymentsScreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.zuru.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController, destination: String) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    val paymentMethods = listOf("M-PESA", "PayPal", "Card")
    val travelModes = listOf("SGR", "Road", "Flight")
    val availableVehicles = listOf("Car", "Matatu", "Bus")

    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }
    var selectedTravelMode by remember { mutableStateOf(travelModes[0]) }
    var selectedVehicle by remember { mutableStateOf(availableVehicles[0]) }

    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }
    var showAnimation by remember { mutableStateOf(false) }
    var paymentSuccessful by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation))
    val progress by animateLottieCompositionAsState(composition, isPlaying = true, iterations = 1)

    if (paymentSuccessful) {
        LaunchedEffect(Unit) {
            val method = selectedMethod
            val travelMode = selectedTravelMode
            val vehicleType = if (selectedTravelMode == "Road") selectedVehicle else "N/A"
            val dateofTravel = selectedDate
            val timestamp = Timestamp.now().toDate().time

            val encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8.toString())
            val encodedMethod = URLEncoder.encode(method, StandardCharsets.UTF_8.toString())
            val encodedAmount = URLEncoder.encode(amount, StandardCharsets.UTF_8.toString())
            val encodedTravelMode = URLEncoder.encode(travelMode, StandardCharsets.UTF_8.toString())
            val encodedVehicleType = URLEncoder.encode(vehicleType, StandardCharsets.UTF_8.toString())
            val encodedDateofTravel = URLEncoder.encode(dateofTravel, StandardCharsets.UTF_8.toString())

            delay(2500)

            navController.navigate(
                "receipt/$encodedDestination/$encodedMethod/$encodedAmount/$timestamp/$encodedTravelMode/$encodedVehicleType/$encodedDateofTravel"
            ) {
                popUpTo("payments/$destination") { inclusive = true }
            }

            paymentSuccessful = false
            showAnimation = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Payment", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00796B))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(Color(0xFFE0F7FA), Color.White)))
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Complete your payment for the trip to $destination", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))

                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = {},
                        label = { Text("Date of Travel") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date",
                                modifier = Modifier.clickable { showDatePicker = true }
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                            selectedDate = sdf.format(Date(millis))
                                        }
                                        showDatePicker = false
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    DropdownField("Travel Mode", selectedTravelMode, travelModes) {
                        selectedTravelMode = it
                        if (it != "Road") selectedVehicle = availableVehicles[0]
                    }

                    if (selectedTravelMode == "Road") {
                        DropdownField("Vehicle Type", selectedVehicle, availableVehicles) {
                            selectedVehicle = it
                        }
                    }

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (KES)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownField("Payment Method", selectedMethod, paymentMethods) {
                        selectedMethod = it
                    }

                    Button(
                        onClick = {
                            when {
                                amount.isBlank() -> Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                                selectedDate.isBlank() -> Toast.makeText(context, "Please select travel date", Toast.LENGTH_SHORT).show()
                                else -> showConfirmDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        Text("Pay Now", color = Color.White)
                    }
                }
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showConfirmDialog = false
                            showAnimation = true

                            val paymentData = hashMapOf(
                                "email" to user?.email,
                                "destination" to destination,
                                "method" to selectedMethod,
                                "amount" to amount,
                                "dateofTravel" to selectedDate,
                                "travelMode" to selectedTravelMode,
                                "vehicleType" to if (selectedTravelMode == "Road") selectedVehicle else "N/A",
                                "timestamp" to Timestamp.now()
                            )

                            firestore.collection("payments")
                                .add(paymentData)
                                .addOnSuccessListener {
                                    paymentSuccessful = true
                                }
                                .addOnFailureListener {
                                    showAnimation = false
                                    Toast.makeText(context, "Payment failed", Toast.LENGTH_SHORT).show()
                                }
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Confirm Payment") },
                    text = {
                        Column {
                            Text("Destination: $destination")
                            Text("Date of Travel: $selectedDate")
                            Text("Travel Mode: $selectedTravelMode")
                            if (selectedTravelMode == "Road") {
                                Text("Vehicle Type: $selectedVehicle")
                            }
                            Text("Amount: KES $amount")
                            Text("Payment Method: $selectedMethod")
                        }
                    }
                )
            }

            if (showAnimation) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, color = Color.Black)
        Box {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                label = { Text(label, color = Color.Black) },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
