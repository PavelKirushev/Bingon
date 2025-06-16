package com.example

import com.example.auth.AuthController
import com.example.auth.AuthService
import com.example.auth.JwtConfig
import com.example.database.UserService
import com.example.database.connectToPostgres
import io.ktor.server.application.*

fun Application.configureDependencies(): ApplicationDependencies {
    val dbConnection = connectToPostgres(embedded = false)
    val userService = UserService(dbConnection)

    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString()
    )

    val authService = AuthService(userService, jwtConfig)
    val authController = AuthController(authService)

    return ApplicationDependencies(
        userService,
        authController,
    )
}

data class ApplicationDependencies(
    val userService: UserService,
    val authController: AuthController
)