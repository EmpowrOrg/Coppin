package org.empowrco.coppin.courses.presenters

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coursesPresentersModule = module {
    singleOf(::RealCoursesPortalPresenter) { bind<CoursesPortalPresenter>() }
}
