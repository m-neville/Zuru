package com.example.zuru.screens.destinationDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zuru.screens.Destination
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DestinationDetailsScreen(
    navController: NavController,
    destinationId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    var destination by remember { mutableStateOf<Destination?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(destinationId) {
        firestore.collection("destinations").document(destinationId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    destination = document.toObject(Destination::class.java)?.copy(id = document.id)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        destination != null -> {
            val dest = destination!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(dest.name, style = MaterialTheme.typography.headlineSmall)
                Text(dest.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                Text(dest.description, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))
                Text("Price: KES ${dest.price}", fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController.navigate("payment_screen?destinationId=${dest.id}")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Destination not found.", color = Color.Red)
            }
        }
    }
}
