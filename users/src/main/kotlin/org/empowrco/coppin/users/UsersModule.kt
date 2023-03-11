package org.empowrco.coppin.users

import org.empowrco.coppin.users.backend.usersBackendModule
import org.empowrco.coppin.users.presenters.usersPresenterModule

val usersModule = listOf(usersBackendModule, usersPresenterModule)
