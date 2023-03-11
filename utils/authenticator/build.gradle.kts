group = "${ConfigData.baseGroup}.utils.authenticator"
dependencies {
    implementation(project(Deps.Sources.main))
    implementation(project(Deps.Models.main))
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.auth)
    implementation(Deps.Ktor.jwt)
    implementation(Deps.JWT.main)
}
