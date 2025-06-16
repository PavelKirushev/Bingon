package com.example.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)
