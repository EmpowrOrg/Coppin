plugins {
    id("org.jetbrains.kotlin.plugin.serialization")
}
dependencies {
    implementation(project(Deps.Models.main))
    api(project(Deps.Db.main))
    implementation(Deps.Exposed.core)
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Lettuce.main)
    implementation(Deps.Ktor.Client.core)
    implementation(Deps.Ktor.Client.logging)
    implementation(Deps.Ktor.Client.serialization)
    implementation(Deps.Ktor.Client.json)
    implementation(Deps.Ktor.Client.auth)
    implementation(Deps.Ktor.Client.engine)
    implementation(Deps.OpenAi.chatGpt)
    implementation(project(Deps.Utils.main))
    implementation(project(Deps.Utils.serialization))
}
