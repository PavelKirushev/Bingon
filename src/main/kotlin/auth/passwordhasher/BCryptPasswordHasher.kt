package com.example.auth.passwordhasher

import at.favre.lib.crypto.bcrypt.BCrypt

class BCryptPasswordHasher : PasswordHasher {
    override fun hash(password: String): String {
        return BCrypt.withDefaults()
            .hashToString(12, password.toCharArray()) // 12 - фактор сложности
    }

    override fun verify(password: String, hash: String): Boolean {
        return BCrypt.verifyer()
            .verify(password.toCharArray(), hash)
            .verified
    }
}