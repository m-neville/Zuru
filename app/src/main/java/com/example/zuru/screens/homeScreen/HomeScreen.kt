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
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    var showWelcomeText by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // Launch animations on composition
    LaunchedEffect(true) {
        showWelcomeText = true
        delay(500)
        showButtons = true
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.home_bground),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x80000000), Color(0xCC000000))
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
                            color = Color.White,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "Zuru Travel App",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontSize = 32.sp
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
        color = Color(0xFF006400),
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
