plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Deps.Koin.main)
    implementation(project(Deps.Models.main))
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Ktor.json)
    implementation(project(Deps.Admin.Backend.main))
    implementation(project(Deps.Utils.main))
    implementation(project(Deps.Utils.authenticator))
    implementation(Deps.Apache.commonsText)
    implementation(project(Deps.Utils.serialization))

}
