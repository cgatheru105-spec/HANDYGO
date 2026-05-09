package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.android.gms.tasks.Task

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                    database.child("profiles").child(userId).child("role").get().addOnSuccessListener { snapshot: DataSnapshot ->
                        val role = snapshot.getValue(String::class.java)
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        if (role == "provider") {
                            navController.navigate(ROUTE_PROVIDER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        } else {
                            navController.navigate(ROUTE_USER_HOME) {
                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
                        }
                    }.addOnFailureListener {
                        // Fallback if role is not found
                        Toast.makeText(context, "Login Successful (Defaulting to User)", Toast.LENGTH_SHORT).show()
                        navController.navigate(ROUTE_USER_HOME) {
                            popUpTo(ROUTE_LOGIN) { inclusive = true }
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
                        if (role == "provider") {
                            navController.navigate(ROUTE_PROVIDER_HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(ROUTE_USER_HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Profile creation failed: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
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
    
    fun isLogged(): Boolean {
        return mAuth.currentUser != null
    }
}
