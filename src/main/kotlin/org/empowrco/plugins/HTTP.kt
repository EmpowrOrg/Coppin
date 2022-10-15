package org.empowrco.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cachingheaders.CachingHeaders

fun Application.configureHTTP() {
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                else -> null
            }
        }
    }
}
