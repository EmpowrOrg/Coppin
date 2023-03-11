package org.empowrco.coppin.utils.authenticator

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authenticatorModule = module {
    singleOf(::RealAuthenticator) { bind<Authenticator>() }
}
