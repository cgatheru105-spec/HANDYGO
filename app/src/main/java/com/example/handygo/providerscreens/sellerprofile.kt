package com.example.handygo.providerscreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.handygo.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    navController: NavHostController, 
    profileViewModel: ProfileViewModel = viewModel()
) {
    // Extract dynamic data from the shared ViewModel
    val sellerName = profileViewModel.selectedSellerName.value.ifBlank { "Provider Name" }
    val category = profileViewModel.selectedSellerCategory.value.ifBlank { "Service Provider" }
    val location = profileViewModel.selectedSellerLocation.value.ifBlank { "Location Unknown" }
    val cost = profileViewModel.selectedSellerPrice.value.ifBlank { "Negotiable" }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share Logic */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name and Rating
            Text(text = sellerName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "4.8 (120 reviews)", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Details Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(icon = Icons.Default.Build, label = "Service", value = category)
                    DetailRow(icon = Icons.Default.LocationOn, label = "Location", value = location)
                    DetailRow(icon = Icons.Default.AttachMoney, label = "Starting Price", value = cost)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bio Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("About Me", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Professional $category with over 5 years of experience. I guarantee quality work and timely delivery. Feel free to book my services.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Work Portfolio
            Text("Recent Work", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(5) { index ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image, 
                            contentDescription = "Work sample $index",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Booking Button
            Button(
                onClick = { 
                    val newNotification = ServiceRequest(
                        id = System.currentTimeMillis().toString(),
                        userName = profileViewModel.name.value,
                        serviceType = category,
                        description = "New Booking: User is at ${profileViewModel.location.value}. Reach them at ${profileViewModel.contact.value}",
                        time = "Just now"
                    )
                    profileViewModel.addServiceRequest(newNotification, profileViewModel.selectedSellerId.value)
                    Toast.makeText(context, "Booking Sent to $sellerName!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("BOOK THIS PROVIDER", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "$label:", fontWeight = FontWeight.Medium, color = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true)
@Composable
fun SellerProfilePreview() {
    SellerProfileScreen(rememberNavController())
}
