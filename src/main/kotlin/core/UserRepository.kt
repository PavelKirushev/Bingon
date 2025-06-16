package com.example.core

import com.example.database.User

interface UserRepository {
    suspend fun getUserByLogin(login: String): User?
}