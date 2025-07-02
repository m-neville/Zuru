package com.example.zuru.screens.authScreen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var rePassword by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var rePasswordVisible by remember { mutableStateOf(false) }

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
                                rePassword = ""
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
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                    trailingIcon = {
//                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                            Icon(imageVector = image, contentDescription = null)
//                        }
//                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = rePassword,
                        onValueChange = { rePassword = it },
                        label = { Text("Re-enter Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (rePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
//                            val image = if (rePasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                            IconButton(onClick = { rePasswordVisible = !rePasswordVisible }) {
//                                Icon(imageVector = image, contentDescription = null)
//                            }
                        }
                    )
                    if (rePassword.isNotEmpty() && rePassword != password) {
                        Text("Passwords do not match", color = Color.Red, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        loading = true
                        val cleanedEmail = email.trim()
                        val cleanedPassword = password.trim()
                        val cleanedRePassword = rePassword.trim()

                        if (cleanedEmail.isBlank() || cleanedPassword.isBlank() ||
                            (selectedTab == 1 && (name.trim().isBlank() || cleanedRePassword.isBlank()))
                        ) {
                            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            loading = false
                            return@Button
                        }

                        if (!Patterns.EMAIL_ADDRESS.matcher(cleanedEmail).matches()) {
                            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
                            loading = false
                            return@Button
                        }

                        if (selectedTab == 1 && cleanedPassword != cleanedRePassword) {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            loading = false
                            return@Button
                        }

                        if (selectedTab == 0) {
                            auth.signInWithEmailAndPassword(cleanedEmail, cleanedPassword)
                                .addOnCompleteListener { task ->
                                    loading = false
                                    if (task.isSuccessful) {
                                        val userId = auth.currentUser?.uid ?: ""
                                        val userEmail = auth.currentUser?.email ?: ""
                                        val userRef = firestore.collection("users").document(userId)

                                        userRef.get().addOnSuccessListener { document ->
                                            if (!document.exists()) {
                                                val profile = hashMapOf("name" to "", "email" to userEmail)
                                                userRef.set(profile)
                                            }
                                        }

                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                        navController.navigate("onboarding") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    } else {
                                        Toast.makeText(context, "Login failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
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
                                                navController.navigate("onboarding") {
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
