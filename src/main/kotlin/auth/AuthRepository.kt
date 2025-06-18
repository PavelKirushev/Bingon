package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.auth.passwordhasher.BCryptPasswordHasher
import com.example.auth.refreshtoken.RefreshToken
import com.example.auth.refreshtoken.RefreshTokenRepository
import com.example.core.UserRepository
import com.example.core.UserResponse
import com.example.core.UserSchema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date
import java.util.UUID

class AuthRepository(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtConfig: JwtConfig
) {

    private val passwordHasher = BCryptPasswordHasher()

    suspend fun register(
        login: String,
        password: String,
        firstName: String,
        lastName: String,
        email: String,
        city: String,
        country: String,
        age: Int,
        gender: String,
        description: String
    ): String? {
        if (userRepository.getUserByLogin(login) != null) {
            return null
        }
        val passwordHash = passwordHasher.hash(password)
        return userRepository.addUser(UserSchema(-1, login, passwordHash, firstName, lastName, email, city, country, age, gender, description))
    }

    suspend fun authenticate(login: String, password: String): TokenResponse? {
        try {
            val user = userRepository.getUserByLogin(login) ?: return null

            // Проверяем, что у пользователя есть хеш пароля
            if (user.password.isNullOrEmpty()) {
                println("User ${user.login} has empty password hash")
                return null
            }

            if (!passwordHasher.verify(password, user.password)) {
                return null
            }

            return generateTokens(user)
        } catch (e: Exception) {
            println("Authentication failed for $login")
            throw e
        }
    }

    suspend fun getUser(login: String): UserResponse? {
        val user = userRepository.getUserResponse(login) ?: return null
        return user
    }

    private fun generateTokens(userSchema: UserSchema): TokenResponse {
        try {
            val accessToken = JWT.create()
                .withSubject(userSchema.login)
                .withClaim("login", userSchema.login)
                .withIssuer(jwtConfig.issuer)  // Добавьте issuer
                .withAudience(jwtConfig.audience)  // Добавьте audience
                .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.accessTokenExpiry))
                .sign(Algorithm.HMAC256(jwtConfig.secret))

            val refreshToken = UUID.randomUUID().toString()

            // Сохраните refreshToken в БД (важно!)
            CoroutineScope(Dispatchers.IO).launch {
                refreshTokenRepository.save(
                    RefreshToken(
                        userId = userSchema.id,
                        token = refreshToken,
                        expiresAt = Instant.now().plusMillis(jwtConfig.accessTokenExpiry)
                    )
                )
            }


            return TokenResponse(accessToken, refreshToken, jwtConfig.accessTokenExpiry)
        } catch (e: Exception) {
            println("Token generation failed for ${userSchema.login}")
            throw e
        }
    }
}