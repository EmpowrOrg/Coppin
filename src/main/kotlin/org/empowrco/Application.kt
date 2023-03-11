package org.empowrco

import io.ktor.server.application.Application
import org.empowrco.coppin.db.DatabaseFactory
import org.empowrco.plugins.configureCallLogging
import org.empowrco.plugins.configureFreeMarker
import org.empowrco.plugins.configureHTTP
import org.empowrco.plugins.configureKoin
import org.empowrco.plugins.configureRouting
import org.empowrco.plugins.configureSecurity
import org.empowrco.plugins.configureSerialization
import org.empowrco.plugins.configureSessions
import org.empowrco.plugins.configureStatusPages

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureHTTP()
    configureFreeMarker()
    configureSerialization()
    configureStatusPages()
    configureKoin()
    configureCallLogging()
    configureSecurity()
    configureSessions()
    configureRouting()
    DatabaseFactory.init()
}
