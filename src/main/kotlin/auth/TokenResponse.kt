package com.example.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,   // сам токен
    val refreshToken: String,  // рандомная уникальная строка
    val expiresIn: Long        // время жизни токена
)