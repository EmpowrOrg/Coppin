package org.empowrco.coppin.assignment

import org.empowrco.coppin.assignment.backend.assignmentBackendModule
import org.empowrco.coppin.assignment.presenters.assignmentPresentersModule

val assignmentModule = listOf(assignmentPresentersModule, assignmentBackendModule)
