package org.empowrco.coppin.utils.diff

import org.koin.dsl.module

val diffUtilsModule = module {
    single<DiffUtil> { RealDiffUtil(get(), get()) }
}
