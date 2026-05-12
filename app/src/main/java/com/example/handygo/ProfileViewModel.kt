package com.example.handygo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handygo.providerscreens.MarketProduct
import com.example.handygo.providerscreens.ServiceRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
    var name = mutableStateOf("User Name")
    var contact = mutableStateOf("0712345678")
    var location = mutableStateOf("Nairobi, Westlands")
    var latitude = mutableStateOf(-1.286389)
    var longitude = mutableStateOf(36.817223)
    var bio = mutableStateOf("I need quick and reliable services.")
    var myCategory = mutableStateOf("General Provider")
    var role = mutableStateOf("user")
    var profileImageUri = mutableStateOf<String?>(null)

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
        database.child("profiles").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name.value = snapshot.child("name").getValue(String::class.java) ?: "User Name"
                contact.value = snapshot.child("contact").getValue(String::class.java) ?: ""
                location.value = snapshot.child("location").getValue(String::class.java) ?: ""
                bio.value = snapshot.child("bio").getValue(String::class.java) ?: ""
                myCategory.value = snapshot.child("category").getValue(String::class.java) ?: "General"
                role.value = snapshot.child("role").getValue(String::class.java) ?: "user"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
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
        database.child("profiles").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allProviders.clear()
                for (child in snapshot.children) {
                    val roleVal = child.child("role").getValue(String::class.java)
                    if (roleVal == "provider") {
                        val providerData = child.value as? Map<String, Any> ?: continue
                        val providerWithId = providerData.toMutableMap()
                        providerWithId["id"] = child.key ?: ""
                        allProviders.add(providerWithId)
                    }
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

    fun updateProfile(newName: String, newContact: String, newLocation: String, newBio: String, newCategory: String) {
        val userId = auth.currentUser?.uid ?: return
        val profileMap = mapOf(
            "name" to newName,
            "contact" to newContact,
            "location" to newLocation,
            "bio" to newBio,
            "category" to newCategory
        )
        database.child("profiles").child(userId).updateChildren(profileMap)
    }

    fun updateLocation(newLocation: String, newLat: Double, newLng: Double) {
        val userId = auth.currentUser?.uid ?: return
        location.value = newLocation
        latitude.value = newLat
        longitude.value = newLng
        database.child("profiles").child(userId).child("location").setValue(newLocation)
        database.child("profiles").child(userId).child("latitude").setValue(newLat)
        database.child("profiles").child(userId).child("longitude").setValue(newLng)
    }

    fun addProduct(product: MarketProduct) {
        val productId = database.child("marketplace").push().key ?: return
        val newProduct = product.copy(id = productId)
        database.child("marketplace").child(productId).setValue(newProduct)
    }

    fun addPost(post: ProviderPost) {
        val userId = auth.currentUser?.uid ?: return
        val postId = database.child("posts").child(userId).push().key ?: return
        val newPost = post.copy(id = postId)
        database.child("posts").child(userId).child(postId).setValue(newPost)
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
