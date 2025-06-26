package com.example.zuru.screens.authScreen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AuthScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Login", "Sign Up")

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient)
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to Zuru",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                email = ""
                                password = ""
                                name = ""
                            },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        loading = true
                        val cleanedEmail = email.trim()
                        val cleanedPassword = password.trim()

                        if (cleanedEmail.isBlank() || cleanedPassword.isBlank() || (selectedTab == 1 && name.trim().isBlank())) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            loading = false
                            return@Button
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(cleanedEmail).matches()) {
                            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                            loading = false
                            return@Button
                        }

                        if (selectedTab == 0) {
                            // Login
                            auth.signInWithEmailAndPassword(cleanedEmail, cleanedPassword)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid ?: ""
                                        val userEmail = auth.currentUser?.email ?: ""
                                        val userRef = firestore.collection("users").document(userId)

                                        userRef.get().addOnSuccessListener { document ->
                                            if (!document.exists()) {
                                                val profile = hashMapOf(
                                                    "name" to "",
                                                    "email" to userEmail
                                                )
                                                userRef.set(profile)
                                            }
                                        }

                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, "Login failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            // Sign up
                            auth.createUserWithEmailAndPassword(cleanedEmail, cleanedPassword)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid ?: ""
                                        val userMap = hashMapOf(
                                            "name" to name.trim(),
                                            "email" to cleanedEmail
                                        )
                                        firestore.collection("users").document(userId)
                                            .set(userMap)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Signup successful", Toast.LENGTH_SHORT).show()
                                                navController.navigate("home") {
                                                    popUpTo("auth") { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Failed to save user: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "Signup failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !loading
                ) {
                    Text(text = if (selectedTab == 0) "Login" else "Create Account")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
