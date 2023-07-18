package org.empowrco.coppin.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class EdxCoursesResponse(val results: List<EdxCourse>)
