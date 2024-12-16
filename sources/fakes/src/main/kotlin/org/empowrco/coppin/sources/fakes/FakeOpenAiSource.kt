package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.responses.AiResponse
import org.empowrco.coppin.sources.OpenAiSource

class FakeOpenAiSource : OpenAiSource {
    override suspend fun prompt(
        query: String,
        user: String,
    ): AiResponse {
        TODO("Not yet implemented")
    }

    override suspend fun rawPrompt(
        query: String,
        user: String,
    ): AiResponse {
        TODO("Not yet implemented")
    }

    override suspend fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }
}