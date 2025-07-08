package com.example.zuru.screens.aboutScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import com.example.zuru.R // Replace with your actual resource path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Us") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // App Icon or Placeholder Avatar
            Spacer(modifier = Modifier.height(16.dp))
//            Icon(
//                painter = painterResource(id = R.drawable.zuru_logo), // Replace with your logo drawable
//                contentDescription = "Zuru Logo",
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(RoundedCornerShape(20.dp)),
//                tint = MaterialTheme.colorScheme.primary
//            )

            Spacer(modifier = Modifier.height(24.dp))

            // Content Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome to Zuru",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        text = "Zuru is a smart and convenient travel app designed to help you explore Kenya with ease. Whether you're planning a road trip, a weekend getaway, or a city adventure, Zuru connects you with travel options, lets you book destinations, and securely pay via M-PESA or other methods — all from your phone.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "What You Can Do with Zuru:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text("• Discover exciting destinations across Kenya", style = MaterialTheme.typography.bodyMedium)
                        Text("• Book and pay for your travel seamlessly", style = MaterialTheme.typography.bodyMedium)
                        Text("• View your bookings, payments, and receipts", style = MaterialTheme.typography.bodyMedium)
                        Text("• Manage your profile and preferences", style = MaterialTheme.typography.bodyMedium)
                        Text("• Enjoy a smooth and user-friendly experience", style = MaterialTheme.typography.bodyMedium)
                    }

                    Text(
                        text = "Our Mission",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Text(
                        text = "We aim to make travel planning easier, more accessible, and more enjoyable for everyone. With Zuru, you don’t just travel — you explore.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Thank you for choosing Zuru!",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
