package com.example.database

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases(databaseRepository: DatabaseRepository) {

    routing {

        // add user
        post("/users") {
            val user = call.receive<User>()
            val id = databaseRepository.addUser(user)
            if (id != null) {
                call.respond(HttpStatusCode.Created, "Successfully added user $id")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Something went wrong")
            }

        }

        // get user
        get("/users/{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = databaseRepository.getUserById(id)
                if (user == null) {
                    call.respond(HttpStatusCode.OK, user)
                    log.info("Successfully fetched user $id")
                } else {
                    log.error("Error getting user $id")
                    call.respond(HttpStatusCode.NotFound)
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Something went wrong")
            }

        }

        // update user
        put("/users/{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = call.receive<User>()
                databaseRepository.updateUser(id, user)
                call.respond(HttpStatusCode.OK)
                log.info("Successfully updated user $id")
            } catch (e: Exception) {
                log.error(e.message ?: "Something went wrong")
            }

        }

        // delete user
        delete("/users/{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                databaseRepository.deleteUserById(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                log.error(e.message ?: "Something went wrong")
            }

        }
    }
}

/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        log.info("Using embedded H2 database for testing; replace this flag to use postgres")
        return DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
    } else {
        val url = environment.config.property("postgres.url").getString()
        log.info("Connecting to postgres database at $url")
        val user = environment.config.property("postgres.user").getString()
        val password = environment.config.property("postgres.password").getString()

        return DriverManager.getConnection(url, user, password)
    }
}
