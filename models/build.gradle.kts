plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Deps.Ktor.json)
    implementation(Deps.Kotlin.dateTime)
}
