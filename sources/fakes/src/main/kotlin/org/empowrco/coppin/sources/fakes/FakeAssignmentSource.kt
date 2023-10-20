package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.Assignment
import org.empowrco.coppin.sources.AssignmentSource
import java.util.UUID

class FakeAssignmentSource : AssignmentSource {

    val assignments = mutableListOf<Assignment>()

    override suspend fun getAssignment(id: UUID): Assignment? {
        return assignments.find { it.id == id }
    }

    override suspend fun assignmentsWithReferenceStartingWithCount(name: String): Long {
        return assignments.filter { it.referenceId.startsWith(name) }.size.toLong()
    }

    override suspend fun getAssignmentCountBySubject(id: UUID): Long {
        return assignments.filter { it.subject.id == id }.size.toLong()
    }

    override suspend fun getAssignmentsForCourse(id: UUID): List<Assignment> {
        return assignments.filter { it.courseId == id }
    }

    override suspend fun getAssignmentsForSubject(id: UUID): List<Assignment> {
        return assignments.filter { it.subject.id == id }
    }

    override suspend fun getAssignmentByReferenceId(id: String): Assignment? {
        return assignments.find { it.referenceId == id }
    }

    override suspend fun createAssignment(assignment: Assignment) {
        assignments.add(assignment)
    }

    override suspend fun deleteAssignment(assignment: Assignment): Boolean {
        return assignments.removeIf { it.id == assignment.id }
    }

    override suspend fun updateAssignment(assignment: Assignment): Boolean {
        val result = assignments.removeIf {
            assignment.id == it.id
        }
        if (result) {
            assignments.add(assignment)
        }
        return result
    }

}
