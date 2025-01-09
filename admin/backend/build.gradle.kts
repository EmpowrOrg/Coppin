plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(Deps.Sources.main))
    implementation(project(Deps.Models.main))
    implementation(Deps.Ktor.json)
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.Client.core)
    implementation(Deps.Ktor.Client.serialization)
    implementation(Deps.Ktor.Client.json)
    implementation(Deps.Ktor.Client.auth)
    implementation(Deps.Ktor.Client.engine)
    testImplementation(project(Deps.Sources.fakes))
    testImplementation(Deps.Kotlin.dateTime)
    testImplementation(project(Deps.Utils.main))
    implementation(project(Deps.Utils.serialization))
}

