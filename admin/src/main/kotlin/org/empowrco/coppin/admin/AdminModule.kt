package org.empowrco.coppin.admin

import org.empowrco.coppin.admin.backend.adminBackendModule
import org.empowrco.coppin.admin.presenters.adminPresenterModule

val adminModule = listOf(adminPresenterModule, adminBackendModule)
