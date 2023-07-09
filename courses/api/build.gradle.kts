plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}
dependencies {
    implementation(project(Deps.Courses.presenters))
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.json)
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(Deps.Ktor.gson)
    implementation(Deps.Ktor.freeMarker)
    implementation(Deps.Kotlin.coroutines)
    implementation(project(Deps.Models.main))
    implementation(project(Deps.Assignment.presenters))
}
