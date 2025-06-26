package com.example.zuru.screens.splashScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.zuru.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    // Always redirect to auth screen after a short delay
    LaunchedEffect(Unit) {
        delay(2500) // 2.5 seconds delay
        navController.navigate("auth") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // Splash Screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFADD8E6)) // Light Blue
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.zuru_logo),
                contentDescription = "Zuru Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Welcome to Zuru",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
        }
    }
}
