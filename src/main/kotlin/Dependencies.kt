package com.example

import com.example.auth.AuthController
import com.example.auth.AuthRepository
import com.example.auth.JwtConfig
import com.example.auth.refreshtoken.RefreshTokenRepository
import com.example.database.DatabaseRepository
import com.example.database.connectToPostgres
import io.ktor.server.application.*

fun Application.configureDependencies(): ApplicationDependencies {
    val dbConnection = connectToPostgres(embedded = false)
    val databaseRepository = DatabaseRepository(dbConnection)
    val refreshTokenRepository = RefreshTokenRepository(dbConnection)
    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString()
    )

    val authRepository = AuthRepository(databaseRepository, refreshTokenRepository, jwtConfig)
    val authController = AuthController(authRepository)

    return ApplicationDependencies(
        databaseRepository,
        authController,
    )
}

data class ApplicationDependencies(
    val databaseRepository: DatabaseRepository,
    val authController: AuthController
)