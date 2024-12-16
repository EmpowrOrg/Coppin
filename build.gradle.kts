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
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${ConfigData.isDebug}")
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

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            // jvmTarget.set(JvmTarget.JVM_1_8)
            freeCompilerArgs.add("-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI")
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }
    /*    tasks.withType(KotlinCompile::class.java).all {
            compilerOptions {
                // jvmTarget.set(JvmTarget.JVM_1_8)
                freeCompilerArgs.add("-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI")
                freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            }
        }*/

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(Deps.Kotlin.dateTime)
        if (!name.contains("logs")) {
            implementation(project(Deps.Utils.logs))
        }
        if (name.contains("api")) {
            implementation(Deps.Ktor.core)
            implementation(Deps.Koin.main)
            implementation(project(Deps.Utils.routing))
            implementation(Deps.Ktor.auth)
        }
        if (name.contains("presenters")) {
            implementation(Deps.Koin.main)
            implementation(project(Deps.Utils.main))
        }
        if (name.contains("backend")) {
            implementation(project(Deps.Sources.main))
            implementation(Deps.Koin.main)
        }
        testImplementation(kotlin("test"))
    }
    tasks.test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation(project(Deps.Admin.main))
    implementation(project(Deps.Admin.presenters))
    implementation(project(Deps.Admin.api))
    implementation(project(Deps.Assignment.main))
    implementation(project(Deps.Assignment.presenters))
    implementation(project(Deps.Assignment.api))
    implementation(project(Deps.Courses.main))
    implementation(project(Deps.Courses.api))
    implementation(project(Deps.Courses.presenters))
    implementation(project(Deps.Languages.main))
    implementation(project(Deps.Languages.api))
    implementation(project(Deps.Languages.presenters))
    implementation(project(Deps.Sources.main))
    implementation(project(Deps.Db.main))
    implementation(project(Deps.Models.main))
    implementation(project(Deps.Users.api))
    implementation(project(Deps.Users.main))
    implementation(project(Deps.Users.presenters))
    implementation(project(Deps.Utils.routing))
    implementation(project(Deps.Utils.authenticator))
    implementation(Deps.Ktor.auth)
    implementation(Deps.Koin.main)
    implementation(Deps.Koin.logger)
    implementation(Deps.Ktor.callLogging)
    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.headers)
    implementation(Deps.Ktor.sessions)
    implementation(Deps.Exposed.core)
    implementation(Deps.Ktor.jwt)
    implementation(Deps.Ktor.statusPages)
    implementation(Deps.Ktor.contentNegotiation)
    implementation(Deps.Ktor.json)
    implementation(Deps.Ktor.netty)
    implementation(Deps.Ktor.freeMarker)
    implementation(Deps.Logback.main)
    implementation(project(Deps.Utils.main))
    implementation(project(Deps.Utils.serialization))
}


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
/*val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
}*/
