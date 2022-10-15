package org.empowrco.coppin.utils.routing

import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.header
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.util.KtorDsl
import io.ktor.util.pipeline.PipelineContext
import org.empowrco.coppin.utils.UnauthorizedException
import java.io.File

@KtorDsl
@JvmName("postTyped")
inline fun <reified T : Any> Route.authPost(
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route = post {
    handlePost(body)
}

@KtorDsl
@JvmName("postTypedPath")
inline fun <reified T : Any> Route.authPost(
    path: String,
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route = post(path) {
    handlePost(body)
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.handlePost(
    body: suspend (call: ApplicationCall) -> T,
) {
    authenticate(call)
    val response = body(call)
    respond(response)
}

fun authenticate(call: ApplicationCall) {
    val bearer = call.request.header("Authorization") ?: throw UnauthorizedException
    val idToken = bearer.substringAfter("Bearer").trim()
    val secret = System.getenv("secret")
    if (idToken != secret) {
        throw UnauthorizedException
    }
}

@KtorDsl
@JvmName("putTyped")
inline fun <reified T : Any> Route.authPut(
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route = put {
    handlePut(body)
}

@KtorDsl
@JvmName("putTypedPath")
inline fun <reified T : Any> Route.authPut(
    path: String,
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route = put(path) {
    handlePut(body)
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.handlePut(
    body: suspend (call: ApplicationCall) -> T,
) {
    authenticate(call)
    val response = body(call)
    respond(response)
}

@KtorDsl
inline fun Route.authDelete(
    crossinline body: suspend (call: ApplicationCall) -> Unit,
): Route {
    return delete {
        authenticate(call)
        body(call)
        respond(HttpStatusCode.OK)
    }
}

/**
 * Builds a route to match `DELETE` requests.
 * @see [Application.routing]
 */
@KtorDsl
inline fun Route.authDelete(
    path: String,
    crossinline body: suspend (call: ApplicationCall) -> Unit,
): Route {
    return delete(path) {
        authenticate(call)
        body(call)
        respond(HttpStatusCode.OK)
    }
}

@KtorDsl
inline fun <reified T : Any> Route.authGet(
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route {
    return get {
        authenticate(call)
        val response = body(call)
        respond(response)
    }
}

@KtorDsl
inline fun <reified T : Any> Route.authGet(
    path: String,
    crossinline body: suspend (call: ApplicationCall) -> T,
): Route {
    return get(path) {
        authenticate(call)
        val response = body(call)
        respond(response)
    }
}

suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.respond(
    response: T
) {
    if (response is File) {
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, response.name)
                .toString()
        )

        call.respondFile(response)
    } else {
        call.respond(response)
    }
}
