plugins {
    kotlin("jvm")
}

group = "org.empowrco"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":command"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}