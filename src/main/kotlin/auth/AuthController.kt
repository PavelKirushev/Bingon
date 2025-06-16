package com.example.auth

import com.example.auth.dto.ErrorResponse
import com.example.auth.dto.SuccessResponse
import com.example.auth.models.LoginRequest
import com.example.auth.models.RegisterRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

class AuthController(private val authRepository: AuthRepository) {

    suspend fun login(call: ApplicationCall) {
        val request = call.receive<LoginRequest>()
        val tokens = authRepository.authenticate(request.login, request.password)
            ?: return call.respond(
                HttpStatusCode.Unauthorized
            )
        call.respond(tokens)
    }

    suspend fun register(call: ApplicationCall) {
        val request = call.receive<RegisterRequest>()
        when {
            request.password.length < 8 -> return call.respond(HttpStatusCode.BadRequest, ErrorResponse("Length must be 8 or more characters"))
            request.login.isEmpty() -> return call.respond(HttpStatusCode.BadRequest, ErrorResponse("Login is empty"))
            request.firstName.isEmpty() -> return call.respond(HttpStatusCode.BadRequest, ErrorResponse("Firstname is empty"))
            request.gender.isEmpty() -> return call.respond(HttpStatusCode.BadRequest, ErrorResponse("Gender is empty"))
            request.age !in 1..100 -> return call.respond(HttpStatusCode.BadRequest, ErrorResponse("Age not between 1 and 100"))
        }
        val user = authRepository.register(
            request.login,
            request.password,
            request.firstName,
            request.lastName,
            request.email,
            request.city,
            request.country,
            request.age,
            request.gender,
            request.description,
        )
        if (user != null) {
            call.respond(HttpStatusCode.OK, SuccessResponse(user + "was added"))
        } else {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Login already taken"))
        }
    }
}