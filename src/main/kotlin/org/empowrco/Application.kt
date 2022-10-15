package org.empowrco

import io.ktor.server.application.Application
import org.empowrco.coppin.db.DatabaseFactory
import org.empowrco.plugins.configureCallLogging
import org.empowrco.plugins.configureHTTP
import org.empowrco.plugins.configureKoin
import org.empowrco.plugins.configureRouting
import org.empowrco.plugins.configureSerialization
import org.empowrco.plugins.configureStatusPages

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureRouting()
    configureHTTP()
    configureSerialization()
    configureStatusPages()
    configureKoin()
    configureCallLogging()
    DatabaseFactory.init()
}
