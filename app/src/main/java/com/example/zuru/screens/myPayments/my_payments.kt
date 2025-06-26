package com.example.zuru.screens.myPayments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPaymentsScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val db = FirebaseFirestore.getInstance()

    var payments by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(user?.email) {
        user?.email?.let { email ->
            db.collection("payments")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    val list = documents.map { it.data }
                    payments = list
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Payments", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color(0xFFF1F8F6))
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (payments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No payments found.", color = Color.DarkGray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(payments) { payment ->
                        PaymentCard(payment)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentCard(payment: Map<String, Any>) {
    val timestamp = payment["timestamp"] as? Timestamp
    val formattedDate = timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Trip to: ${payment["destination"] ?: "Unknown"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00332C)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Method: ${payment["method"] ?: "N/A"}",
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = "Amount: KES ${payment["amount"] ?: "N/A"}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF004D40)
            )

            formattedDate?.let {
                Text(
                    text = "Paid on: $it",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
