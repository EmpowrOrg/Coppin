package org.empowrco.coppin.sources

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sourcesModule = module {
    singleOf(::RealAssignmentSource) { bind<AssignmentSource>() }
    singleOf(::RealLanguageSource) { bind<LanguagesSource>() }
    singleOf(::RealAssignmentCodesSource) { bind<AssignmentCodesSource>() }
    singleOf(::RealUsersSource) { bind<UsersSource>() }
}
