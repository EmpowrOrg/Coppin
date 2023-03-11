package org.empowrco.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.empowrco.coppin.utils.AssignmentLanguageSupportException
import org.empowrco.coppin.utils.DuplicateKeyException
import org.empowrco.coppin.utils.InvalidUuidException
import org.empowrco.coppin.utils.LanguageSupportException
import org.empowrco.coppin.utils.UnauthorizedException
import org.empowrco.coppin.utils.UnsupportedLanguage
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnsupportedLanguage> { call, cause ->
            respond(call, cause, HttpStatusCode.NotFound)
        }
        exception<LanguageSupportException> { call, cause ->
            respond(call, cause, HttpStatusCode.NotFound)
        }
        exception<AssignmentLanguageSupportException> { call, cause ->
            respond(call, cause, HttpStatusCode.NotFound)
        }
        exception<InvalidUuidException> {call, cause ->
            respond(call, cause, HttpStatusCode.BadRequest)
        }
        exception<CannotTransformContentToTypeException> { call, cause ->
            respond(call, cause, HttpStatusCode.BadRequest)
        }
        exception<ExposedSQLException> { call, cause ->
            respond(call, cause, HttpStatusCode.InternalServerError)
        }
        exception<UnauthorizedException> { call, cause ->
            respond(call, cause, HttpStatusCode.Unauthorized)
        }
        exception<MissingRequestParameterException> { call, cause ->
            respond(call, cause, HttpStatusCode.BadRequest)
        }
        exception<DuplicateKeyException> { call, cause ->
            respond(call, cause.duplicateKeyError, HttpStatusCode.Conflict)
        }
        exception<Throwable> { call, cause ->
            respond(call, cause, HttpStatusCode.InternalServerError)
        }
    }
}

private suspend fun respond(
    call: ApplicationCall,
    cause: Throwable,
    status: HttpStatusCode,
) {
    respond(call, cause.localizedMessage ?: cause.message, status)
}

private suspend fun respond(
    call: ApplicationCall,
    cause: String?,
    status: HttpStatusCode,
) {
    call.respond(status, ErrorStatus(cause))
}

@kotlinx.serialization.Serializable
private data class ErrorStatus(val error: String?)
