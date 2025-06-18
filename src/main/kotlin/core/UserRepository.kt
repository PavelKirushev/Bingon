package com.example.core

interface UserRepository {
    suspend fun getUserByLogin(login: String): UserSchema?
    suspend fun getUserResponse(login: String): UserResponse?
    suspend fun addUser(userSchema: UserSchema): String?
}