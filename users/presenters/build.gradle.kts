plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}
dependencies {
    implementation(project(Deps.Models.main))
    implementation(project(Deps.Users.Backend.main))
    implementation(project(Deps.Utils.authenticator))
    implementation(project(Deps.Utils.main))
    implementation(Deps.Ktor.json)
}
