package org.empowrco.coppin.courses.presenters.api

import org.empowrco.coppin.courses.backend.CoursesApiRepository
import org.empowrco.coppin.utils.toUuid

interface CoursesApiPresenter {
    suspend fun getGrades(request: GetStudentAssignmentsRequest): GetStudentAssignmentsResponse
}

internal class RealCoursesApiPresenter(
    private val repo: CoursesApiRepository,
) : CoursesApiPresenter {
    override suspend fun getGrades(request: GetStudentAssignmentsRequest): GetStudentAssignmentsResponse {
        val courseId = request.courseId.toUuid() ?: throw Exception("Invalid course id")
        if (!repo.doesCourseExist(courseId)) {
            throw Exception("Course not found")
        }
        if (request.studentId.isBlank()) {
            throw Exception("Student ID is missing")
        }
        val submissions = repo.getLatestSubmissionForEachAssignment(courseId, request.studentId)
        return GetStudentAssignmentsResponse(
            assignments = submissions.mapNotNull {
                val language = repo.getLanguage(it.languageId) ?: return@mapNotNull null
                GetStudentAssignmentsResponse.Assignment(
                    score = if (it.correct) 100 else 0,
                    assignmentId = it.assignmentId.toString(),
                    feedback = it.feedback,
                    code = it.code,
                    language = GetStudentAssignmentsResponse.Assignment.Language(
                        mime = language.mime,
                        name = language.name,
                    ),
                    attempts = it.attempt,
                )
            }
        )
    }
}
