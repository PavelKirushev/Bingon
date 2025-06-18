package com.example.core

import kotlinx.serialization.Serializable

@Serializable
data class UserSchema(
    val id: Int,
    val login: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String,
    val country: String,
    val age: Int,
    val gender: String,
    val description: String,
)