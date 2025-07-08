package com.example.zuru.screens.exploreScreen

import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var displayDestinationName by remember { mutableStateOf("") }
    var searchedLocationName by remember { mutableStateOf("") }
    var calculatedFare by remember { mutableStateOf<Int?>(null) }

    val kenyaLatLng = LatLng(-1.286389, 36.817223)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kenyaLatLng, 6f)
    }

    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to get current location", Toast.LENGTH_SHORT).show()
        }
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

    fun calculateDistanceInKm(start: LatLng, end: LatLng): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    fun calculateFare(distanceKm: Double): Int {
        val ratePerKm = 4.5
        return round(distanceKm * ratePerKm).toInt()
    }

    fun searchLocation() {
        if (searchQuery.isNotBlank()) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(searchQuery, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val latLng = LatLng(address.latitude, address.longitude)
                    selectedLatLng = latLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 16f)

                    searchedLocationName = searchQuery.trim()
                    displayDestinationName = searchQuery.trim()

                    userLocation?.let { current ->
                        val distance = calculateDistanceInKm(current, latLng)
                        calculatedFare = calculateFare(distance)
                    }
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
            TopAppBar(
                title = { Text("Explore Destinations") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
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
                    Text("Selected: $destinationToShow", style = MaterialTheme.typography.titleMedium)

                    calculatedFare?.let {
                        Text("Fare: KES $it", style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(
                        onClick = {
                            val fare = calculatedFare ?: 0
                            val booking = hashMapOf(
                                "destination" to destinationToShow,
                                "user" to FirebaseAuth.getInstance().currentUser?.email,
                                "timestamp" to System.currentTimeMillis(),
                                "fare" to fare
                            )

                            FirebaseFirestore.getInstance().collection("bookings")
                                .add(booking)
                                .addOnSuccessListener {
                                    navController.navigate("payments/${Uri.encode(destinationToShow)}/$fare")
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
                        searchedLocationName = ""

                        userLocation?.let { current ->
                            val distance = calculateDistanceInKm(current, latLng)
                            calculatedFare = calculateFare(distance)
                        }
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
