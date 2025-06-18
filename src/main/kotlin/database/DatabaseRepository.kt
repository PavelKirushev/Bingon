package com.example.database

import com.example.core.UserSchema
import com.example.core.UserRepository
import com.example.core.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class DatabaseRepository(private val connection: Connection): UserRepository {
    companion object {
        private const val CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS USERS (" +
                    "ID SERIAL PRIMARY KEY NOT NULL," +
                    "LOGIN VARCHAR(255) NOT NULL," +
                    "PASSWORD VARCHAR(255) NOT NULL," +
                    "FIRSTNAME VARCHAR(255) NOT NULL," +
                    "LASTNAME VARCHAR(255)," +
                    "EMAIL VARCHAR(255)," +
                    "CITY VARCHAR(255)," +
                    "COUNTRY VARCHAR(255)," +
                    "AGE INT CHECK (AGE >= 0)," +
                    "GENDER VARCHAR(255) NOT NULL," +
                    "DESCRIPTION VARCHAR(255))"

        private const val SELECT_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?"
        private const val INSERT_USER = "INSERT INTO USERS (LOGIN, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, CITY, COUNTRY, AGE, GENDER, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_USER = "UPDATE USERS SET LOGIN = ?, PASSWORD = ?, FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, CITY = ?, COUNTRY = ?, AGE = ?, GENDER = ?, DESCRIPTION = ? WHERE LOGIN = ?"
        private const val DELETE_USER = "DELETE FROM USERS WHERE LOGIN = ?"
    }

    init {
        val stmt: Statement = connection.createStatement()
        stmt.executeUpdate(CREATE_TABLE_USERS)
    }

    override suspend fun addUser(userSchema: UserSchema): String? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, userSchema.login)
        statement.setString(2, userSchema.password)
        statement.setString(3, userSchema.firstName)
        statement.setString(4, userSchema.lastName)
        statement.setString(5, userSchema.email)
        statement.setString(6, userSchema.city)
        statement.setString(7, userSchema.country)
        statement.setInt(8, userSchema.age)
        statement.setString(9, userSchema.gender)
        statement.setString(10, userSchema.description)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getString(2)
        } else {
            return@withContext null
        }
    }

    suspend fun updateUser(login: String, userSchema: UserSchema) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setString(1, userSchema.login)
        statement.setString(2, userSchema.password)
        statement.setString(3, userSchema.firstName)
        statement.setString(4, userSchema.lastName)
        statement.setString(5, userSchema.email)
        statement.setString(6, userSchema.city)
        statement.setString(7, userSchema.country)
        statement.setInt(8, userSchema.age)
        statement.setString(9, userSchema.gender)
        statement.setString(10, userSchema.description)
        statement.setString(11, login)
        statement.executeUpdate()
    }

    override suspend fun getUserByLogin(login: String): UserSchema? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_LOGIN)
        statement.setString(1, login)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userSchema = UserSchema(
                id = resultSet.getInt("ID"),
                login = resultSet.getString("LOGIN"),
                password = resultSet.getString("PASSWORD"),
                firstName = resultSet.getString("FIRSTNAME"),
                lastName = resultSet.getString("LASTNAME"),
                email = resultSet.getString("EMAIL"),
                city = resultSet.getString("CITY"),
                country = resultSet.getString("COUNTRY"),
                age = resultSet.getInt("AGE"),
                gender = resultSet.getString("GENDER"),
                description = resultSet.getString("DESCRIPTION"),
            )
            return@withContext userSchema
        } else {
            return@withContext null
        }
    }

    override suspend fun getUserResponse(login: String): UserResponse? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_LOGIN)
        statement.setString(1, login)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val userResponse = UserResponse(
                login = resultSet.getString("LOGIN"),
                firstName = resultSet.getString("FIRSTNAME"),
                lastName = resultSet.getString("LASTNAME"),
                email = resultSet.getString("EMAIL"),
                city = resultSet.getString("CITY"),
                country = resultSet.getString("COUNTRY"),
                age = resultSet.getInt("AGE"),
                gender = resultSet.getString("GENDER"),
                description = resultSet.getString("DESCRIPTION"),
            )
            return@withContext userResponse
        } else {
            return@withContext null
        }
    }

    suspend fun deleteUserByLogin(login: String) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setString(1, login)
        statement.executeUpdate()
    }

}