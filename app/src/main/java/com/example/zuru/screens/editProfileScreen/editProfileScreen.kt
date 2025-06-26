package com.example.zuru.screens.editProfileScreen

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user == null) {
        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
        navController.navigate("auth") // Go back to login if needed
        return
    }

    var displayName by remember { mutableStateOf(user.displayName ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Profile") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = currentPassword.isNotEmpty() // Enable only if current password is provided
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var profileUpdated = false
                    var emailUpdated = false
                    var passwordUpdated = false

                    // Update Display Name
                    if (displayName != user.displayName) {
                        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    profileUpdated = true
                                    Toast.makeText(context, "Display Name Updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update display name: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    // Update Email
                    if (email != user.email) {
                        user.updateEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    emailUpdated = true
                                    Toast.makeText(context, "Email Updated. Please verify your new email.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Failed to update email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    // Update Password
                    if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                        // Re-authenticate user first for security reasons if changing password
                        // This is a simplified example. For production, handle re-authentication properly.
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    passwordUpdated = true
                                    Toast.makeText(context, "Password Updated", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to update password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    // Optionally, navigate back or show a combined success message
                    // For simplicity, individual toasts are shown.
                    if (profileUpdated || emailUpdated || passwordUpdated) {
                         navController.popBackStack() // Navigate back if any update was attempted
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = displayName.isNotBlank() || email.isNotBlank() || (currentPassword.isNotEmpty() && newPassword.isNotEmpty())
            ) {
                Text("Save Changes")
            }
        }
    }
}



