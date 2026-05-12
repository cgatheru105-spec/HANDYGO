package com.example.handygo.providerscreens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.handygo.ProfileViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun LocationScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Default location Nairobi or current saved location
    val startLocation = if (profileViewModel.latitude.value != 0.0) {
        LatLng(profileViewModel.latitude.value, profileViewModel.longitude.value)
    } else {
        LatLng(-1.286389, 36.817223)
    }

    // Camera state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 12f)
    }

    // Search text
    var searchQuery by remember { mutableStateOf("") }

    // Marker position
    var markerPosition by remember { 
        mutableStateOf<LatLng?>(
            if (profileViewModel.latitude.value != 0.0) LatLng(profileViewModel.latitude.value, profileViewModel.longitude.value) 
            else null
        ) 
    }

    // Coordinates and address
    var latitude by remember { mutableDoubleStateOf(profileViewModel.latitude.value) }
    var longitude by remember { mutableDoubleStateOf(profileViewModel.longitude.value) }
    var addressName by remember { mutableStateOf(profileViewModel.location.value) }

    // Search loading state
    var isSearching by remember { mutableStateOf(false) }

    // Permission state
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request permission automatically
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Function to get address from LatLng
    fun updateAddressFromLatLng(latLng: LatLng) {
        coroutineScope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                }
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    addressName = address.getAddressLine(0) ?: "Unknown Location"
                }
            } catch (e: Exception) {
                addressName = "Location pinned"
            }
        }
    }

    // Map properties
    val properties by remember(hasLocationPermission) {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = hasLocationPermission
            )
        )
    }

    // Map UI settings
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                compassEnabled = true
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // GOOGLE MAP
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = properties,
            uiSettings = uiSettings,
            onMapClick = { latLng ->
                markerPosition = latLng
                latitude = latLng.latitude
                longitude = latLng.longitude
                updateAddressFromLatLng(latLng)
                Toast.makeText(context, "Location pinned", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Show marker
            markerPosition?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Business Location",
                    snippet = addressName
                )
            }
        }

        // TOP SEARCH SECTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Set Your Business Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (addressName.isNotEmpty()) {
                        Text(
                            text = addressName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search for place...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (isSearching) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                IconButton(onClick = {
                                    if (searchQuery.isNotEmpty()) {
                                        coroutineScope.launch {
                                            isSearching = true
                                            try {
                                                val geocoder = Geocoder(context, Locale.getDefault())
                                                val addresses = withContext(Dispatchers.IO) {
                                                    @Suppress("DEPRECATION")
                                                    geocoder.getFromLocationName(searchQuery, 1)
                                                }
                                                if (addresses?.isNotEmpty() == true) {
                                                    val address = addresses[0]
                                                    val foundLocation = LatLng(address.latitude, address.longitude)
                                                    markerPosition = foundLocation
                                                    latitude = address.latitude
                                                    longitude = address.longitude
                                                    addressName = address.getAddressLine(0) ?: searchQuery
                                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(foundLocation, 16f))
                                                } else {
                                                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                            } finally {
                                                isSearching = false
                                            }
                                        }
                                    }
                                }) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // FINISH BUTTON
        Button(
            onClick = {
                // Save to ViewModel
                profileViewModel.updateLocation(
                    newLocation = if (addressName.isNotEmpty()) addressName else "Pinned Location",
                    newLat = latitude,
                    newLng = longitude
                )
                
                // Return to registration or previous screen
                navController.popBackStack()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            enabled = markerPosition != null,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = if (markerPosition == null) "Pin Your Location" else "Confirm Location", fontSize = 18.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    LocationScreen(navController = rememberNavController())
}
