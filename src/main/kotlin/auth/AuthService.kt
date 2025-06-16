package com.example.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.core.UserRepository
import com.example.database.User
import java.util.Date
import java.util.UUID

class AuthService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig
) {
    suspend fun authenticate(login: String, password: String): TokenResponse? {
        val user = userRepository.getUserByLogin(login) ?: return null
        if (!BCrypt.verifyer().verify(password.toCharArray(), user.password).verified) {
            return null
        }

        return generateTokens(user)
    }

    private fun generateTokens(user: User): TokenResponse {
        val accessToken = JWT.create()
            .withSubject(user.login)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.accessTokenExpiry))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        // рандомная строка
        val refreshToken = UUID.randomUUID().toString()

        return TokenResponse(accessToken, refreshToken, jwtConfig.accessTokenExpiry)
    }
}