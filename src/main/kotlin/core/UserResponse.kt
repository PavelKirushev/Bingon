package com.example.core

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val login: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String,
    val country: String,
    val age: Int,
    val gender: String,
    val description: String,
)
