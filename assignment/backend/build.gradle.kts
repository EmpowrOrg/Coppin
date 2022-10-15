dependencies {
    implementation(project(Deps.Sources.main))
    implementation(project(Deps.Models.main))
    implementation(Deps.Koin.main)
    testImplementation(project(Deps.Sources.fakes))
    testImplementation(Deps.Kotlin.dateTime)
    testImplementation(project(Deps.Utils.main))
}

