package org.empowrco.coppin.admin.presenters

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val adminPresenterModule = module {
    singleOf(::RealAdminOrgPresenter) { bind<AdminOrgPresenter>() }
    singleOf(::RealAdminAiPresenter) { bind<AdminAiPresenter>() }
}
