package com.example.handygo

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
<<<<<<< HEAD
import com.example.handygo.navigation.ROUTE_SPLASH
=======
import com.example.handygo.navigation.ROUTE_START
import com.example.handygo.navigation.ROUTE_SPLASH // Assuming ROUTE_SPLASH exists for popUpTo
import com.example.handygo.data.User
import com.example.handygo.data.Provider
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
<<<<<<< HEAD
<<<<<<< HEAD
import com.google.firebase.database.DataSnapshot
import com.google.android.gms.tasks.Task
=======
=======
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)

// Data models are now in com.example.handygo.data
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

<<<<<<< HEAD
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
=======
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
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
                }
            } else {
                Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
    }

<<<<<<< HEAD
<<<<<<< HEAD
    fun registerUser(email: String, pass: String, confpass: String) {
=======
    fun registerUser(email: String, pass: String, confpass: String, name: String, contact: String, location: String, bio: String) {
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
=======
    fun registerUser(email: String, pass: String, confpass: String, name: String, contact: String, location: String, bio: String, imageUri: Uri? = null) {
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)
        if (email.isBlank() || pass.isBlank() || confpass.isBlank()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
<<<<<<< HEAD
        
=======
        if (pass.length < 6) {
            Toast.makeText(context, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
        if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

<<<<<<< HEAD
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
=======
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid
                    
                    if (imageUri != null) {
                        uploadImage("profile_images/$uid.jpg", imageUri) { downloadUrl ->
                            saveProfileToDatabase(uid, email, name, contact, location, bio, "user", downloadUrl)
                        }
                    } else {
                        saveProfileToDatabase(uid, email, name, contact, location, bio, "user", null)
                    }
                }
            } else {
                val message = if (it.exception is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    it.exception?.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
            }
        }
    }

    private fun saveProfileToDatabase(
        uid: String,
        email: String,
        name: String,
        contact: String,
        location: String,
        bio: String,
        role: String,
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

        profileRef.setValue(profileMap)
            .addOnSuccessListener {
                Toast.makeText(context, "${role.replaceFirstChar { it.uppercase() }} Registered and data saved!", Toast.LENGTH_LONG).show()
                val route = if (role == "provider") ROUTE_PROVIDER_HOME else ROUTE_USER_HOME
                navController.navigate(route) {
                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save data: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImage(path: String, uri: Uri, onSuccess: (String) -> Unit) {
        val ref = storage.reference.child(path)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri: Uri ->
                    onSuccess(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception: Exception ->
                Toast.makeText(context, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
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

<<<<<<< HEAD
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
=======
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid
                    
                    if (profileImageUri != null) {
                        uploadImage("profile_images/$uid.jpg", profileImageUri) { downloadUrl ->
                            saveProfileToDatabase(uid, email, name, contact, location, bio, "provider", downloadUrl, latitude, longitude, category)
                        }
                    } else {
                        saveProfileToDatabase(uid, email, name, contact, location, bio, "provider", null, latitude, longitude, category)
                    }
                }
            } else {
                val message = if (it.exception is FirebaseAuthUserCollisionException) {
                    "This email is already registered. Please Login instead."
                } else {
                    it.exception?.message ?: "Registration Failed"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
<<<<<<< HEAD
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
=======
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
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
            popUpTo(0) { inclusive = true }
        }
    }
}
