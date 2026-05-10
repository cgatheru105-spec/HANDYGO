package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_SPLASH // Assuming ROUTE_SPLASH exists for popUpTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

// Data models (should be in separate files, e.g., data/User.kt, data/Provider.kt)
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val contact: String = "",
    val location: String = "",
    val bio: String = "",
    val userType: String = "user"
)

data class Provider(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val contact: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bio: String = "",
    val profileImage: String = "", // URL to the image
    val category: String = "",
    val userType: String = "provider"
)

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
                    firebaseUser?.let {
                        // Fetch user role/type from Realtime Database to navigate correctly
                        database.getReference("users").child(it.uid).get().addOnSuccessListener {
                            val userType = it.child("userType").getValue(String::class.java)
                            if (userType == "provider") {
                                navController.navigate(ROUTE_PROVIDER_HOME) {
                                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                                }
                            } else {
                                navController.navigate(ROUTE_USER_HOME) {
                                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                                }
                            }
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to get user data: ${it.message}", Toast.LENGTH_LONG).show()
                            // Still navigate, but user might not have full profile
                            navController.navigate(ROUTE_USER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
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
                    val userRef = database.getReference("users").child(uid)

                    val user = User(
                        uid = uid,
                        email = email,
                        name = name,
                        contact = contact,
                        location = location,
                        bio = bio,
                        userType = "user"
                    )

                    userRef.setValue(user)
                        .addOnSuccessListener {
                            Toast.makeText(context, "User Registered and data saved!", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_USER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            t
                            Toast.makeText(context, "Failed to save user data: ${it.message}", Toast.LENGTH_LONG).show()
                            // Optionally, delete the Firebase Auth user if data saving fails
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
                    val providerRef = database.getReference("providers").child(uid) // Store providers under 'users' for unified access

                    val provider = Provider(
                        uid = uid,
                        email = email,
                        name = name,
                        contact = contact,
                        location = location,
                        latitude = latitude,
                        longitude = longitude,
                        bio = bio,
                        profileImage = profileImage ?: "", // Handle nullability
                        category = category,
                        userType = "provider"
                    )

                    providerRef.setValue(provider)
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
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(ROUTE_SPLASH) { inclusive = true } // Use ROUTE_SPLASH or appropriate start destination
        }
    }
}
