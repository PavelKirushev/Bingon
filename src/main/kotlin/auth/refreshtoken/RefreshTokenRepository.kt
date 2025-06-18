package com.example.auth.refreshtoken

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement
import java.sql.Timestamp

class RefreshTokenRepository(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_TOKENS =
            "CREATE TABLE IF NOT EXISTS refresh_tokens (" +
                "id SERIAL PRIMARY KEY," +
                "user_id INT REFERENCES users(id) ON DELETE CASCADE," +
                "token VARCHAR(255) UNIQUE NOT NULL," +
                "expires_at TIMESTAMP WITH TIME ZONE NOT NULL," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");"

        private const val SELECT_TOKEN = "SELECT * FROM refresh_tokens WHERE token = ?"
        private const val INSERT_TOKEN = "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)"
        private const val DELETE_TOKEN = "DELETE FROM refresh_tokens WHERE token = ?"
        private const val DELETE_ALL_TOKENS = "DELETE FROM refresh_tokens WHERE user_id = ?"
    }
    init {
        val stmt: Statement = connection.createStatement()
        stmt.executeUpdate(CREATE_TABLE_TOKENS)
    }

    suspend fun save(token: RefreshToken) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_TOKEN)
        statement.setInt(1, token.userId)
        statement.setString(2, token.token)
        statement.setTimestamp(3, Timestamp.from(token.expiresAt))
        statement.executeUpdate()
    }

    suspend fun findByToken(token: String): RefreshToken? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_TOKEN)
        statement.setString(1, token)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            val token = RefreshToken(
                userId = resultSet.getInt("user_id"),
                token = resultSet.getString("token"),
                expiresAt = resultSet.getTimestamp("expires_at").toInstant()
            )
            return@withContext token
        } else {
            return@withContext null
        }
    }

    suspend fun delete(token: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_TOKEN)
        statement.setString(1, token)
        statement.executeUpdate()
    }

    suspend fun deleteAllForUser(userId: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_ALL_TOKENS)
        statement.setInt(1, userId)
        statement.executeUpdate()
    }

}