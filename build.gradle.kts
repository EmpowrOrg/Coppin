import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version Versions.kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.kotlin
    id("io.ktor.plugin") version Versions.ktor
}

group = ConfigData.baseGroup
version = ConfigData.version
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    }

    tasks.withType(KotlinCompile::class.java).all {
        kotlinOptions.freeCompilerArgs = listOf(
            "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
            "-opt-in=kotlin.RequiresOptIn"
        ) + kotlinOptions.freeCompilerArgs
    }

    dependencies {
        implementation(kotlin("stdlib"))
        testImplementation(kotlin("test"))
    }
    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(Deps.Download.main))
    implementation(project(Deps.Download.api))
    implementation(project(Deps.Assignment.main))
    implementation(project(Deps.Assignment.api))
    implementation(project(Deps.Sources.main))
    implementation(project(Deps.Command.main))
    implementation(project(Deps.Db.main))
    implementation(project(Deps.Utils.routing))
    implementation(Deps.Koin.main)
    implementation(Deps.Ktor.callLogging)
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.headers)
    implementation(Deps.Exposed.core)
    implementation(Deps.Ktor.statusPages)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(Deps.Ktor.json)
    implementation(Deps.Ktor.netty)
    implementation(Deps.Logback.main)
    implementation(project(Deps.Utils.main))
}


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
