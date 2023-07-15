package org.empowrco.coppin.courses.presenters

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.courses.backend.CoursesPortalRepository
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.monthDayYear
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface CoursesPortalPresenter {
    suspend fun getCourses(request: GetCoursesRequest): Result<GetCoursesResponse>
    suspend fun getCourse(request: GetCourseRequest): Result<GetCourseResponse>
    suspend fun getUnlinkedCourses(request: GetCoursesRequest): Result<GetUnlinkedCoursesResponse>
    suspend fun linkCourses(request: LinkCoursesRequest): Result<Unit>
}

internal class RealCoursesPortalPresenter(
    private val repo: CoursesPortalRepository,
) : CoursesPortalPresenter {
    override suspend fun getCourses(request: GetCoursesRequest): Result<GetCoursesResponse> {
        val userId = request.id.toUuid() ?: return failure("Invalid uuid")
        val courses = repo.getLinkedCourses(userId)
        if (courses.isEmpty()) {
            return GetCoursesResponse(
                coursesCount = 0,
                courses = emptyList()
            ).toResult()
        }
        val edxCoursesResult = repo.getEdxCourses()
        if (edxCoursesResult.isFailure) {
            return Result.failure(edxCoursesResult.exceptionOrNull()!!)
        }
        val edxCourses = edxCoursesResult.getOrThrow().results // Should never throw
        return GetCoursesResponse(
            courses = courses.mapNotNull { course ->
                val edxCourse = edxCourses.find { it.id == course.edxId } ?: return@mapNotNull null
                updateCourse(edxCourse, course)
                GetCoursesResponse.Course(
                    id = course.id.toString(),
                    endDate = edxCourse.end?.monthDayYear() ?: "",
                    startDate = edxCourse.start.monthDayYear() ?: "",
                    name = edxCourse.name,
                    number = edxCourse.number,
                )
            },
            coursesCount = courses.size,
        ).toResult()
    }

    override suspend fun getUnlinkedCourses(request: GetCoursesRequest): Result<GetUnlinkedCoursesResponse> {
        val userId = request.id.toUuid() ?: return failure("Invalid uuid")
        val courses = repo.getLinkedCourses(userId)
        val edxCoursesResult = repo.getEdxCourses().getOrNull()?.results?.filterNot { edxCourse ->
            courses.any { it.edxId == edxCourse.id }
        } ?: return failure("Could not fetch edx courses")
        val edxCourses = if (edxCoursesResult.size <= 3) {
            listOf(edxCoursesResult)
        } else {
            edxCoursesResult.windowed(size = 3, partialWindows = true)
        }

        return GetUnlinkedCoursesResponse(
            rows = edxCourses.map { courseRow ->
                val one = GetUnlinkedCoursesResponse.Course(
                    name = courseRow[0].name,
                    id = courseRow[0].id,
                    dates = courseRow[0].start,
                    number = courseRow[0].number,
                    org = courseRow[0].org,
                )
                val two = courseRow.getOrNull(1)?.let {
                    GetUnlinkedCoursesResponse.Course(
                        name = it.name,
                        id = it.id,
                        dates = it.start,
                        number = it.number,
                        org = courseRow[0].org,
                    )
                }
                val three = courseRow.getOrNull(2)?.let {
                    GetUnlinkedCoursesResponse.Course(
                        name = it.name,
                        id = it.id,
                        dates = it.start,
                        number = it.number,
                        org = courseRow[0].org,
                    )
                }
                GetUnlinkedCoursesResponse.CourseRow(one, two, three)
            },
            count = edxCoursesResult.size,
        ).toResult()
    }

    override suspend fun linkCourses(request: LinkCoursesRequest): Result<Unit> {
        val userId = request.userId.toUuid() ?: return failure("Invalid user id")
        var failureStatement = ""
        val currentTime = LocalDateTime.now()
        request.classIds.forEach { edxId ->
            val edxCourse = repo.getEdxCourse(edxId).getOrNull() ?: run {
                failureStatement += "Failure adding $edxId\n"
                return@forEach
            }
            val courseId = repo.getCourseByEdxId(edxId)?.id ?: run {
                val course = Course(
                    id = UUID.randomUUID(),
                    edxId = edxId,
                    title = edxCourse.name,
                    number = edxCourse.number,
                    org = edxCourse.org,
                    createdAt = currentTime,
                    lastModifiedAt = currentTime,
                )
                repo.createCourse(course)
                course.id
            }
            repo.linkCourse(courseId, userId, currentTime)

        }
        return Unit.toResult()
    }

    private suspend fun updateCourse(edxCourse: EdxCourse, course: Course) {
        withContext(Dispatchers.IO) {
            launch {
                val courseToUpdate = course.copy(
                    title = edxCourse.name,
                    number = edxCourse.number,
                    org = edxCourse.org,
                )
                repo.updateCourse(courseToUpdate)
            }
        }
    }

    override suspend fun getCourse(request: GetCourseRequest): Result<GetCourseResponse> {
        val courseId = request.id.toUuid() ?: return failure("Invalid Course Id")
        val course = repo.getCourse(courseId) ?: return failure("Course Not Found")
        val assignments = repo.getAssignmentsForCourse(course.id)
        val studentResponse = repo.getStudentsForCourse(course.edxId)
        val students = studentResponse.getOrNull()?.results ?: return failure(
            "Could not " +
                    "retrieve student info for the course"
        )
        val responseAssignments = assignments.map { assignment ->
            val submissions = repo.getLastStudentSubmissionForAssignment(assignment.id)
            val successes = submissions.filter { it.correct }
            val successRate = successes.size.toDouble() / submissions.size.toDouble()
            val successPercent = successRate * 100

            val completionRate = submissions.size.toDouble() / students.size.toDouble()
            val completionPercent = completionRate * 100
            GetCourseResponse.Assignment(
                id = assignment.id.toString(),
                title = assignment.title,
                successRate = "${successPercent.toInt()}%",
                completionRate = "${completionPercent.toInt()}%",
                lastModified = assignment.lastModifiedAt.monthDayYear(),
            )
        }
        return GetCourseResponse(
            id = course.id.toString(),
            name = course.title,
            referenceId = course.edxId,
            assignments = responseAssignments,
        ).toResult()
    }
}
