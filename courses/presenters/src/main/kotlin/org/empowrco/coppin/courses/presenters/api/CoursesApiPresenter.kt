package org.empowrco.coppin.courses.presenters.api

import org.empowrco.coppin.courses.backend.CoursesApiRepository
import org.empowrco.coppin.courses.presenters.GetCourseResponse
import org.empowrco.coppin.utils.monthDayYear
import org.empowrco.coppin.utils.toUuid

interface CoursesApiPresenter {
    suspend fun getGrades(request: GetStudentAssignmentsRequest): GetStudentAssignmentsResponse
    suspend fun getCourse(request: GetCourseRequest): GetCourseResponse
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
                val assignment = repo.getAssignment(it.assignmentId) ?: return@mapNotNull null
                GetStudentAssignmentsResponse.Assignment(
                    score = if (it.correct) 100 else 0,
                    id = it.assignmentId.toString(),
                    feedback = it.feedback,
                    code = it.code,
                    language = GetStudentAssignmentsResponse.Assignment.Language(
                        mime = language.mime,
                        name = language.name,
                    ),
                    attempts = it.attempt,
                    title = assignment.title,
                )
            }
        )
    }

    override suspend fun getCourse(request: GetCourseRequest): GetCourseResponse {
        val courseId = request.id.toUuid() ?: return throw Exception("Invalid Course Id")
        val course = repo.getCourse(courseId) ?: return throw Exception("Course Not Found")
        val assignments = repo.getAssignmentsForCourse(course.id)
        val studentResponse = repo.getStudentsForCourse(course.edxId)
        val students = studentResponse.getOrNull()?.results ?: throw studentResponse.exceptionOrNull()
            ?: Exception("Unknown error retrieving students")

        val subjects = repo.getSubjects(courseId)
        val subjectCompletionRates = mutableMapOf<String, List<Double>>()
        val subjectSuccessRates = mutableMapOf<String, List<Double>>()
        val responseAssignments = assignments.map { assignment ->
            val submissions = repo.getLatestStudentSubmissionsForAssignment(assignment.id)
            val successes = submissions.filter { it.correct }
            val successRate = successes.size.toDouble() / submissions.size.toDouble()
            val successPercent = successRate * 100
            val subSRates = subjectSuccessRates.getOrDefault(assignment.subject.name, emptyList()).toMutableList()
            subSRates.add(successRate)
            subjectSuccessRates[assignment.subject.name] = subSRates

            val completionRate = submissions.size.toDouble() / students.size.toDouble()
            val completionPercent = completionRate * 100
            val subCRates = subjectCompletionRates.getOrDefault(assignment.subject.name, emptyList()).toMutableList()
            subCRates.add(completionRate)
            subjectCompletionRates[assignment.subject.name] = subCRates
            GetCourseResponse.Assignment(
                id = assignment.id.toString(),
                title = assignment.title,
                successRate = "${successPercent.toInt()}%",
                completionRate = "${completionPercent.toInt()}%",
                subject = assignment.subject.name,
                lastModified = assignment.lastModifiedAt.monthDayYear(),
            )
        }
        val courseSubjects = subjects.map {
            val numOfAssignments = repo.getAssignmentCountBySubject(it.id)
            GetCourseResponse.Subject(
                id = it.id.toString(),
                name = it.name,
                assignments = numOfAssignments.toString(),
                lastModified = it.lastModifiedAt.monthDayYear(),
            )
        }
        subjects.forEach {
            if (!subjectCompletionRates.containsKey(it.name)) {
                subjectCompletionRates[it.name] = listOf(0.0)
            }
            if (!subjectSuccessRates.containsKey(it.name)) {
                subjectSuccessRates[it.name] = listOf(0.0)
            }
        }
        val subjectCompletionPercentages = subjectCompletionRates.entries.sortedBy { it.key }.map { (_, value) ->
            value.average() * 100
        }
        val completionAverage = subjectCompletionPercentages.average().toInt()
        return GetCourseResponse(
            id = course.id.toString(),
            name = course.title,
            referenceId = course.edxId,
            assignments = responseAssignments,
            subjects = courseSubjects,
            completionRate = completionAverage.toString(),
            chart = GetCourseResponse.Chart(
                title = "Overall Course Summary",
                y = GetCourseResponse.Chart.Y(
                    label = "Rate",
                    min = 0,
                    max = 100,
                ),
                x = GetCourseResponse.Chart.X(
                    labels = subjects.map { it.name },
                    lines = listOf(
                        GetCourseResponse.Chart.X.Line(
                            name = "Completion Rate",
                            points = subjectCompletionPercentages,
                            color = "#5D0E81",
                        ),
                        GetCourseResponse.Chart.X.Line(
                            name = "Success Rate",
                            points = subjectSuccessRates.entries.sortedBy { it.key }.map { (_, value) ->
                                value.average() * 100
                            },
                            color = "#FFD700",
                        ),
                    )
                )
            )
        )
    }
}
