package com.example.handygo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handygo.providerscreens.MarketProduct
import com.example.handygo.providerscreens.ServiceRequest
import com.google.firebase.auth.FirebaseAuth
<<<<<<< HEAD
import com.google.firebase.database.*
=======
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)

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
    private val storage = FirebaseStorage.getInstance()

    // Profile State
    var name = mutableStateOf("User Name")
    var contact = mutableStateOf("0712345678")
    var location = mutableStateOf("Nairobi, Westlands")
    var latitude = mutableStateOf(-1.286389)
    var longitude = mutableStateOf(36.817223)
    var bio = mutableStateOf("I need quick and reliable services.")
    var myCategory = mutableStateOf("General Provider")
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
        fetchProfile()
        fetchServiceRequests()
        fetchProviderPosts()
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
        
        // Try to fetch from users first
        database.child("profiles").child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    updateLocalState(snapshot)
                } else {
                    // If not in users, check in providers
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
        name.value = snapshot.child("name").getValue(String::class.java) ?: "User Name"
        contact.value = snapshot.child("contact").getValue(String::class.java) ?: ""
        location.value = snapshot.child("location").getValue(String::class.java) ?: ""
        bio.value = snapshot.child("bio").getValue(String::class.java) ?: ""
        myCategory.value = snapshot.child("category").getValue(String::class.java) ?: "General"
        role.value = snapshot.child("role").getValue(String::class.java) ?: "user"
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

<<<<<<< HEAD
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

    fun updateProfile(newName: String, newContact: String, newLocation: String, newBio: String, newCategory: String) {
=======
    fun updateProfile(newName: String, newContact: String, newLocation: String, newBio: String, newCategory: String, newImageUri: Uri? = null) {
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)
        val userId = auth.currentUser?.uid ?: return
        
        if (newImageUri != null && !newImageUri.toString().startsWith("http")) {
            uploadImage("profile_images/$userId.jpg", newImageUri) { downloadUrl ->
                saveProfileData(userId, newName, newContact, newLocation, newBio, newCategory, downloadUrl)
            }
        } else {
            saveProfileData(userId, newName, newContact, newLocation, newBio, newCategory, newImageUri?.toString())
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
<<<<<<< HEAD
<<<<<<< HEAD
        database.child("profiles").child(userId).updateChildren(profileMap)
=======
=======
        profileImageUrl?.let { profileMap["profileImage"] = it }
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)
        
        val node = if (role.value == "provider") "providers" else "users"
        database.child("profiles").child(node).child(userId).updateChildren(profileMap)
        
        // Update local state
        name.value = newName
        contact.value = newContact
        location.value = newLocation
        bio.value = newBio
        myCategory.value = newCategory
<<<<<<< HEAD
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
=======
        if (profileImageUrl != null) profileImageUri.value = Uri.parse(profileImageUrl)
    }

    private fun uploadImage(path: String, uri: Uri, onSuccess: (String) -> Unit) {
        val ref = storage.reference.child(path)
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { downloadUri ->
                    onSuccess(downloadUri.toString())
                }
            }
>>>>>>> 46506c0 (Integrated Firebase Storage for public image URLs, updated ViewModels for Uri support, and added image pickers to registration and product posting)
    }

    fun updateLocation(newLocation: String, newLat: Double, newLng: Double) {
        val userId = auth.currentUser?.uid ?: return
        location.value = newLocation
        latitude.value = newLat
        longitude.value = newLng
<<<<<<< HEAD
        database.child("profiles").child(userId).child("location").setValue(newLocation)
        database.child("profiles").child(userId).child("latitude").setValue(newLat)
        database.child("profiles").child(userId).child("longitude").setValue(newLng)
=======
        
        val userId = auth.currentUser?.uid ?: return
        val locationMap = mapOf(
            "location" to newLocation,
            "latitude" to newLat,
            "longitude" to newLng
        )
        // Only providers typically need coordinates, but we'll update in the correct node
        val node = if (role.value == "provider") "providers" else "users"
        database.child("profiles").child(node).child(userId).updateChildren(locationMap)
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
    }

    fun addProduct(product: MarketProduct, imageUri: Uri? = null) {
        val productId = database.child("marketplace").push().key ?: return
        
        if (imageUri != null) {
            uploadImage("product_images/$productId.jpg", imageUri) { downloadUrl ->
                val newProduct = product.copy(id = productId, imageUri = downloadUrl)
                database.child("marketplace").child(productId).setValue(newProduct)
            }
        } else {
            val newProduct = product.copy(id = productId)
            database.child("marketplace").child(productId).setValue(newProduct)
        }
    }

    fun addPost(post: ProviderPost) {
<<<<<<< HEAD
        val userId = auth.currentUser?.uid ?: return
        val postId = database.child("posts").child(userId).push().key ?: return
        val newPost = post.copy(id = postId)
        database.child("posts").child(userId).child(postId).setValue(newPost)
=======
        val postId = database.child("posts").push().key ?: return
        database.child("posts").child(postId).setValue(post)
        providerPosts.add(0, post)
>>>>>>> 1f99d742bdf6bf12ca4e592920f142c2caa6c289
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
