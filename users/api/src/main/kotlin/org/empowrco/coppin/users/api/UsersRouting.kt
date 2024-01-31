package org.empowrco.coppin.users.api

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.empowrco.coppin.users.presenters.CreateAccessKey
import org.empowrco.coppin.users.presenters.DeleteAccessKey
import org.empowrco.coppin.users.presenters.GetCurrentUserRequest
import org.empowrco.coppin.users.presenters.GetUserRequest
import org.empowrco.coppin.users.presenters.GetUsersRequest
import org.empowrco.coppin.users.presenters.UpdateUserRequest
import org.empowrco.coppin.users.presenters.UsersPresenters
import org.empowrco.coppin.utils.routing.Breadcrumbs
import org.empowrco.coppin.utils.routing.UserSession
import org.empowrco.coppin.utils.routing.error
import org.empowrco.coppin.utils.routing.errorRedirect
import org.empowrco.coppin.utils.routing.respondFreemarker
import org.koin.ktor.ext.inject

fun Application.usersRouting() {
    val presenter: UsersPresenters by inject()
    routing {
        authenticate("auth-session") {
            route("users") {
                get {
                    val email = call.sessions.get<UserSession>()?.email
                    presenter.getUsers(GetUsersRequest(email)).fold({
                        call.respondFreemarker("users.ftl", it)
                    }, {
                        call.errorRedirect(it.localizedMessage, "/")
                    })
                }

            }
            get("user") {
                val email = call.sessions.get<UserSession>()?.email ?: run {
                    call.errorRedirect("User Id Not Found", "/login")
                    return@get
                }
                presenter.getCurrentUser(GetCurrentUserRequest(email)).fold({
                    call.respondRedirect("/user/${it.id}")
                }, {
                    call.errorRedirect(it.localizedMessage, "/login")
                })
            }
            route("user/{uuid}") {
                get {
                    val email = call.sessions.get<UserSession>()?.email.toString()
                    val request = GetUserRequest(
                        id = call.parameters["uuid"].toString(),
                        currentUser = email
                    )
                    presenter.getUser(request).fold({
                        call.respondFreemarker("user.ftl", it, Breadcrumbs(
                            crumbs = buildList {
                                if (it.isAdmin) {
                                    add(Breadcrumbs.Crumb("manage_accounts", "Users", "/users"))
                                }
                                add(
                                    Breadcrumbs.Crumb(
                                        if (it.isAdmin) {
                                            null
                                        } else {
                                            "account_circle"
                                        }, it.firstName, null
                                    )
                                )
                            }
                        ))
                    }, {
                        call.errorRedirect(it.localizedMessage)
                    })
                }
                post {
                    val params = call.receiveParameters()
                    val request = UpdateUserRequest(
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
                route("keys/{keyId?}") {
                    post {
                        val request = call.receive<CreateAccessKey>()
                        presenter.createKey(request).fold({
                            call.respond(it)
                        }, {
                            call.error(it)
                        })
                    }
                    delete {
                        val request = call.receive<DeleteAccessKey>()
                        presenter.deleteKey(request).fold({
                            call.respond(it)
                        }, {
                            call.error(it)
                        })
                    }
                }
            }
        }
        get("signout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/")
        }
    }
}
