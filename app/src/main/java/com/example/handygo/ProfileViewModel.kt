package com.example.handygo

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.handygo.providerscreens.MarketProduct
import com.example.handygo.providerscreens.ServiceRequest

data class ProviderPost(
    val id: String,
    val description: String,
    val location: String,
    val time: String,
    val imageUri: String? = null
)

class ProfileViewModel : ViewModel() {
    // Profile State
    var name = mutableStateOf("User Name")
    var contact = mutableStateOf("0712345678")
    var location = mutableStateOf("Nairobi, Westlands")
    var latitude = mutableStateOf(-1.286389)
    var longitude = mutableStateOf(36.817223)
    var bio = mutableStateOf("I need quick and reliable services.")
    var profileImageUri = mutableStateOf<String?>(null)

    // Marketplace State
    val marketplaceProducts = mutableStateListOf(
        MarketProduct(
            "1", "Heavy Duty Drill", "Brand new cordless drill with 2 batteries.",
            "Ksh 12,000", "Nairobi, CBD", "John Tools"
        ),
        MarketProduct(
            "2", "Plumbing Wrench Set", "Professional grade 3-piece wrench set.",
            "Ksh 4,500", "Mombasa, Nyali", "Jane Supplies"
        )
    )

    // Provider Posts State
    val providerPosts = mutableStateListOf(
        ProviderPost(
            "1", "Available for emergency plumbing services in Westlands.", 
            "Westlands, Nairobi", "10 mins ago"
        )
    )

    // Service Requests (Notifications for Provider)
    val serviceRequests = mutableStateListOf(
        ServiceRequest("1", "John Doe", "Plumbing", "Leaking tap in kitchen", "2 mins ago"),
        ServiceRequest("2", "Jane Smith", "Electrical", "Socket not working", "1 hour ago")
    )

    fun updateProfile(newName: String, newContact: String, newLocation: String, newBio: String) {
        name.value = newName
        contact.value = newContact
        location.value = newLocation
        bio.value = newBio
    }

    fun updateLocation(newLocation: String, newLat: Double, newLng: Double) {
        location.value = newLocation
        latitude.value = newLat
        longitude.value = newLng
    }

    fun addProduct(product: MarketProduct) {
        marketplaceProducts.add(0, product)
    }

    fun addPost(post: ProviderPost) {
        providerPosts.add(0, post)
    }

    fun addServiceRequest(request: ServiceRequest) {
        serviceRequests.add(0, request)
    }
}
