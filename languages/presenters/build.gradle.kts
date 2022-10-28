plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Deps.Koin.main)
    implementation(project(Deps.Models.main))
    implementation(project(Deps.Utils.routing))
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Ktor.json)
    implementation(project(Deps.Languages.Backend.main))
    implementation(project(Deps.Utils.Diff.main))
    implementation(project(Deps.Utils.main))
    implementation("org.apache.commons:commons-text:1.10.0")
}
