package com.example.zuru.screens.confirmationScreen



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(navController: NavController) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("raw/success_animation.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    // Automatically navigate after a delay
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("home") {
            popUpTo("confirmation") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Payment Successful") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text("Your booking has been confirmed!", style = MaterialTheme.typography.titleMedium)
        }
    }
}
