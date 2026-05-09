package com.example.handygo.userscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.handygo.AuthViewModel
import com.example.handygo.ProfileViewModel
import com.example.handygo.R
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_SPLASH
import com.example.handygo.navigation.ROUTE_START
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay

@Composable
fun Splashscreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference()

    LaunchedEffect(key1 = true) {
        delay(3000)
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Check user role in Realtime Database
            database.child("profiles").child(currentUser.uid).child("role").get()
                .addOnSuccessListener { snapshot ->
                    val role = snapshot.getValue(String::class.java)
                    if (role == "provider") {
                        navController.navigate(ROUTE_PROVIDER_HOME) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    } else {
                        navController.navigate(ROUTE_USER_HOME) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    }
                }
                .addOnFailureListener {
                    // Fallback to Start Screen if role check fails
                    navController.navigate(ROUTE_START) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }
        } else {
            navController.navigate(ROUTE_START) {
                popUpTo(ROUTE_SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "HANDYGO",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "WELCOME",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.handygo),
                contentDescription = "company logo",
                modifier = Modifier
                    .height(200.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "get it fixed in time",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashscreenPreview() {
    Splashscreen(navController = rememberNavController())
}
