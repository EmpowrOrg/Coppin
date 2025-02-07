package org.empowrco.coppin.courses.presenters

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.empowrco.coppin.courses.backend.CoursesPortalRepository
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Section
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.models.responses.EdxCourse
import org.empowrco.coppin.utils.DuplicateKeyException
import org.empowrco.coppin.utils.failure
import org.empowrco.coppin.utils.monthDayYear
import org.empowrco.coppin.utils.now
import org.empowrco.coppin.utils.toResult
import org.empowrco.coppin.utils.toUuid
import java.util.UUID

interface CoursesPortalPresenter {
    suspend fun getCourses(request: GetCoursesRequest): Result<GetCoursesResponse>
    suspend fun getCourse(request: GetCourseRequest): Result<GetCourseResponse>
    suspend fun getManageCourses(request: GetCoursesRequest): Result<GetManagedCoursesResponse>
    suspend fun linkCourses(request: LinkCoursesRequest): Result<Unit>
    suspend fun createSubject(request: CreateSubjectRequest): Result<CreateSubjectResponse>
    suspend fun getSubject(request: GetSubjectRequest): Result<GetSubjectResponse>
    suspend fun updateSubject(request: UpdateSubjectRequest): Result<UpdateSubjectResponse>
    suspend fun deleteSubject(request: DeleteSubjectRequest): Result<DeleteSubjectResponse>
    suspend fun createSection(request: CreateSectionRequest)
    suspend fun updateSection(request: UpdateSectionRequest)
    suspend fun deleteSection(request: DeleteSectionRequest)
    suspend fun getSection(request: GetSectionRequest)
}

internal class RealCoursesPortalPresenter(
    private val repo: CoursesPortalRepository,
) : CoursesPortalPresenter {
    override suspend fun getCourses(request: GetCoursesRequest): Result<GetCoursesResponse> {
        val user = repo.getUserByEmail(request.email) ?: return failure("Unauthorized user")
        val courses = repo.getLinkedCourses(user.id)
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

    override suspend fun getManageCourses(request: GetCoursesRequest): Result<GetManagedCoursesResponse> {
        val user = repo.getUserByEmail(request.email) ?: return failure("Unauthorized user")
        val linkedCourses = repo.getLinkedCourses(user.id)
        val edxCoursesResult =
            repo.getEdxCourses().getOrNull()?.results ?: return failure("Could not fetch edx courses")
        val edxCourses = if (edxCoursesResult.size <= 3) {
            listOf(edxCoursesResult)
        } else {
            edxCoursesResult.windowed(size = 3, partialWindows = true)
        }

        return GetManagedCoursesResponse(
            rows = edxCourses.map { courseRow ->
                val one = GetManagedCoursesResponse.Course(
                    name = courseRow[0].name,
                    id = courseRow[0].id,
                    dates = courseRow[0].dates(),
                    number = courseRow[0].number,
                    org = courseRow[0].org,
                    selected = linkedCourses.any { it.edxId == courseRow[0].id },
                )
                val two = courseRow.getOrNull(1)?.let {
                    GetManagedCoursesResponse.Course(
                        name = it.name,
                        id = it.id,
                        dates = it.dates(),
                        number = it.number,
                        org = courseRow[1].org,
                        selected = linkedCourses.any { it.edxId == courseRow[1].id },
                    )
                }
                val three = courseRow.getOrNull(2)?.let {
                    GetManagedCoursesResponse.Course(
                        name = it.name,
                        id = it.id,
                        dates = it.dates(),
                        number = it.number,
                        org = courseRow[2].org,
                        selected = linkedCourses.any { it.edxId == courseRow[2].id },
                    )
                }
                GetManagedCoursesResponse.CourseRow(one, two, three)
            },
            count = edxCoursesResult.size,
        ).toResult()
    }

    private fun EdxCourse.dates(): String {
        val start = start.monthDayYear()
        val endRange = end?.monthDayYear() ?: "Present"
        return "$start - $endRange"
    }

    override suspend fun linkCourses(request: LinkCoursesRequest): Result<Unit> {
        val user = repo.getUserByEmail(request.email) ?: return failure("Unauthorized user")
        var failureStatement = ""
        val currentTime = LocalDateTime.now()
        val linkedCourses = mutableListOf<UUID>()
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
            try {
                repo.linkCourse(courseId, user.id, currentTime)
                linkedCourses.add(courseId)
            } catch (ex: DuplicateKeyException) {
                // Course is already linked
                linkedCourses.add(courseId)
            } catch (ex: Exception) {
                failureStatement += "Failure adding $edxId\n"
            }
        }
        repo.unlinkCoursesNotIn(linkedCourses, user.id)
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

    override suspend fun createSubject(request: CreateSubjectRequest): Result<CreateSubjectResponse> {
        if (request.name.isBlank()) {
            return failure("Name must not be blank")
        }
        val courseId = request.courseId.toUuid() ?: return failure("Invalid course id")
        repo.getCourse(courseId) ?: return failure("Course Not Found")
        val currentTime = LocalDateTime.now()
        val subject = Subject(
            id = UUID.randomUUID(),
            courseId = courseId,
            name = request.name,
            createdAt = currentTime,
            sections = emptyList(),
            lastModifiedAt = currentTime,
        )
        return try {
            repo.createSubject(subject)
            CreateSubjectResponse.toResult()
        } catch (ex: Exception) {
            failure(ex.localizedMessage)
        }
    }

    override suspend fun getSubject(request: GetSubjectRequest): Result<GetSubjectResponse> {
        val courseId = request.courseId.toUuid() ?: return failure("Invalid course id")
        val course = repo.getCourse(courseId) ?: return failure("Course not found")
        val subjectId = request.id?.toUuid() ?: return failure("Invalid subject id")
        val subject = repo.getSubject(subjectId) ?: return failure("Subject not found")
        val assignmentCount = repo.getAssignmentCountBySubject(subjectId)
        return GetSubjectResponse(
            courseId = courseId.toString(),
            id = subjectId.toString(),
            name = subject.name,
            canBeDeleted = assignmentCount == 0L,
            courseName = course.title,
        ).toResult()
    }

    override suspend fun updateSubject(request: UpdateSubjectRequest): Result<UpdateSubjectResponse> {
        if (request.name.isBlank()) {
            return failure("Name must not be blank")
        }
        val subjectId = request.id.toUuid() ?: return failure("Invalid subject id")
        val subject = repo.getSubject(subjectId) ?: return failure("Subject not found")
        val updatedSubject = subject.copy(
            name = request.name,
            lastModifiedAt = LocalDateTime.now(),
        )
        val result = repo.updateSubject(updatedSubject)
        return if (!result) {
            failure("Error updating subject")
        } else {
            UpdateSubjectResponse.toResult()
        }
    }

    override suspend fun deleteSubject(request: DeleteSubjectRequest): Result<DeleteSubjectResponse> {
        val id = request.id.toUuid() ?: return failure("Invalid subject id")
        val subject = repo.getSubject(id) ?: return failure("No subject found")
        val result = repo.deleteSubject(subject)
        return if (!result) {
            failure("Unknown error")
        } else {
            DeleteSubjectResponse.toResult()
        }
    }

    override suspend fun getCourse(request: GetCourseRequest): Result<GetCourseResponse> {
        val courseId = request.id.toUuid() ?: return failure("Invalid Course Id")
        val course = repo.getCourse(courseId) ?: return failure("Course Not Found")
        val assignments = repo.getAssignmentsForCourse(course.id)
        val studentResponse = repo.getStudentsForCourse(course.edxId)
        val students = studentResponse.getOrNull()?.results ?: return failure(
            studentResponse.exceptionOrNull()?.localizedMessage ?: "Unknown error retrieving students"
        )
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
        ).toResult()
    }

    override suspend fun createSection(request: CreateSectionRequest) {
        val subjectId = request.subjectId.toUuid() ?: return
        repo.getSubject(subjectId) ?: return
        val currentTime = LocalDateTime.now()
        val section = Section(
            id = UUID.randomUUID(),
            subjectId = subjectId,
            name = request.name,
            createdAt = currentTime,
            order = request.order,
            lastModifiedAt = currentTime,
        )
        repo.createSection(section)
    }

    override suspend fun updateSection(request: UpdateSectionRequest) {
        val sectionId = request.id.toUuid() ?: return
        val section = repo.getSection(sectionId) ?: return
        val updatedSection = section.copy(
            name = request.name,
            order = request.order,
            lastModifiedAt = LocalDateTime.now(),
        )
        repo.updateSection(updatedSection)
    }

    override suspend fun deleteSection(request: DeleteSectionRequest) {
        val sectionId = request.id.toUuid() ?: return
        val deleted = repo.deleteSection(sectionId)
    }

    override suspend fun getSection(request: GetSectionRequest) {
        val sectionId = request.id.toUuid() ?: return
        val section = repo.getSection(sectionId) ?: return
    }
}
