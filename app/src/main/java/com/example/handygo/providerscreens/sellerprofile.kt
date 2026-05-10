package com.example.handygo.providerscreens

import android.widget.Toast
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.handygo.ProfileViewModel
<<<<<<< HEAD
import com.google.firebase.database.FirebaseDatabase
=======
import com.example.handygo.ProviderPost
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    navController: NavHostController, 
<<<<<<< HEAD
    profileViewModel: ProfileViewModel = viewModel()
=======
    profileViewModel: ProfileViewModel = viewModel(),
    sellerName: String? = null,
    category: String = "Professional Service Provider"
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
) {
    val sellerName = profileViewModel.selectedSellerName.value.ifBlank { "Provider Name" }
    val category = profileViewModel.selectedSellerCategory.value.ifBlank { "Service Provider" }
    val location = profileViewModel.selectedSellerLocation.value.ifBlank { "Location Unknown" }
    val cost = profileViewModel.selectedSellerPrice.value.ifBlank { "Negotiable" }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // If no sellerName is passed, we show the current profile
    val displayName = sellerName ?: profileViewModel.name.value
    val displayLocation = if (sellerName == null) profileViewModel.location.value else "Nairobi, Kenya"
    val displayBio = if (sellerName == null) profileViewModel.bio.value else "Professional services with guaranteed quality."
    val profileImage = if (sellerName == null) profileViewModel.profileImageUri.value else null
    val contact = if (sellerName == null) profileViewModel.contact.value else "+254 700 123 456"
    
    val myProducts = profileViewModel.marketplaceProducts.filter { sellerName == null || it.sellerName == sellerName || it.sellerName == "You" }
    val myPosts = profileViewModel.providerPosts // In a real app, filter by sellerId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
<<<<<<< HEAD
            Box(
                modifier = Modifier
                    .size(120.dp)
=======
            // Profile Image
            Box(
                modifier = Modifier
                    .size(100.dp)
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
<<<<<<< HEAD
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
=======
                if (profileImage != null) {
                    AsyncImage(
                        model = profileImage,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
            }

            Spacer(modifier = Modifier.height(16.dp))

<<<<<<< HEAD
            Text(text = sellerName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "4.8 (Recent Rating)", fontSize = 14.sp, color = Color.Gray)
=======
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = category,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                Text(text = " 4.8 (150 reviews)", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = displayBio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Contact and Location Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(icon = Icons.Default.LocationOn, label = "Base Location", value = displayLocation)
                    InfoRow(icon = Icons.Default.Phone, label = "Contact number", value = contact)
                }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
            }

            Spacer(modifier = Modifier.height(24.dp))

<<<<<<< HEAD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(icon = Icons.Default.Build, label = "Service", value = category)
                    DetailRow(icon = Icons.Default.LocationOn, label = "Location", value = location)
                    DetailRow(icon = Icons.Default.AttachMoney, label = "Starting Price", value = cost)
=======
            // Marketplace Items Section
            if (myProducts.isNotEmpty()) {
                Text(
                    text = "Marketplace Products",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(myProducts) { product ->
                        Card(
                            modifier = Modifier.size(160.dp, 200.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(100.dp).background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (product.imageUri != null) {
                                        AsyncImage(model = product.imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } else {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(text = product.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 14.sp)
                                    Text(text = product.price, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Service Posts Section
            if (myPosts.isNotEmpty()) {
                Text(
                    text = "Service Updates & Posts",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    myPosts.forEach { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = post.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text(text = post.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = post.description, style = MaterialTheme.typography.bodyMedium)
                                if (post.imageUri != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    AsyncImage(
                                        model = post.imageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
<<<<<<< HEAD
                    // SENDING DATA TO FIREBASE
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("users")
                    myRef.setValue("Hello")

=======
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
                    val newNotification = ServiceRequest(
                        id = System.currentTimeMillis().toString(),
                        userName = profileViewModel.name.value,
                        serviceType = category,
                        description = "New Booking Request",
                        time = "Just now"
                    )
<<<<<<< HEAD
                    profileViewModel.addServiceRequest(newNotification, profileViewModel.selectedSellerId.value)
                    Toast.makeText(context, "Booking Sent to $sellerName!", Toast.LENGTH_LONG).show()
=======
                    profileViewModel.addServiceRequest(newNotification)
                    Toast.makeText(context, "Booking Sent to $displayName!", Toast.LENGTH_LONG).show()
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("BOOK THIS PROVIDER", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
