package org.empowrco.coppin.users.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val usersBackendModule = module {
    singleOf(::RealUsersRepository) { bind<UsersRepository>() }
}
