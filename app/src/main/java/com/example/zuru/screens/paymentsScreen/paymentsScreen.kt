package com.example.zuru.screens.paymentsScreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(navController: NavController, destination: String, amount: Int) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    val paymentMethod = "M-PESA"
    val travelModes = listOf("SGR", "Road", "Flight")
    val availableVehicles = listOf("Car", "Matatu", "Bus")
    val tripType = listOf("One-way", "Round-trip")

    var selectedMethod by remember { mutableStateOf(paymentMethod) }
    var selectedTravelMode by remember { mutableStateOf(travelModes[0]) }
    var selectedVehicle by remember { mutableStateOf(availableVehicles[0]) }
    var selectedTripType by remember { mutableStateOf(tripType[0]) }

    var selectedDate by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showReturnDatePicker by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    var showAnimation by remember { mutableStateOf(false) }
    var paymentSuccessful by remember { mutableStateOf(false) }

    val finalAmount = if (selectedTripType == "Round-trip") (amount * 2) else amount
    val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(finalAmount)

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
            val encodedAmount = URLEncoder.encode(finalAmount.toString(), StandardCharsets.UTF_8.toString())
            val encodedTravelMode = URLEncoder.encode(travelMode, StandardCharsets.UTF_8.toString())
            val encodedVehicleType = URLEncoder.encode(vehicleType, StandardCharsets.UTF_8.toString())
            val encodedDateofTravel = URLEncoder.encode(dateofTravel, StandardCharsets.UTF_8.toString())
            val encodedTripType = tripType
            val encodedReturnDate = URLEncoder.encode(returnDate.ifEmpty { "N/A" }, StandardCharsets.UTF_8.toString())

            navController.navigate(
                "receipt/$encodedDestination/$encodedMethod/$encodedAmount/$timestamp/$encodedTravelMode/$encodedVehicleType/$encodedDateofTravel/$encodedTripType/$encodedReturnDate"
            )



             {
                popUpTo("payments/$destination/$amount") { inclusive = true }
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

                    DropdownField("Trip Type", selectedTripType, tripType) {
                        selectedTripType = it
                    }

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

                    if (selectedTripType == "Round-trip") {
                        OutlinedTextField(
                            value = returnDate,
                            onValueChange = {},
                            label = { Text("Return Date") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select return date",
                                    modifier = Modifier.clickable { showReturnDatePicker = true }
                                )
                            },
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (showDatePicker || showReturnDatePicker) {
                        val today = remember { Calendar.getInstance() }

                        // Minimum: Tomorrow
                        val minDepartureDate = remember { Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) } }

                        // For return: Must be at least one day after selectedDate
                        val minReturnDate = remember(selectedDate) {
                            val cal = Calendar.getInstance()
                            if (selectedDate.isNotBlank()) {
                                try {
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    cal.time = sdf.parse(selectedDate)!!
                                    cal.add(Calendar.DAY_OF_YEAR, 1)
                                } catch (e: Exception) {
                                    cal.time = Date()
                                    cal.add(Calendar.DAY_OF_YEAR, 2)
                                }
                            } else {
                                cal.time = Date()
                                cal.add(Calendar.DAY_OF_YEAR, 2)
                            }
                            cal
                        }

                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = (if (showReturnDatePicker) minReturnDate else minDepartureDate).timeInMillis,
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                    return utcTimeMillis >= (if (showReturnDatePicker) minReturnDate else minDepartureDate).timeInMillis
                                }
                            }
                        )

                        DatePickerDialog(
                            onDismissRequest = {
                                showDatePicker = false
                                showReturnDatePicker = false
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        val selected = sdf.format(Date(millis))
                                        if (showDatePicker) selectedDate = selected
                                        if (showReturnDatePicker) returnDate = selected
                                    }
                                    showDatePicker = false
                                    showReturnDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                    showReturnDatePicker = false
                                }) {
                                    Text("Cancel")
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
                        value = "KES $formattedAmount",
                        onValueChange = {},
                        label = { Text("Amount") },
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = selectedMethod,
                        onValueChange = { },
                        label = { Text("Payment Method") },
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            when {
                                selectedDate.isBlank() -> Toast.makeText(context, "Please select travel date", Toast.LENGTH_SHORT).show()
                                selectedTripType == "Round-trip" && returnDate.isBlank() -> Toast.makeText(context, "Please select return date", Toast.LENGTH_SHORT).show()
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
                                "amount" to finalAmount,
                                "dateofTravel" to selectedDate,
                                "returnDate" to if (selectedTripType == "Round-trip") returnDate else "N/A",
                                "tripType" to selectedTripType,
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
                            Text("Trip Type: $selectedTripType")
                            Text("Date of Travel: $selectedDate")
                            if (selectedTripType == "Round-trip") Text("Return Date: $returnDate")
                            Text("Travel Mode: $selectedTravelMode")
                            if (selectedTravelMode == "Road") Text("Vehicle Type: $selectedVehicle")
                            Text("Amount: KES $formattedAmount")
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
