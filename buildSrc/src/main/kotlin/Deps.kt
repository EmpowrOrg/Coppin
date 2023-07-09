object Deps {

    object Apache {
        val commonsText by lazy { "org.apache.commons:commons-text:1.10.0" }
    }

    object Assignment {
        val api by lazy { ":assignment:api" }
        val presenters by lazy { ":assignment:presenters" }

        object Backend {
            val main by lazy { ":assignment:backend" }
            val fakes by lazy { ":assignment:backend:fakes" }
        }

        val main by lazy { ":assignment" }
    }

    object Courses {
        val api by lazy { ":courses:api" }
        val presenters by lazy { ":courses:presenters" }

        object Backend {
            val main by lazy { ":courses:backend" }
            val fakes by lazy { ":courses:backend:fakes" }
        }

        val main by lazy { ":courses" }
    }


    object Languages {
        val api by lazy { ":languages:api" }
        val presenters by lazy { ":languages:presenters" }

        object Backend {
            val main by lazy { ":languages:backend" }
            val fakes by lazy { ":languages:backend:fakes" }
        }

        val main by lazy { ":languages" }
    }

    object Users {
        val api by lazy { ":users:api" }
        val main by lazy { ":users" }
        val presenters by lazy { ":users:presenters" }

        object Backend {
            val main by lazy { ":users:backend" }
            val fakes by lazy { ":users:backend:fakes" }
        }
    }

    object Utils {
        val routing by lazy { ":utils:routing" }
        val main by lazy { ":utils" }
        val files by lazy { ":utils:files" }
        val authenticator by lazy { ":utils:authenticator" }
        val logs by lazy { ":utils:logs" }
        val serialization by lazy { ":utils:serialization" }
    }

    object Exposed {
        val core by lazy { "org.jetbrains.exposed:exposed-core:${Versions.exposed}" }
        val jdbc by lazy { "org.jetbrains.exposed:exposed-jdbc:${Versions.exposed}" }
        val dateTime by lazy { "org.jetbrains.exposed:exposed-kotlin-datetime:${Versions.exposed}" }
    }

    object Hikari {
        val main by lazy { "com.zaxxer:HikariCP:5.0.1" }
    }

    object Postgresql {
        val main by lazy { "org.postgresql:postgresql:42.6.0" }
    }

    object Models {
        val main by lazy { ":models" }
    }

    object Db {
        val main by lazy { ":db" }
    }


    object JWT {
        val main by lazy { "com.auth0:java-jwt:${Versions.jwt}" }
    }

    object Koin {
        val main by lazy { "io.insert-koin:koin-ktor:${Versions.koin}" }
    }

    object Kotlin {
        val coroutines by lazy { "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4" }
        val dateTime by lazy { "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0" }
    }

    object Ktor {
        val core by lazy { "io.ktor:ktor-server-core-jvm:${Versions.ktor}" }
        val auth by lazy { "io.ktor:ktor-server-auth-jvm:${Versions.ktor}" }
        val headers by lazy { "io.ktor:ktor-server-caching-headers-jvm:${Versions.ktor}" }
        val contentNegotiation by lazy { "io.ktor:ktor-server-content-negotiation-jvm:${Versions.ktor}" }
        val json by lazy { "io.ktor:ktor-serialization-kotlinx-json-jvm:${Versions.ktor}" }
        val jwt by lazy { "io.ktor:ktor-server-auth-jwt-jvm:${Versions.ktor}" }
        val gson by lazy { "io.ktor:ktor-client-gson:${Versions.ktor}" }
        val netty by lazy { "io.ktor:ktor-server-netty-jvm:${Versions.ktor}" }
        val statusPages by lazy { "io.ktor:ktor-server-status-pages:${Versions.ktor}" }
        val test by lazy { "io.ktor:ktor-server-test-host:${Versions.ktor}" }
        val callLogging by lazy { "io.ktor:ktor-server-call-logging:${Versions.ktor}" }
        val sessions by lazy { "io.ktor:ktor-server-sessions:${Versions.ktor}" }
        val freeMarker by lazy { "io.ktor:ktor-server-freemarker:${Versions.ktor}" }
        val serialization by lazy { "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}" }

        object Client {
            val core by lazy { "io.ktor:ktor-client-core:${Versions.ktor}" }
            val serialization by lazy { "io.ktor:ktor-client-content-negotiation:${Versions.ktor}" }
            val json by lazy { "io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}" }
            val auth by lazy { "io.ktor:ktor-client-auth:${Versions.ktor}" }
            val engine by lazy { "io.ktor:ktor-client-apache:${Versions.ktor}" }
            val logging by lazy { "io.ktor:ktor-client-logging:${Versions.ktor}" }
        }
    }

    object Lettuce {
        val main by lazy { "io.lettuce:lettuce-core:${Versions.lettuce}" }
    }

    object Sources {
        val main by lazy { ":sources" }
        val fakes by lazy { ":sources:fakes" }
    }

    object Logback {
        val main by lazy { "ch.qos.logback:logback-classic:${Versions.logback}" }
    }
}
