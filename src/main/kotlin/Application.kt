package com.example

import com.example.auth.configureAuthentication
import com.example.database.configureDatabases
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080,
        module = Application::module
    ).start(true)
}

fun Application.module() {
    val deps = configureDependencies()
    configureDatabases(deps.databaseRepository)
    configureAuthentication(deps.authController)
    configureSerialization()
    configureSockets()
    configureSecurity()
    configureHTTP()
    configureRouting()

}
