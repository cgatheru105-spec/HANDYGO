package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_SPLASH

class AuthViewModel(var navController: NavHostController, var context: Context) {

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Placeholder for actual login logic
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            navController.navigate(ROUTE_USER_HOME)
        }
    }

    fun register(email: String, pass: String, confpass: String) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
        } else if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            // Placeholder for actual registration logic
            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
            navController.navigate(ROUTE_USER_HOME)
        }
    }

    fun logout() {
        // Placeholder for actual logout logic
        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(ROUTE_USER_HOME) { inclusive = true }
        }
    }
}
