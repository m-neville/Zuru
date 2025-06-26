package com.example.zuru.screens.contactScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.zuru.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(navController: NavHostController) {
    val context = LocalContext.current

    val developerPhone = "+254708911844"
    val developerEmail = "ochiengnevil002@gmail.com"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Us") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00796B),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2F1), Color(0xFFFFFFFF))
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.help_desk),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Need Help?",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00796B),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ContactButton(
                            icon = painterResource(id = R.drawable.phone_call),
                            label = "Call Developer",
                            color = Color(0xFF0B35CE)
                        ) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$developerPhone")
                            }
                            context.startActivity(intent)
                        }

                        ContactButton(
                            icon = painterResource(id = R.drawable.sms),
                            label = "Send SMS",
                            color = Color(0xFF03A9F4)
                        ) {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:$developerPhone")
                                putExtra("sms_body", "Hi, I need help with your app.")
                            }
                            context.startActivity(intent)
                        }

                        ContactButton(
                            icon = painterResource(id = R.drawable.mail),
                            label = "Send Email",
                            color = Color(0xFF9C27B0)
                        ) {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$developerEmail")
                                putExtra(Intent.EXTRA_SUBJECT, "Zuru App Inquiry")
                                putExtra(Intent.EXTRA_TEXT, "Hello, I would like to...")
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactButton(
    icon: Painter,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}
