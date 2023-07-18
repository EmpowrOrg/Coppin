package org.empowrco.coppin.courses.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coursesBackendModule = module {
    singleOf(::RealCoursesPortalRepository) { bind<CoursesPortalRepository>() }
}
