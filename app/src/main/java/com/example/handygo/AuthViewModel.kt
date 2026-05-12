package com.example.handygo

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.handygo.navigation.ROUTE_USER_HOME
import com.example.handygo.navigation.ROUTE_PROVIDER_HOME
import com.example.handygo.navigation.ROUTE_LOGIN
import com.example.handygo.navigation.ROUTE_START
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.Main)

    // --- CLOUDINARY CONFIGURATION ---
    // Replace these with your actual Cloudinary credentials
    private val CLOUD_NAME = "YOUR_CLOUD_NAME" 
    private val UPLOAD_PRESET = "YOUR_UPLOAD_PRESET"

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let { user ->
                        val uid = user.uid
                        // Check if user is a regular user
                        database.getReference("profiles").child("users").child(uid).get().addOnSuccessListener { snapshot ->
                            if (snapshot.exists()) {
                                navController.navigate(ROUTE_USER_HOME) {
                                    popUpTo(ROUTE_LOGIN) { inclusive = true }
                                }
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            } else {
                                // Check if user is a provider
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
                    Toast.makeText(context, task.exception?.message ?: "Login Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private suspend fun uploadToCloudinary(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val file = uriToFile(context, imageUri) ?: return@withContext null
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "photo.jpg", file.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val result = response.body?.string() ?: return@withContext null
                val json = JSONObject(result)
                json.getString("secure_url")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    fun registerUser(email: String, pass: String, confpass: String, name: String, contact: String, location: String, bio: String, imageUri: Uri? = null) {
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

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid
                    scope.launch {
                        val downloadUrl = if (imageUri != null) uploadToCloudinary(imageUri) else null
                        saveProfileToDatabase(uid, email, name, contact, location, bio, "user", downloadUrl)
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

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                firebaseUser?.let { user ->
                    val uid = user.uid
                    scope.launch {
                        val downloadUrl = if (profileImageUri != null) uploadToCloudinary(profileImageUri) else null
                        saveProfileToDatabase(uid, email, name, contact, location, bio, "provider", downloadUrl, latitude, longitude, category)
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
                Toast.makeText(context, "Failed to save data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password reset link sent to $email", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, task.exception?.message ?: "Failed to send reset email", Toast.LENGTH_LONG).show()
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
