package com.example.database

import com.example.core.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

class UserService(private val connection: Connection): UserRepository {
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

        private const val SELECT_USER_BY_ID = "SELECT * FROM USERS WHERE ID = ?"
        private const val SELECT_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?"
        private const val INSERT_USER = "INSERT INTO USERS (LOGIN, PASSWORD, FIRSTNAME, LASTNAME, EMAIL, CITY, COUNTRY, AGE, GENDER, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        private const val UPDATE_USER = "UPDATE USERS SET LOGIN = ?, PASSWORD = ?, FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, CITY = ?, COUNTRY = ?, AGE = ?, GENDER = ?, DESCRIPTION = ? WHERE ID = ?"
        private const val DELETE_USER = "DELETE FROM USERS WHERE ID = ?"
    }

    init {
        val stmt: Statement = connection.createStatement()
        stmt.executeUpdate(CREATE_TABLE_USERS)
    }

    suspend fun addUser(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.login)
        statement.setString(2, user.password)
        statement.setString(3, user.firstName)
        statement.setString(4, user.lastName)
        statement.setString(5, user.email)
        statement.setString(6, user.city)
        statement.setString(7, user.country)
        statement.setInt(8, user.age)
        statement.setString(9, user.gender)
        statement.setString(10, user.description)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to return the id of the newly added user")
        }
    }

    suspend fun updateUser(id: Int, user: User) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_USER)
        statement.setString(1, user.login)
        statement.setString(2, user.password)
        statement.setString(3, user.firstName)
        statement.setString(4, user.lastName)
        statement.setString(5, user.email)
        statement.setString(6, user.city)
        statement.setString(7, user.country)
        statement.setInt(8, user.age)
        statement.setString(9, user.gender)
        statement.setString(10, user.description)
        statement.setInt(11, id)
        statement.executeUpdate()
    }

    suspend fun getUserById(id: Int): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val user = User(
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
            return@withContext user
        } else {
            throw Exception("User not found")
        }
    }

    override suspend fun getUserByLogin(login: String): User = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_LOGIN)
        statement.setString(1, login)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val user = User(
                login = resultSet.getString("LOGIN"),
                password = resultSet.getString("PASSWORD"),
                firstName = resultSet.getString("FIRSTNAME"),
                lastName = resultSet.getString("LASTNAME"),
                email = resultSet.getString("EMAIL"),
                city = resultSet.getString("COUNTRY"),
                country = resultSet.getString("AGE"),
                age = resultSet.getInt("AGE"),
                gender = resultSet.getString("GENDER"),
                description = resultSet.getString("DESCRIPTION"),
            )
            return@withContext user
        } else {
            throw Exception("User not found")
        }
    }

    suspend fun deleteUserById(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_USER)
        statement.setInt(1, id)
        statement.executeUpdate()
    }

}