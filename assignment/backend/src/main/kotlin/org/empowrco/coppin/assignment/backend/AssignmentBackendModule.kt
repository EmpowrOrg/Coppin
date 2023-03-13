package org.empowrco.coppin.assignment.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val assignmentBackendModule = module {
    singleOf(::RealAssignmentApiRepository) { bind<AssignmentApiRepository>() }
    singleOf(::RealAssignmentPortalRepository) { bind<AssignmentPortalRepository>() }
}
