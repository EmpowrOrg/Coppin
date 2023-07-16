package org.empowrco.coppin.sources

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sourcesModule = module {
    singleOf(::RealAssignmentSource) { bind<AssignmentSource>() }
    singleOf(::RealLanguageSource) { bind<LanguagesSource>() }
    singleOf(::RealAssignmentCodesSource) { bind<AssignmentCodesSource>() }
    singleOf(::RealUsersSource) { bind<UsersSource>() }
    singleOf(::RealSubmissionSource) { bind<SubmissionSource>() }
    singleOf(::RealCoursesSource) { bind<CoursesSource>() }
    singleOf(::RealEdxSource) { bind<EdxSource>() }
    singleOf(::RealSubjectSource) { bind<SubjectSource>() }
    single {
        if (System.getenv("DEBUG").toBoolean()) {
            DebugCache()
        } else {
            RealCache()
        }
    }
}
