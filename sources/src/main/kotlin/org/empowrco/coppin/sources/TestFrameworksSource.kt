package org.empowrco.coppin.sources

import org.empowrco.coppin.models.Language

interface TestFrameworksSource {

    suspend fun getTestFrameworks(): List<Language.TestFramework>
}