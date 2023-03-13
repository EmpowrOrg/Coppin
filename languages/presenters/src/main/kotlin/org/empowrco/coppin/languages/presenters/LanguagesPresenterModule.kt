package org.empowrco.coppin.languages.presenters

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val languagesPresentersModule = module {
    singleOf(::RealLanguagesPresenter) { bind<LanguagesPresenter>() }
    singleOf(::RealLanguagesApiPresenter) { bind<LanguagesApiPresenter>() }
}
