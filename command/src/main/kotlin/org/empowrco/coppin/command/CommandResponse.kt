package org.empowrco.coppin.command

sealed class CommandResponse(open val output: String) {
    data class Error(override val output: String): CommandResponse(output)
    data class Success(override val output: String): CommandResponse(output)
}
