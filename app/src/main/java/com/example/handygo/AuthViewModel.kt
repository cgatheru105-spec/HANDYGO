package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_START
import com.example.handygo.navigation.ROUTE_SPLASH // Assuming ROUTE_SPLASH exists for popUpTo
import com.example.handygo.data.User
import com.example.handygo.data.Provider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

// Data models are now in com.example.handygo.data

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun login(
        email: String,
        password: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        val uid = user.uid
                        // Check in users first
                        database.getReference("profiles").child("users").child(uid).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                navController.navigate(ROUTE_USER_HOME) {
                                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                                }
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            } else {
                                // Check in providers
                                database.getReference("profiles").child("providers").child(uid).get().addOnSuccessListener { providerSnapshot ->
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
                        }.addOnFailureListener {
                            Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, it.exception?.message ?: "Login Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun registerUser(email: String, pass: String, confpass: String, name: String, contact: String, location: String, bio: String) {
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

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let {
                    val uid = it.uid
                    val profileRef = database.getReference("profiles").child("users").child(uid)

                    val profileMap = mapOf(
                        "uid" to uid,
                        "email" to email,
                        "name" to name,
                        "contact" to contact,
                        "location" to location,
                        "bio" to bio,
                        "role" to "user"
                    )

                    profileRef.setValue(profileMap)
                        .addOnSuccessListener {
                            Toast.makeText(context, "User Registered and data saved!", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_USER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save user data: ${it.message}", Toast.LENGTH_LONG).show()
                            firebaseUser.delete()
                        }
                }
            } else {
                val message = if (it.exception is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    it.exception?.message ?: "Registration Failed"
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
        profileImage: String?,
        category: String
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

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let {
                    val uid = it.uid
                    val profileRef = database.getReference("profiles").child("providers").child(uid)

                    val profileMap = mapOf(
                        "uid" to uid,
                        "email" to email,
                        "name" to name,
                        "contact" to contact,
                        "location" to location,
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "bio" to bio,
                        "profileImage" to (profileImage ?: ""),
                        "category" to category,
                        "role" to "provider"
                    )

                    profileRef.setValue(profileMap)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Provider Registered and data saved!", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_PROVIDER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save provider data: ${it.message}", Toast.LENGTH_LONG).show()
                            firebaseUser.delete()
                        }
                }
            } else {
                val message = if (it.exception is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    it.exception?.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, it.exception?.message ?: "Failed to send reset email", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun logout() {
        auth.signOut()
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_START) {
            popUpTo(0) { inclusive = true }
        }
    }
}
