package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.empowrco.coppin.admin.adminModule
import org.empowrco.coppin.assignment.assignmentModule
import org.empowrco.coppin.courses.coursesModules
import org.empowrco.coppin.languages.languagesModule
import org.empowrco.coppin.sources.sourcesModule
import org.empowrco.coppin.users.usersModule
import org.empowrco.coppin.utils.authenticator.authenticatorModule
import org.empowrco.coppin.utils.utilsModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            assignmentModule + authenticatorModule + coursesModules + sourcesModule + utilsModule + languagesModule +
                    usersModule + adminModule
        )
    }
}
