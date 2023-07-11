package org.empowrco.coppin.courses.presenters

data class GetCoursesRequest(val id: String)
data class GetCourseRequest(val id: String)
data class LinkCoursesRequest(val classIds: List<String>, val userId: String)
data class LinkCourse(val id: String)
