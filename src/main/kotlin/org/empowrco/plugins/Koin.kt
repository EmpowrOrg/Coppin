package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.empowrco.coppin.assignment.assignmentModule
import org.empowrco.coppin.languages.languagesModule
import org.empowrco.coppin.sources.sourcesModule
import org.empowrco.coppin.users.usersModule
import org.empowrco.coppin.utils.authenticator.authenticatorModule
import org.empowrco.coppin.utils.utilsModule
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(assignmentModule + authenticatorModule + sourcesModule + utilsModule + languagesModule + usersModule)
    }
}
