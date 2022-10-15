package org.empowrco.coppin.command.fakes

import org.empowrco.coppin.command.CommandResponse
import org.empowrco.coppin.command.Commander

class FakeCommander: Commander {
    val responses = ArrayDeque<CommandResponse>()
    override suspend fun execute(command: String): CommandResponse {
        return responses.removeLast()
    }
}
