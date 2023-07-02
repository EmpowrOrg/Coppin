dependencies {
    implementation(project(Deps.Models.main))
    api(project(Deps.Db.main))
    implementation(Deps.Exposed.core)
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.serialization)
    implementation(Deps.Lettuce.main)
    implementation(project(Deps.Utils.main))
    implementation(project(Deps.Utils.serialization))
}
