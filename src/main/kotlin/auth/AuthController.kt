package com.example.auth

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(private val authService: AuthService) {

    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        val tokens = authService.authenticate(request.login, request.password)
            ?: return call.respond(
                HttpStatusCode.Unauthorized
            )

        call.respond(tokens)
    }
}