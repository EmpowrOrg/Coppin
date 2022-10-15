dependencies {
    implementation(Deps.Exposed.core)
    implementation(Deps.Exposed.jdbc)
    implementation(Deps.Hikari.main)
    implementation(Deps.Postgresql.main)
    implementation(project(Deps.Models.main))
    implementation(Deps.Exposed.dateTime)
}
