package org.empowrco.coppin.assignment.presenters

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val assignmentPresentersModule = module {
    singleOf(::RealAssignmentApiPresenter) { bind<AssignmentApiPresenter>() }
    singleOf(::RealAssignmentPortalPresenter) { bind<AssignmentPortalPresenter>() }
}
