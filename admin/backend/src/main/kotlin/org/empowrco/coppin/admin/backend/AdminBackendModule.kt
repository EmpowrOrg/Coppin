package org.empowrco.coppin.admin.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val adminBackendModule = module {
    singleOf(::RealAdminSecurityRepository) { bind<AdminSecurityRepository>() }
}
