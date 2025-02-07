package org.empowrco.coppin.courses.presenters

import kotlinx.serialization.Serializable

data class GetCoursesRequest(val email: String)
data class GetCourseRequest(val id: String)
data class LinkCoursesRequest(val classIds: List<String>, val email: String)
data class LinkCourse(val id: String)

@Serializable
data class CreateSubjectRequest(val courseId: String, val name: String)
data class UpdateSubjectRequest(val id: String, val name: String)
data class GetSubjectRequest(val id: String?, val courseId: String)

@Serializable
data class DeleteSubjectRequest(val id: String)

@Serializable
data class CreateSectionRequest(val subjectId: String, val name: String, val order: Int)
data class UpdateSectionRequest(val id: String, val name: String, val order: Int)
@Serializable
data class DeleteSectionRequest(val id: String)
data class GetSectionRequest(val id: String)
