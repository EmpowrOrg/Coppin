package org.empowrco.coppin.languages.backend

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val languagesBackendModule = module {
    singleOf(::RealLanguagesRepository) { bind<LanguagesRepository>() }
    singleOf(::RealLanguagesApiRepository) { bind<LanguagesApiRepository>() }
}
