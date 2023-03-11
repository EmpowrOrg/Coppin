package org.empowrco.copping.users.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.empowrco.coppin.users.presenters.GetUserRequest
import org.empowrco.coppin.users.presenters.LoginRequest
import org.empowrco.coppin.users.presenters.PatchUserRequest
import org.empowrco.coppin.users.presenters.RegisterRequest
import org.empowrco.coppin.users.presenters.UsersPresenters
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.usersRouting() {
    val presenter: UsersPresenters by inject()
    routing {
        route("users") {
            get {
                presenter.getUsers().fold({
                    call.respondFreemarker("users.ftl", it)
                }, {
                    call.errorRedirect(it.localizedMessage, "/")
                })
            }

        }
        route("login") {
            get {
                call.respondFreemarker("login.ftl", mapOf("hideSideNav" to true))
            }
            post {
                val params = call.receiveParameters()
                presenter.login(
                    LoginRequest(
                        email = params["email"].toString(),
                        password = params["password"].toString(),
                    )
                ).fold({
                    call.respondRedirect("/")
                }, {
                    call.errorRedirect(it.localizedMessage, "/")
                })
            }
        }
        route("register") {
            get {
                call.respondFreemarker("register.ftl", mapOf("hideSideNav" to true))
            }
            post {
                val params = call.receiveParameters()
                presenter.register(
                    RegisterRequest(
                        firstName = params["firstName"].toString(),
                        lastName = params["lastName"].toString(),
                        email = params["email"].toString(),
                        password = params["password"].toString(),
                        confirmPassword = params["confirmPassword"].toString()
                    )
                ).fold({
                    call.respondRedirect("/")
                }, {
                    call.errorRedirect(it.localizedMessage)
                })
            }
        }
        route("user/{uuid}") {
            get {
                val request = GetUserRequest(
                    id = call.parameters["uuid"].toString()
                )
                presenter.getUser(request).fold({
                    call.respondFreemarker("user.ftl", it)
                }, {
                    call.errorRedirect(it.localizedMessage)
                })
            }
            patch {
                val params = call.receiveParameters()
                val request = PatchUserRequest(
                    id = call.parameters["uuid"].toString(),
                    email = params["email"].toString(),
                    firstName = params["firstName"].toString(),
                    lastName = params["lastName"].toString(),
                    authorized = params["authorized"].toString(),
                    type = params["type"].toString(),
                )
                presenter.updateUser(request).fold({
                    call.respondRedirect("/users")
                }, {
                    call.errorRedirect(it.localizedMessage)
                })
            }
        }
    }
}
