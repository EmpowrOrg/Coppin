plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}
dependencies {
    implementation(project(Deps.Assignment.presenters))
    implementation(Deps.Ktor.json)
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.gson)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(Deps.Kotlin.coroutines)
    implementation(project(Deps.Models.main))
    implementation(project(Deps.Utils.routing))
}
