package org.empowrco.coppin.command

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


class RealCommanderTest {

    private val commander = RealCommander()

    @Test
    fun execute() = runBlocking {
        val response = commander.execute("echo Hello, World")
        assertEquals(response, CommandResponse.Success("Hello, World"))
    }

    @Test
    fun testInvalidCommand() = runBlocking {
        val response = commander.execute("ec ho Hello, World")
        assertEquals(response, CommandResponse.Error("Cannot run program \"ec\": error=2, No such file or directory"))
    }
}
