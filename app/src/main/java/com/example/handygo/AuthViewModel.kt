package com.example.handygo

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_START
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun login(email: String, password: String, navController: NavHostController, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val uid = user.uid
                    // Check if user is a regular user
                    val userSnapshot = database.getReference("profiles").child("users").child(uid).get().await()
                    if (userSnapshot.exists()) {
                        navController.navigate(ROUTE_USER_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
                        }
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // Check if user is a provider
                        val providerSnapshot = database.getReference("profiles").child("providers").child(uid).get().await()
                        if (providerSnapshot.exists()) {
                            navController.navigate(ROUTE_PROVIDER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Profile not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun uploadToCloudinary(imageUri: Uri): String? {
        return CloudinaryManager.uploadImageAsync(imageUri)
    }

    fun registerUser(
        email: String,
        pass: String,
        confpass: String,
        name: String,
        contact: String,
        location: String,
        bio: String,
        navController: NavHostController,
        context: Context,
        imageUri: Uri? = null
    ) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.length < 6) {
            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user
                if (user != null) {
                    val uid = user.uid
                    val downloadUrl = if (imageUri != null) uploadToCloudinary(imageUri) else null
                    saveProfileToDatabase(uid, email, name, contact, location, bio, "user", navController, context, downloadUrl)
                }
            } catch (e: Exception) {
                val message = if (e is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    e.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun registerProvider(
        email: String,
        pass: String,
        confpass: String,
        name: String,
        contact: String,
        location: String,
        latitude: Double,
        longitude: Double,
        bio: String,
        profileImageUri: Uri?,
        category: String,
        navController: NavHostController,
        context: Context
    ) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass.length < 6) {
            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val user = result.user
                if (user != null) {
                    val uid = user.uid
                    val downloadUrl = if (profileImageUri != null) uploadToCloudinary(profileImageUri) else null
                    saveProfileToDatabase(uid, email, name, contact, location, bio, "provider", navController, context, downloadUrl, latitude, longitude, category)
                }
            } catch (e: Exception) {
                val message = if (e is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    e.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun saveProfileToDatabase(
        uid: String,
        email: String,
        name: String,
        contact: String,
        location: String,
        bio: String,
        role: String,
        navController: NavHostController,
        context: Context,
        profileImage: String?,
        latitude: Double? = null,
        longitude: Double? = null,
        category: String? = null
    ) {
        val node = if (role == "provider") "providers" else "users"
        val profileRef = database.getReference("profiles").child(node).child(uid)

        val profileMap = mutableMapOf<String, Any>(
            "uid" to uid,
            "email" to email,
            "name" to name,
            "contact" to contact,
            "location" to location,
            "bio" to bio,
            "role" to role
        )
        profileImage?.let { profileMap["profileImage"] = it }
        latitude?.let { profileMap["latitude"] = it }
        longitude?.let { profileMap["longitude"] = it }
        category?.let { profileMap["category"] = it }

        try {
            profileRef.setValue(profileMap).await()
//            Toast.makeText(context, "${role.replaceFirstChar { it.uppercase() }} Registered and data saved!", Toast.LENGTH_LONG).show()
            Toast.makeText(
                context,
                "${role.uppercase()} Registered and data saved!",
                Toast.LENGTH_LONG
            ).show()
            val route = if (role == "provider") ROUTE_PROVIDER_HOME else ROUTE_USER_HOME
            navController.navigate(route) {
                popUpTo(ROUTE_LOGIN) { inclusive = true }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun forgotPassword(email: String, context: Context) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Failed to send reset email", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout(navController: NavHostController, context: Context) {
        auth.signOut()
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_START) {
            popUpTo(ROUTE_START) { inclusive = true }
        }
    }
}
