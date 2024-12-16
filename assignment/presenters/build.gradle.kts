plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Deps.Koin.main)
    implementation(project(Deps.Models.main))
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Ktor.json)
    implementation(project(Deps.Assignment.Backend.main))
    implementation(project(Deps.Utils.main))
    implementation(Deps.Apache.commonsText)
    implementation(project(Deps.Utils.serialization))
    implementation(project(Deps.Utils.Files.main))

    testImplementation(project(Deps.Assignment.Backend.fakes))
    testImplementation(project(Deps.Utils.Files.fakes))
}
