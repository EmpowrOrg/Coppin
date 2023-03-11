dependencies {
    implementation(Deps.Ktor.auth)
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.freeMarker)
    implementation(Deps.Ktor.json)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(project(Deps.Utils.main))
    implementation(project(Deps.Models.main))
    implementation(Deps.Kotlin.dateTime)
}
