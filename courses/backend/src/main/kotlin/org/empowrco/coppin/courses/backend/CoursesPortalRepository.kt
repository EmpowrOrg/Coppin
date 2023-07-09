package org.empowrco.coppin.courses.backend

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.responses.EdxCoursesResponse
import org.empowrco.coppin.sources.CoursesSource
import java.util.UUID
import java.util.concurrent.TimeUnit

interface CoursesPortalRepository {
    suspend fun getCourses(): List<Course>
    suspend fun createCourse(course: Course)
    suspend fun updateCourse(course: Course): Boolean
    suspend fun deleteCourse(id: UUID): Boolean
    suspend fun getEdxCourses(): Result<EdxCoursesResponse>
}

internal class RealCoursesPortalRepository(
    private val coursesSource: CoursesSource,
) : CoursesPortalRepository {

    override suspend fun getCourses(): List<Course> {
        return coursesSource.getCourses()
    }

    override suspend fun updateCourse(course: Course): Boolean {
        return coursesSource.updateCourse(course)
    }

    override suspend fun createCourse(course: Course) {
        coursesSource.createCourse(course)
    }

    override suspend fun deleteCourse(id: UUID): Boolean {
        return coursesSource.deleteCourse(id)
    }

    override suspend fun getEdxCourses(): Result<EdxCoursesResponse> {
        return executeRequest("courses/v1/courses/?page_size=100")
    }

    private val client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json(org.empowrco.coppin.utils.serialization.json)
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

    private suspend fun executeRequest(
        path: String,
    ): Result<EdxCoursesResponse> {
        val url = System.getenv("EDX_API_URL")
        val response = client.get("$url$path") {
            contentType(ContentType.Application.Json)
        }
        return if (response.status == HttpStatusCode.OK) {
            try {
                Result.success(response.body<EdxCoursesResponse>())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        } else {
            Result.failure(Exception(response.body<String>()))
        }
    }
}
