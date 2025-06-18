package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*

fun Application.configureAuthentication(authController: AuthController) {
    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString(),
        accessTokenExpiry = 30 * 60 * 1000
    )

    install(Authentication) {
        jwt("auth") {
            realm = jwtConfig.realm
            verifier(JWT.require(Algorithm.HMAC256(jwtConfig.secret))
                .withIssuer(jwtConfig.issuer)
                .withAudience(jwtConfig.audience)
                .acceptExpiresAt(5)
                .build()
            )
            validate { credential ->
                val subject = credential.payload.subject
                val login = credential.payload.getClaim("login").asString()

                if (subject != null || login != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    routing {
        post("/login") {
            authController.login(call)
        }
        post("/register") {
            authController.register(call)
        }

        authenticate("auth") {
            get("/me") {
                authController.me(call)
            }
        }

    }
}
