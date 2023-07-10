package org.empowrco.coppin.sources

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.cookie
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.http.setCookie
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.models.responses.EdxGradeResponse
import org.empowrco.coppin.utils.serialization.json
import java.util.concurrent.TimeUnit

interface EdxSource {
    suspend fun getCourses(): Result<EdxCoursesResponse>
    suspend fun getCourse(id: String): Result<EdxCourse>
    suspend fun getGrades(id: String): Result<EdxGradeResponse>
}

internal class RealEdxSource : EdxSource {

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
    }
    private val edxHeaders = mutableMapOf<String, String>()
    private val baseUrl = System.getenv("EDX_API_URL")

    init {
        edxHeaders["Referer"] = baseUrl
    }


    private suspend fun updateAuth() {
        val csrfToken = getCsrfTokenFromCookie().getOrNull() ?: return
        edxHeaders["X-CSRFToken"] = csrfToken
        val loginResult = loginJwt()
        println(loginResult)
        //val loginResult = loginCSRF<String>()
    }

    override suspend fun getCourses(): Result<EdxCoursesResponse> {
        return get<EdxCoursesResponse>("api/courses/v1/courses/?page_size=100")
    }

    override suspend fun getCourse(id: String): Result<EdxCourse> {
        return get<EdxCourse>("api/courses/v1/courses/$id")
    }

    override suspend fun getGrades(id: String): Result<EdxGradeResponse> {
        updateAuth()
        TODO()
    }

    private suspend inline fun getCsrfTokenFromCookie(): Result<String> {
        val response = client.get("${baseUrl}login") {
            contentType(ContentType.Application.Json)
            edxHeaders.forEach { (key, value) ->
                header(key, value)
            }
        }
        val cookies = response.setCookie()
        val result = cookies.find {
            it.name == "csrftoken"
        }?.value
        return if (result == null) {
            Result.failure(Exception("cookie not found"))
        } else {
            Result.success(result)
        }
    }

    private suspend inline fun <reified T : Any> get(
        path: String,
    ): Result<T> {

        val response = client.get("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            edxHeaders.forEach { (key, value) ->
                header(key, value)
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

    private suspend inline fun loginJwt() {
        val response: HttpResponse = client.submitForm(
            url = "${baseUrl}oauth2/access_token/",
            formParameters = parameters {
                append("client_id", "je9Kt6gN97SnufqcXIfhZOikVLlRFkpcsOjKlv1y")
                append(
                    "client_secret",
                    "QQ9YbGMYw5dJd8GvRgASn6QFuoUBlcxx51qjhSZ46djT7VsyQ15C5weVvLMSp3J4sxObGOmPSoARwDN2KVDoNOYgnYOAQ0UYRx9KVefALfTR3Vpm2k9GoHgkoxzLouJp"
                )
                append("grant_type", "client_credentials")
                append("token_type", "jwt")
            }
        ) {
            edxHeaders.forEach { (key, value) ->
                header(key, value)
            }
            edxHeaders["X-CSRFToken"]?.let {
                cookie("csrftoken", it)
            }
        }
        if (response.status == HttpStatusCode.OK) {
            try {
                val cookies = response.setCookie()
                println(cookies)
                val body = response.body<JwtResponse>()
                println(body)
            } catch (ex: Exception) {
                println(ex.localizedMessage)
            }
        } else if (response.status == HttpStatusCode.Unauthorized) {

        } else {
            val bodyString = response.body<String>()
            println(bodyString)
        }
    }

    private suspend inline fun <reified T : Any> loginCSRF(): Result<T> {
        val response: HttpResponse = client.submitForm(
            url = "${baseUrl}api/user/v1/account/login_session/",
            formParameters = parameters {
                append("email", System.getenv("EDX_API_CLIENT_ID"))
                append("password", System.getenv("EDX_API_CLIENT_SECRET"))
            }
        ) {
            edxHeaders.forEach { (key, value) ->
                header(key, value)
            }
            edxHeaders["X-CSRFToken"]?.let {
                cookie("csrftoken", it)
            }
        }
        return if (response.status == HttpStatusCode.OK) {
            try {
                val cookies = response.setCookie()
                println(cookies)
                Result.success(response.body<T>())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        } else {
            val bodyString = response.body<String>()
            Result.failure(Exception(bodyString))
        }
    }

    private suspend inline fun <reified T : Any> post(
        path: String,
        body: JsonObject,
    ): Result<T> {
        val response = client.post("$baseUrl$path") {
            contentType(ContentType.Application.Json)
            edxHeaders.forEach { (key, value) ->
                header(key, value)
            }
            edxHeaders["X-CSRFToken"]?.let {
                cookie("csrftoken", it)
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
}

@Serializable
private data class JwtResponse(@SerialName("access_token") val accessToken: String)
