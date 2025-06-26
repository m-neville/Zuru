package com.example.zuru.screens.changePassword

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user == null || user.email.isNullOrEmpty()) {
        Toast.makeText(context, "User not logged in or missing email", Toast.LENGTH_SHORT).show()
        navController.navigate("auth")
        return
    }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Change Password") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (currentPassword.isNotBlank() && newPassword.isNotBlank()) {
                        isLoading = true
                        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                    isLoading = false
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        val exception = updateTask.exception?.message ?: "Unknown error"
                                        Toast.makeText(context, "Failed to update password: $exception", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                isLoading = false
                                val exception = reauthTask.exception?.message ?: "Unknown error"
                                Toast.makeText(context, "Re-authentication failed: $exception", Toast.LENGTH_LONG).show()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Updating..." else "Update Password")
            }
        }
    }
}
