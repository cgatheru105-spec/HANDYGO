package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
<<<<<<< HEAD
import com.example.handygo.navigation.ROUTE_SPLASH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.android.gms.tasks.Task
=======
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
<<<<<<< HEAD
        } else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                    
                    // SENDING DATA TO FIREBASE ON LOGIN
                    val db = FirebaseDatabase.getInstance()
                    val myRef = db.getReference("users")
                    myRef.setValue("Hello")

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
    }

    fun register(email: String, pass: String, confpass: String, role: String, extraData: Map<String, Any> = emptyMap()) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!email.endsWith("@gmail.com")) {
            Toast.makeText(context, "Please use a valid gmail account", Toast.LENGTH_SHORT).show()
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

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                val userProfile = mutableMapOf<String, Any>(
                    "email" to email,
                    "role" to role,
                    "name" to email.substringBefore("@")
                )
                userProfile.putAll(extraData)

                database.child("profiles").child(userId).setValue(userProfile).addOnCompleteListener { databaseTask: Task<Void> ->
                    if (databaseTask.isSuccessful) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        
                        // SENDING DATA TO FIREBASE ON SUCCESSFUL REGISTRATION
                        val db = FirebaseDatabase.getInstance()
                        val myRef = db.getReference("users")
                        myRef.setValue("Hello")

                        val target = if (role == "provider") ROUTE_PROVIDER_HOME else ROUTE_USER_HOME
                        navController.navigate(target) {
                            popUpTo(ROUTE_SPLASH) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Database Error: ${databaseTask.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                val message = if (task.exception is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    task.exception?.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
=======
            return
        }

        // Simulating local login
        if (email.contains("@") && pass.length >= 6) {
            Toast.makeText(context, "Login Successful (Local)", Toast.LENGTH_SHORT).show()
            navController.navigate(ROUTE_USER_HOME)
        } else {
            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
        }
    }

    fun registerUser(email: String, pass: String, confpass: String) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
        }
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Simulating local registration
        Toast.makeText(context, "User Registration Successful (Local)", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_USER_HOME)
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

        // Simulating local provider registration
        Toast.makeText(context, "Provider Registration Successful (Local)", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_PROVIDER_HOME)
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(context, "Password reset link sent to $email (Simulated)", Toast.LENGTH_LONG).show()
    }

    fun logout() {
<<<<<<< HEAD
        mAuth.signOut()
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(ROUTE_SPLASH) { inclusive = true }
=======
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0) { inclusive = true }
>>>>>>> 82772831ccf908dab54a6e848f21f2de22dbdd5f
        }
    }
}
