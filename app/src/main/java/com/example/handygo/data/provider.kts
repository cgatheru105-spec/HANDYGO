package com.example.handygo.model

data class Provider(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val contact: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bio: String = "",
    val profileImage: String = ""
)