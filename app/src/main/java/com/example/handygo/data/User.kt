package com.example.handygo.data

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val contact: String = "",
    val location: String = "",
    val bio: String = "",
    val userType: String = "user"
)
