package com.example

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
    configureSerialization()
    configureDatabases()
    configureSockets()
    configureSecurity()
    configureHTTP()
    configureRouting()
}
