dependencies {
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.json)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(project(Deps.Utils.main))
    implementation(Deps.Kotlin.dateTime)
}
