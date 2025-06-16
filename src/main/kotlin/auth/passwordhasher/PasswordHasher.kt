package com.example.auth.passwordhasher

import at.favre.lib.crypto.bcrypt.BCrypt

interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, hash: String): Boolean
}