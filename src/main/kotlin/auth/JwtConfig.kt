package com.example.auth

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessTokenExpiry: Long = 30 * 60 * 1000
)
