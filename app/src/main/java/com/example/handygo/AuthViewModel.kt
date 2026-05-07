package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME

class AuthViewModel(var navController: NavHostController, var context: Context) {

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}
