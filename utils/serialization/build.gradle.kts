plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}
group = "${ConfigData.baseGroup}.utils.serialization"
dependencies {
    implementation(Deps.Ktor.serialization)
}
