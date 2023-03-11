plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Deps.Koin.main)
    implementation(project(Deps.Models.main))
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Ktor.json)
    implementation(project(Deps.Assignment.Backend.main))
    implementation(project(Deps.Utils.Diff.main))
    implementation(project(Deps.Utils.main))
    implementation("org.apache.commons:commons-text:1.10.0")

    testImplementation(project(Deps.Assignment.Backend.fakes))
    testImplementation(project(Deps.Utils.Diff.fakes))
}
