package com.example.auth.refreshtoken

import java.time.Instant

data class RefreshToken(
    val userId: Int,
    val token: String,
    val expiresAt: Instant
)