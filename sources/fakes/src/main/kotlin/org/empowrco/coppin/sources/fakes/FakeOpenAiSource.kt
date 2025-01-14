package org.empowrco.coppin.sources.fakes

import org.empowrco.coppin.models.responses.AiResponse
import org.empowrco.coppin.sources.OpenAiSource

class FakeOpenAiSource : OpenAiSource {
    val aiResponses = mutableListOf<AiResponse>()
    var isEnabled = false

    override suspend fun prompt(
        query: String,
        user: String,
    ): AiResponse {
        return aiResponses.first()
    }

    override suspend fun rawPrompt(
        query: String,
        user: String,
    ): AiResponse {
        return aiResponses.first()
    }

    override suspend fun isEnabled(): Boolean {
        return isEnabled
    }
}