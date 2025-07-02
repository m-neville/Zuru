package com.example.zuru

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.zuru.screens.navigation.NavGraph
import com.example.zuru.screens.viewmodels.SettingsViewModel
import com.example.zuru.ui.theme.ZuruAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firestore offline persistence
        val firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = firestoreSettings

        // Set the Jetpack Compose content
        setContent {
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val currentThemeIdentifier by settingsViewModel.selectedThemeIdentifier.collectAsState()

            ZuruAppTheme(
                darkTheme = isDarkMode,
                themeIdentifier = currentThemeIdentifier
            ) {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
