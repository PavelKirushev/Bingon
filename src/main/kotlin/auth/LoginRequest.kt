package com.example.auth

data class LoginRequest(
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

// При регистрации:
//val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
//
// При проверке:
//BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
//install(RateLimit) {
//    rateLimiter = RateLimiter.of(5, Duration.ofMinutes(1)) // 5 попыток/минуту
//}
