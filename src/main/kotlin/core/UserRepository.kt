package com.example.core

import com.example.database.User

interface UserRepository {
    suspend fun getUserByLogin(login: String): User?
    suspend fun addUser(user: User): String?
}