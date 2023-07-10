package org.empowrco.coppin.courses.presenters

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.empowrco.coppin.courses.backend.CoursesPortalRepository
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid

interface CoursesPortalPresenter {
    suspend fun getCourses(): Result<GetCoursesResponse>
    suspend fun getCourse(request: GetCourseRequest): Result<GetCourseResponse>
    suspend fun getUnlinkedCourses(): Result<GetCoursesResponse>
}

internal class RealCoursesPortalPresenter(
    private val repo: CoursesPortalRepository,
) : CoursesPortalPresenter {
    override suspend fun getCourses(): Result<GetCoursesResponse> {
        val courses = repo.getCourses()
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
                    endDate = edxCourse.end ?: "",
                    startDate = edxCourse.start,
                    name = edxCourse.name,
                    number = edxCourse.number,
                )
            },
            coursesCount = courses.size,
        ).toResult()
    }

    override suspend fun getUnlinkedCourses(): Result<GetCoursesResponse> {
        val courses = repo.getCourses()
        val edxCoursesResult = repo.getEdxCourses()
        if (edxCoursesResult.isFailure) {
            return Result.failure(edxCoursesResult.exceptionOrNull()!!)
        }
        val edxCourses = edxCoursesResult.getOrThrow().results // Should never throw
        return GetCoursesResponse(
            courses = edxCourses.mapNotNull { edxCourse ->
                val course = courses.find { it.edxId == edxCourse.id }
                if (course != null) {
                    return@mapNotNull null
                }
                GetCoursesResponse.Course(
                    id = edxCourse.id,
                    endDate = edxCourse.end ?: "",
                    startDate = edxCourse.start,
                    name = edxCourse.name,
                    number = edxCourse.number,
                )
            },
            coursesCount = courses.size,
        ).toResult()
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
                lastModified = assignment.lastModifiedAt.toString()
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
