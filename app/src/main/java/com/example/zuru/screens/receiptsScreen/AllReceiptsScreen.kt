package com.example.zuru.screens.receiptsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

// ✅ Updated data class
data class ReceiptItem(
    val id: String = "",
    val destination: String = "",
    val method: String = "",
    val amount: String = "",
    val timestamp: Timestamp? = null,
    val dateofTravel: String = "",
    val travelMode: String = "",
    val vehicleType: String = "",
    val tripType: String = "One-way",
    val returnDate: String = "N/A"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllReceiptsScreen(navController: NavController) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var receiptsList by remember { mutableStateOf<List<ReceiptItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedTravelMode by remember { mutableStateOf("All") }
    val travelModes = listOf("All", "Road", "Flight", "SGR")

    LaunchedEffect(currentUser) {
        if (currentUser?.email == null) {
            errorMessage = "User not logged in."
            isLoading = false
            return@LaunchedEffect
        }

        firestore.collection("payments")
            .whereEqualTo("email", currentUser.email)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                receiptsList = documents.mapNotNull { doc ->
                    try {
                        ReceiptItem(
                            id = doc.id,
                            destination = doc.getString("destination") ?: "",
                            method = doc.getString("method") ?: "",
                            amount = doc.get("amount")?.toString() ?: "",
                            timestamp = doc.getTimestamp("timestamp"),
                            dateofTravel = doc.getString("dateofTravel") ?: "",
                            travelMode = doc.getString("travelMode") ?: "",
                            vehicleType = doc.getString("vehicleType") ?: "",
                            tripType = doc.getString("tripType") ?: "One-way",
                            returnDate = doc.getString("returnDate") ?: "N/A"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                isLoading = false
            }
            .addOnFailureListener {
                errorMessage = "Failed to load receipts: ${it.message}"
                isLoading = false
            }
    }

    val filteredReceipts = receiptsList.filter {
        (selectedTravelMode == "All" || it.travelMode == selectedTravelMode) &&
                it.destination.contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Receipts", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00796B))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE0F2F1), Color.White)))
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Destination") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Filter by Travel Mode:", color = Color.Black)
                DropdownMenuFilter(
                    options = travelModes,
                    selectedOption = selectedTravelMode,
                    onOptionSelected = { selectedTravelMode = it },
                    label = "Travel Mode"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> CircularProgressIndicator(color = Color(0xFF00796B))
                errorMessage != null -> Text(errorMessage ?: "Unknown error", color = Color.Red)
                filteredReceipts.isEmpty() -> Text("No receipts found.", color = Color.Gray)
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredReceipts) { receipt ->
                            ReceiptCard(receipt = receipt, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuFilter(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit, label: String) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier.width(200.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReceiptCard(receipt: ReceiptItem, navController: NavController) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            val timestampLong = receipt.timestamp?.toDate()?.time ?: System.currentTimeMillis()

            val encodedDestination = URLEncoder.encode(receipt.destination, StandardCharsets.UTF_8.toString())
            val encodedMethod = URLEncoder.encode(receipt.method, StandardCharsets.UTF_8.toString())
            val encodedAmount = URLEncoder.encode(receipt.amount, StandardCharsets.UTF_8.toString())
            val encodedTravelMode = URLEncoder.encode(receipt.travelMode, StandardCharsets.UTF_8.toString())
            val encodedVehicleType = URLEncoder.encode(receipt.vehicleType.ifEmpty { "N/A" }, StandardCharsets.UTF_8.toString())
            val encodedDateOfTravel = URLEncoder.encode(receipt.dateofTravel, StandardCharsets.UTF_8.toString())
            val encodedTripType = URLEncoder.encode(receipt.tripType, StandardCharsets.UTF_8.toString())
            val encodedReturnDate = URLEncoder.encode(receipt.returnDate.ifEmpty { "N/A" }, StandardCharsets.UTF_8.toString())

            navController.navigate(
                "receipt/$encodedDestination/$encodedMethod/$encodedAmount/$timestampLong/$encodedTravelMode/$encodedVehicleType/$encodedDateOfTravel/$encodedTripType/$encodedReturnDate"
            )
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trip to ${receipt.destination}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF004D40)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Trip Type: ${receipt.tripType}", color = Color.DarkGray)
            Text("Date of Travel: ${receipt.dateofTravel}", color = Color.DarkGray)
            if (receipt.tripType == "Round-trip") {
                Text("Return Date: ${receipt.returnDate}", color = Color.DarkGray)
            }
            Text("Travel Mode: ${receipt.travelMode}", color = Color.DarkGray)
            Text("Amount: KES ${receipt.amount}", color = Color.DarkGray)
            Text("Method: ${receipt.method}", color = Color.DarkGray)
            receipt.timestamp?.toDate()?.let {
                Text("Date: ${dateFormatter.format(it)}", color = Color.Gray)
            }
        }
    }
}
