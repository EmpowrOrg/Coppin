package org.empowrco.coppin.assignment.backend

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.models.AssignmentCode
import org.empowrco.coppin.models.Course
import org.empowrco.coppin.models.Language
import org.empowrco.coppin.models.Subject
import org.empowrco.coppin.sources.AssignmentCodesSource
import org.empowrco.coppin.sources.AssignmentSource
import org.empowrco.coppin.sources.CoursesSource
import org.empowrco.coppin.sources.LanguagesSource
import org.empowrco.coppin.sources.SubjectSource
import java.util.UUID


interface AssignmentPortalRepository {
    suspend fun getAssignments(): List<Assignment>
    suspend fun getAssignment(id: UUID): Assignment?
    suspend fun getSubject(id: UUID): Subject?
    suspend fun getSubjectsForCourse(id: UUID): List<Subject>
    suspend fun createAssignment(assignment: Assignment)
    suspend fun getLanguage(mime: String): Language?
    suspend fun getCode(id: UUID): AssignmentCode?
    suspend fun updateCode(code: AssignmentCode): Boolean
    suspend fun saveCode(code: AssignmentCode)
    suspend fun updateAssignment(assignment: Assignment): Boolean
    suspend fun getLanguages(): List<Language>
    suspend fun deleteCode(id: UUID)
    suspend fun deprimaryAssignmentCodes(assignmentId: UUID)
    suspend fun getAssignmentCodes(id: UUID): List<AssignmentCode>
    suspend fun getCourse(id: UUID): Course?
}

internal class RealAssignmentPortalRepository(
    private val assignmentSource: AssignmentSource,
    private val languagesSource: LanguagesSource,
    private val codesSource: AssignmentCodesSource,
    private val coursesSource: CoursesSource,
    private val subjectSource: SubjectSource,
) : AssignmentPortalRepository {

    override suspend fun getAssignments(): List<Assignment> {
        return assignmentSource.getAssignments()
    }

    override suspend fun getSubject(id: UUID): Subject? {
        return subjectSource.getSubject(id)
    }

    override suspend fun getAssignmentCodes(id: UUID): List<AssignmentCode> {
        return codesSource.getByAssigment(id)
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        return assignmentSource.updateAssignment(assignment)
    }

    override suspend fun getLanguages(): List<Language> {
        return languagesSource.getLanguages()
    }

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignmentSource.getAssignment(id)
    }

    override suspend fun getCode(id: UUID): AssignmentCode? {
        return codesSource.get(id)
    }

    override suspend fun getLanguage(mime: String): Language? {
        return languagesSource.getLanguageByMime(mime)
    }

    override suspend fun createAssignment(assignment: Assignment) {
        return assignmentSource.createAssignment(assignment)
    }


    override suspend fun updateCode(code: AssignmentCode): Boolean {
        return codesSource.update(code)
    }

    override suspend fun saveCode(code: AssignmentCode) {
        codesSource.create(listOf(code))
    }

    override suspend fun deprimaryAssignmentCodes(assignmentId: UUID) {
        codesSource.deprimaryAssignmentCodes(assignmentId)
    }

    override suspend fun deleteCode(id: UUID) {
        codesSource.delete(id)
    }

    override suspend fun getCourse(id: UUID): Course? {
        return coursesSource.getCourse(id)
    }

    override suspend fun getSubjectsForCourse(id: UUID): List<Subject> {
        return subjectSource.getSubjectsForCourse(id)
    }
}
