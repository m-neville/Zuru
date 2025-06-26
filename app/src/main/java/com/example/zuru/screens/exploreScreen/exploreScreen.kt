package com.example.zuru.screens.exploreScreen

import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    val context = LocalContext.current

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var displayDestinationName by remember { mutableStateOf("") }
    var searchedLocationName by remember { mutableStateOf("") } // New state to store the original search query

    val kenyaLatLng = LatLng(-1.286389, 36.817223)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kenyaLatLng, 6f)
    }

    LaunchedEffect(selectedLatLng) {
        selectedLatLng?.let { latLng ->
            displayDestinationName = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val address = addresses?.firstOrNull()

                    val featureName = address?.featureName
                    val addressLine = address?.getAddressLine(0)?.split(",")?.firstOrNull()
                    val locality = address?.locality ?: ""

                    val finalName = when {
                        featureName.isNullOrBlank() -> addressLine
                        featureName.contains("+") || featureName.startsWith("P") -> addressLine
                        else -> featureName
                    }

                    listOfNotNull(finalName, locality)
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                        .ifEmpty { "Unknown Location" }

                } catch (e: Exception) {
                    "Unknown Location"
                }
            }
        }
    }

    fun searchLocation() {
        if (searchQuery.isNotBlank()) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(searchQuery, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    selectedLatLng = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)

                    // Store the original search query as the location name
                    searchedLocationName = searchQuery.trim()
                    // Also update display name for the marker
                    displayDestinationName = searchQuery.trim()
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error searching location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Explore Destinations") })
        },
        bottomBar = {
            val destinationToShow = if (searchedLocationName.isNotEmpty()) searchedLocationName else displayDestinationName
            if (destinationToShow.isNotEmpty() && destinationToShow != "Unknown Location") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Selected: $destinationToShow",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = {
                            val booking = hashMapOf(
                                "destination" to destinationToShow, // Use the appropriate name
                                "user" to FirebaseAuth.getInstance().currentUser?.email,
                                "timestamp" to System.currentTimeMillis()
                            )

                            FirebaseFirestore.getInstance().collection("bookings")
                                .add(booking)
                                .addOnSuccessListener {
                                    navController.navigate("payments/$destinationToShow")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to book trip", Toast.LENGTH_SHORT).show()
                                }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Book Trip to $destinationToShow")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                        searchedLocationName = "" // Clear search name when clicking on map
                    }
                ) {
                    selectedLatLng?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = if (searchedLocationName.isNotEmpty()) searchedLocationName else displayDestinationName,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 4.dp
                ) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Location") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { searchLocation() })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { searchLocation() }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            }
        }
    }
}