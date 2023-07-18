package org.empowrco.coppin.sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.cookie
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.models.responses.EdxEnrollmentsResponse
import org.empowrco.coppin.models.responses.EdxGradeResponse
import org.empowrco.coppin.utils.serialization.json
import java.util.concurrent.TimeUnit

interface EdxSource {
    suspend fun getCourses(): Result<EdxCoursesResponse>
    suspend fun getCourse(id: String): Result<EdxCourse>
    suspend fun getGrades(id: String): Result<EdxGradeResponse>
    suspend fun getStudentsForCourse(id: String): Result<EdxEnrollmentsResponse>
}

@OptIn(InternalSerializationApi::class)

internal class RealEdxSource(private val cache: Cache) : EdxSource {

    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            val timeout = TimeUnit.MINUTES.toMillis(1)
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
            connectTimeoutMillis = timeout
        }
        defaultRequest {
            url(System.getenv("EDX_API_URL"))
            header("Referer", System.getenv("EDX_API_URL"))
        }
    }

    private val csrfKey = "X-CSRFToken"
    private val authenticationKey = "Authorization"
    private val shouldAuthKey = "X-Should-Authenticate"
    private val skipKey = "X-Should-Skip-All"

    init {
        client.plugin(HttpSend).intercept { request ->
            if (request.headers.contains(skipKey)) {
                return@intercept execute(request)
            }
            val appendedCsrf = appendCsrfToken(request)
            if (!appendedCsrf) {
                obtainCsrf()
                appendCsrfToken(request)
            }
            if (request.headers[shouldAuthKey]?.toBoolean() == true) {
                val appendedJwt = appendJwt(request)
                if (!appendedJwt) {
                    obtainJwt()
                    appendJwt(request)
                }
            }
            execute(request)
        }
    }

    private suspend fun appendCsrfToken(request: HttpMessageBuilder): Boolean {
        val csrfToken = cache.get(csrfKey, XcsrfToken::class.serializer())?.token
        if (csrfToken != null) {
            request.header(csrfKey, csrfToken)
            request.cookie("csrftoken", csrfToken)
        }
        return csrfToken != null
    }

    private suspend fun appendJwt(request: HttpMessageBuilder): Boolean {
        val authKey = cache.get(authenticationKey, Jwt::class.serializer())?.jwt
        if (authKey != null) {
            request.header(authenticationKey, authKey)
        }
        return authKey != null
    }

    override suspend fun getCourses(): Result<EdxCoursesResponse> {
        return get<EdxCoursesResponse>("api/courses/v1/courses/?page_size=100")
    }

    override suspend fun getCourse(id: String): Result<EdxCourse> {
        return get<EdxCourse>("api/courses/v1/courses/$id")
    }

    override suspend fun getGrades(id: String): Result<EdxGradeResponse> {
        return get<EdxGradeResponse>("api/grades/v1/gradebook/$id/", auth = true)
    }

    override suspend fun getStudentsForCourse(id: String): Result<EdxEnrollmentsResponse> {
        return get<EdxEnrollmentsResponse>(
            "api/enrollment/v1/enrollments",
            params = mapOf("course_id" to id),
            auth = true,
        )
    }

    private suspend inline fun <reified T : Any> get(
        path: String,
        params: Map<String, String> = mapOf(),
        auth: Boolean = false,
    ): Result<T> {
        val response = client.get(path) {
            params.forEach { (key, value) ->
                parameter(key, value)
            }
            contentType(ContentType.Application.Json)
            if (auth) {
                header(shouldAuthKey, true)
            }
        }
        return if (response.status == HttpStatusCode.OK) {
            try {
                Result.success(response.body<T>())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        } else {
            Result.failure(Exception(response.body<String>()))
        }
    }

    private suspend inline fun <reified T : Any> post(
        path: String,
        body: JsonObject,
        auth: Boolean = false,
    ): Result<T> {
        val response = client.post(path) {
            contentType(ContentType.Application.Json)
            if (auth) {
                header(shouldAuthKey, true)
            }
            setBody(body)
        }
        return if (response.status == HttpStatusCode.OK) {
            try {
                Result.success(response.body<T>())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        } else {
            val bodyString = response.body<String>()
            Result.failure(Exception(bodyString))
        }
    }

    private suspend inline fun obtainCsrf() {
        val response = client.get("login") {
            contentType(ContentType.Application.Json)
            header(skipKey, true)
        }
        val cookies = response.setCookie()
        val result = cookies.find {
            it.name == "csrftoken"
        }?.value
        if (result != null) {
            cache.set(csrfKey, json.encodeToString(XcsrfToken::class.serializer(), XcsrfToken(result)))
        }
    }

    private suspend inline fun obtainJwt() {
        val response: HttpResponse =
            client.submitForm(url = "oauth2/access_token/", formParameters = parameters {
                append("client_id", System.getenv("EDX_API_CLIENT_ID"))
                append("client_secret", System.getenv("EDX_API_CLIENT_SECRET"))
                append("grant_type", "client_credentials")
                append("token_type", "jwt")
            })
        when (response.status) {
            HttpStatusCode.OK -> {
                try {
                    val body = response.body<JwtResponse>()
                    val minutesToExpiration = TimeUnit.SECONDS.toMinutes(body.expiresIn) - 2
                    cache.set(
                        authenticationKey,
                        json.encodeToString(Jwt::class.serializer(), Jwt("JWT ${body.accessToken}")),
                        TimeUnit.MINUTES.toMillis(minutesToExpiration)
                    )
                } catch (ex: Exception) {
                    println(ex.localizedMessage)
                }
            }

            else -> {
                val bodyString = response.body<String>()
                println(bodyString)
            }
        }
    }

}

@Serializable
private data class JwtResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Long,
)

@Serializable
private data class Jwt(val jwt: String)

@Serializable
private data class XcsrfToken(val token: String)
