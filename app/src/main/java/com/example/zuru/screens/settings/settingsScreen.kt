package com.example.zuru.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zuru.R
import com.example.zuru.screens.viewmodels.SettingsViewModel
import com.example.zuru.ui.theme.AppThemeIdentifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val selectedTheme by settingsViewModel.selectedThemeIdentifier.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(auth.currentUser) {
        auth.currentUser?.let { user ->
            name = user.displayName ?: ""
            email = user.email ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionTitle("Account")
            SettingItem("Edit Profile", painterResource(id = R.drawable.edit_text)) { showEditProfileDialog = true }
            SettingItem("About Us", painterResource(id = R.drawable.info)) { navController.navigate("about") }

            HorizontalDivider(modifier = Modifier.padding(8.dp))

            SettingsSectionTitle("Appearance")
            SwitchSettingItem(
                title = "Dark Mode",
                icon = painterResource(id = R.drawable.brightness),
                checked = isDarkMode,
                onCheckedChanged = { settingsViewModel.setDarkMode(it) }
            )

            ThemeSelector(currentThemeId = selectedTheme.id, onThemeSelected = {
                settingsViewModel.setSelectedTheme(AppThemeIdentifier.fromId(it))
            })

            HorizontalDivider(modifier = Modifier.padding(8.dp))

            SettingsSectionTitle("Privacy")
            SettingItem("Delete Account", painterResource(id = R.drawable.delete), titleColor = MaterialTheme.colorScheme.error) {
                showDeleteDialog = true
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    auth.signOut()
                    navController.navigate("auth") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(painterResource(id = R.drawable.logout), contentDescription = "Logout")
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }

        if (showEditProfileDialog) {
            EditProfileDialog(
                currentName = name,
                currentEmail = email,
                onDismiss = { showEditProfileDialog = false },
                onSave = { newName, newEmail, newPassword ->
                    auth.currentUser?.let { user ->
                        user.updateEmail(newEmail)
                        if (newPassword.isNotBlank()) user.updatePassword(newPassword)
                        firestore.collection("users").document(user.uid)
                            .update(mapOf("name" to newName, "email" to newEmail))
                    }
                    showEditProfileDialog = false
                }
            )
        }

        if (showDeleteDialog) {
            ConfirmationDialog(
                title = "Delete Account?",
                text = "This action cannot be undone.",
                confirmButtonText = "Delete",
                onConfirm = {
                    auth.currentUser?.delete()
                    navController.navigate("auth") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SettingItem(title: String, icon: Painter, titleColor: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(16.dp))
        Text(title, color = titleColor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SwitchSettingItem(title: String, icon: Painter, checked: Boolean, onCheckedChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.width(16.dp))
        Text(title, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChanged)
    }
}

@Composable
fun ThemeSelector(currentThemeId: String, onThemeSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(id = R.drawable.palette), contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(8.dp))
            Text("App Theme")
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(AppThemeIdentifier.entries.toTypedArray()) { theme ->
//                ThemeChip(theme.id, theme.color, theme.id == currentThemeId) {
//                    onThemeSelected(theme.id)
//                }
            }
        }
    }
}

@Composable
fun ThemeChip(id: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.outline, CircleShape) else Modifier)
    )
}

@Composable
fun ConfirmationDialog(title: String, text: String, confirmButtonText: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismiss()
            }) { Text(confirmButtonText) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(currentName: String, currentEmail: String, onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") })
            }
        },
        confirmButton = { Button(onClick = { onSave(name, email, password) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )

}
