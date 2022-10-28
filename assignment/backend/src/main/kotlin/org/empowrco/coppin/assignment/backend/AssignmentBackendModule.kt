package org.empowrco.coppin.assignment.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val assignmentBackendModule = module {
    singleOf(::RealAssignmentRepository) { bind<AssignmentRepository>() }
    singleOf(::RealAssignmentPortalRepository) { bind<AssignmentPortalRepository>() }
}
