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
import androidx.compose.ui.layout.ContentScale
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
=======
import com.example.handygo.ProviderPost
import com.google.firebase.database.FirebaseDatabase
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    navController: NavHostController, 
    profileViewModel: ProfileViewModel = viewModel()
) {
    val sellerName = profileViewModel.selectedSellerName.value.ifBlank { "Provider Name" }
    val category = profileViewModel.selectedSellerCategory.value.ifBlank { "Service Provider" }
    val location = profileViewModel.selectedSellerLocation.value.ifBlank { "Location Unknown" }
    val cost = profileViewModel.selectedSellerPrice.value.ifBlank { "Negotiable" }
    val sellerId = profileViewModel.selectedSellerId.value

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
<<<<<<< HEAD
    val myProducts = profileViewModel.marketplaceProducts.filter { it.sellerId == sellerId }
    // Note: In a real app, you would fetch posts for this specific sellerId
    val myPosts = profileViewModel.providerPosts 
=======
    // In a real app, you might fetch more details based on sellerId
    val displayName = sellerName
    val displayLocation = location
    val displayBio = "Professional services with guaranteed quality."
    val profileImage = null // Could be fetched from Firebase
    val contact = "+254 700 123 456" // Could be fetched from Firebase
    
    val myProducts = profileViewModel.marketplaceProducts.filter { it.sellerId == sellerId }
    val myPosts = profileViewModel.providerPosts // In a real app, filter by sellerId
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289

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
            // Profile Image
            Box(
                modifier = Modifier
<<<<<<< HEAD
                    .size(120.dp)
=======
                    .size(100.dp)
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
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
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = sellerName,
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
                Text(text = " 4.8 (Recent Rating)", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact and Location Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
<<<<<<< HEAD
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(icon = Icons.Default.Build, label = "Service", value = category)
                    DetailRow(icon = Icons.Default.LocationOn, label = "Location", value = location)
                    DetailRow(icon = Icons.Default.AttachMoney, label = "Starting Price", value = cost)
=======
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoRow(icon = Icons.Default.LocationOn, label = "Base Location", value = displayLocation)
                    InfoRow(icon = Icons.Default.Phone, label = "Contact number", value = contact)
                    InfoRow(icon = Icons.Default.AttachMoney, label = "Starting Price", value = cost)
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            // Service Posts Section (Simplified for now)
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
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
<<<<<<< HEAD
=======
                    // SENDING DATA TO FIREBASE
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("users")
                    myRef.setValue("Hello")

>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
                    val newNotification = ServiceRequest(
                        id = System.currentTimeMillis().toString(),
                        userName = profileViewModel.name.value,
                        serviceType = category,
                        description = "New Booking Request",
                        time = "Just now"
                    )
                    profileViewModel.addServiceRequest(newNotification, sellerId)
<<<<<<< HEAD
                    Toast.makeText(context, "Booking Sent to $sellerName!", Toast.LENGTH_LONG).show()
=======
                    Toast.makeText(context, "Booking Sent to $displayName!", Toast.LENGTH_LONG).show()
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
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
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SellerProfilePreview() {
    SellerProfileScreen(rememberNavController())
}
