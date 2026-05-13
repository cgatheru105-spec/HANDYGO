package com.example.handygo

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handygo.providerscreens.MarketProduct
import com.example.handygo.providerscreens.ServiceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

data class ProviderPost(
    val id: String = "",
    val description: String = "",
    val location: String = "",
    val time: String = "",
    val imageUri: String? = null
)

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference()

    // Profile State
    var name = mutableStateOf("")
    var contact = mutableStateOf("")
    var location = mutableStateOf("")
    var latitude = mutableDoubleStateOf(0.0)
    var longitude = mutableDoubleStateOf(0.0)
    var bio = mutableStateOf("")
    var myCategory = mutableStateOf("")
    var role = mutableStateOf("user")
    var profileImageUri = mutableStateOf<Uri?>(null)

    // Selected Seller for Details View
    var selectedSellerName = mutableStateOf("")
    var selectedSellerCategory = mutableStateOf("")
    var selectedSellerLocation = mutableStateOf("")
    var selectedSellerPrice = mutableStateOf("")
    var selectedSellerId = mutableStateOf("")

    // Lists
    val marketplaceProducts = mutableStateListOf<MarketProduct>()
    val serviceRequests = mutableStateListOf<ServiceRequest>()
    val allProviders = mutableStateListOf<Map<String, Any>>()
    val providerPosts = mutableStateListOf<ProviderPost>()

    private val authListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser != null) {
            fetchProfile()
            fetchServiceRequests()
            fetchProviderPosts()
        }
    }

    init {
        auth.addAuthStateListener(authListener)
        fetchMarketplace()
        fetchProviders()
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authListener)
    }

    fun fetchProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        database.child("profiles").child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    updateLocalState(snapshot)
                } else {
                    database.child("profiles").child("providers").child(userId).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                updateLocalState(snapshot)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateLocalState(snapshot: DataSnapshot) {
        name.value = snapshot.child("name").getValue(String::class.java) ?: ""
        contact.value = snapshot.child("contact").getValue(String::class.java) ?: ""
        location.value = snapshot.child("location").getValue(String::class.java) ?: ""
        bio.value = snapshot.child("bio").getValue(String::class.java) ?: ""
        myCategory.value = snapshot.child("category").getValue(String::class.java) ?: "General"
        role.value = snapshot.child("role").getValue(String::class.java) ?: "user"
        latitude.doubleValue = snapshot.child("latitude").getValue(Double::class.java) ?: 0.0
        longitude.doubleValue = snapshot.child("longitude").getValue(Double::class.java) ?: 0.0
        val img = snapshot.child("profileImage").getValue(String::class.java)
        profileImageUri.value = if (img != null) Uri.parse(img) else null
    }

    private fun fetchMarketplace() {
        database.child("marketplace").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                marketplaceProducts.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(MarketProduct::class.java)
                    if (product != null) marketplaceProducts.add(product)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchProviders() {
        database.child("profiles").child("providers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProviders.clear()
                for (child in snapshot.children) {
                    val providerData = child.value as? Map<String, Any> ?: continue
                    val providerWithId = providerData.toMutableMap()
                    providerWithId["id"] = child.key ?: ""
                    allProviders.add(providerWithId)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchServiceRequests() {
        val userId = auth.currentUser?.uid ?: return
        database.child("requests").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceRequests.clear()
                for (child in snapshot.children) {
                    val request = child.getValue(ServiceRequest::class.java)
                    if (request != null) serviceRequests.add(request)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun fetchProviderPosts() {
        val userId = auth.currentUser?.uid ?: return
        database.child("posts").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                providerPosts.clear()
                for (child in snapshot.children) {
                    val post = child.getValue(ProviderPost::class.java)
                    if (post != null) providerPosts.add(post)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    suspend fun uploadToCloudinary(imageUri: Uri): String? {
        return CloudinaryManager.uploadImageAsync(imageUri)
    }

    fun updateProfile(context: Context, newName: String, newContact: String, newLocation: String, newBio: String, newCategory: String, newImageUri: Uri? = null) {
        // Update local state first (important for registration flow)
        name.value = newName
        contact.value = newContact
        location.value = newLocation
        bio.value = newBio
        myCategory.value = newCategory
        if (newImageUri != null) profileImageUri.value = newImageUri

        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            val imageUrl = if (newImageUri != null && !newImageUri.toString().startsWith("http")) {
                uploadToCloudinary(newImageUri)
            } else {
                newImageUri?.toString()
            }
            saveProfileData(userId, newName, newContact, newLocation, newBio, newCategory, imageUrl)
        }
    }

    private fun saveProfileData(userId: String, newName: String, newContact: String, newLocation: String, newBio: String, newCategory: String, profileImageUrl: String?) {
        val profileMap = mutableMapOf<String, Any>(
            "name" to newName,
            "contact" to newContact,
            "location" to newLocation,
            "bio" to newBio,
            "category" to newCategory
        )
        profileImageUrl?.let { profileMap["profileImage"] = it }
        
        val node = if (role.value == "provider") "providers" else "users"
        database.child("profiles").child(node).child(userId).updateChildren(profileMap)
        
        if (profileImageUrl != null) profileImageUri.value = Uri.parse(profileImageUrl)
    }

    fun updateLocation(newLocation: String, newLat: Double, newLng: Double) {
        // Update local state first
        location.value = newLocation
        latitude.doubleValue = newLat
        longitude.doubleValue = newLng
        
        val userId = auth.currentUser?.uid ?: return
        
        val locationMap = mapOf(
            "location" to newLocation,
            "latitude" to newLat,
            "longitude" to newLng
        )
        val node = if (role.value == "provider") "providers" else "users"
        database.child("profiles").child(node).child(userId).updateChildren(locationMap)
    }

    fun addProduct(context: Context, product: MarketProduct, imageUri: Uri? = null) {
        val productId = database.child("marketplace").push().key ?: return
        
        viewModelScope.launch {
            val imageUrl = if (imageUri != null) {
                uploadToCloudinary(imageUri)
            } else product.imageUri
            
            val newProduct = product.copy(id = productId, imageUri = imageUrl)
            database.child("marketplace").child(productId).setValue(newProduct)
        }
    }

    fun addPost(context: Context, post: ProviderPost, imageUri: Uri? = null) {
        val userId = auth.currentUser?.uid ?: return
        val postId = database.child("posts").child(userId).push().key ?: return
        
        viewModelScope.launch {
            val imageUrl = if (imageUri != null) {
                uploadToCloudinary(imageUri)
            } else post.imageUri
            
            val newPost = post.copy(id = postId, imageUri = imageUrl)
            database.child("posts").child(userId).child(postId).setValue(newPost)
            providerPosts.add(0, newPost)
        }
    }

    fun addServiceRequest(request: ServiceRequest, providerId: String) {
        if (providerId.isBlank()) return
        val requestId = database.child("requests").child(providerId).push().key ?: return
        val newRequest = request.copy(id = requestId)
        database.child("requests").child(providerId).child(requestId).setValue(newRequest)
    }

    fun removeServiceRequest(requestId: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child("requests").child(userId).child(requestId).removeValue()
    }
}
