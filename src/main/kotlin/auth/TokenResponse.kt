package com.example.auth

data class TokenResponse(
    val accessToken: String,   // сам токен
    val refreshToken: String,  // рандомная уникальная строка
    val expiresIn: Long        // время жизни токена
)