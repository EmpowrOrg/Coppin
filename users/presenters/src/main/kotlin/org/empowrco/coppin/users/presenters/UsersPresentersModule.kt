package org.empowrco.coppin.users.presenters

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val usersPresenterModule = module {
    singleOf(::RealUsersRoutingPresenter) { bind<UsersRoutingPresenter>() }
}
