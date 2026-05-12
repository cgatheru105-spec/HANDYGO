package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_SPLASH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.android.gms.tasks.Task

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                
                database.child("profiles").child(userId).child("role").get().addOnSuccessListener { snapshot: DataSnapshot ->
                    val role = snapshot.getValue(String::class.java)
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    
                    val target = if (role == "provider") ROUTE_PROVIDER_HOME else ROUTE_USER_HOME
                    navController.navigate(target) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }.addOnFailureListener {
                    navController.navigate(ROUTE_USER_HOME) {
                        popUpTo(ROUTE_SPLASH) { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun registerUser(email: String, pass: String, confpass: String) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                val userProfile = mapOf(
                    "email" to email,
                    "role" to "user",
                    "name" to email.substringBefore("@")
                )

                database.child("profiles").child(userId).setValue(userProfile).addOnCompleteListener { databaseTask ->
                    if (databaseTask.isSuccessful) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_USER_HOME) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
        profileImage: String?
    ) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                val providerProfile = mapOf(
                    "email" to email,
                    "role" to "provider",
                    "name" to name,
                    "contact" to contact,
                    "location" to location,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "bio" to bio,
                    "profileImage" to profileImage
                )

                database.child("profiles").child(userId).setValue(providerProfile).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(context, "Provider Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_PROVIDER_HOME) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout() {
        mAuth.signOut()
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}
