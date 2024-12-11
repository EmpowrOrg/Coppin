package org.empowrco.coppin.utils.files

import org.koin.dsl.module

val fileUtilsModule = module {
    single<FileUtil> { RealFileUtil }
    single<FileUploader> { RealFileUploader() }
}
