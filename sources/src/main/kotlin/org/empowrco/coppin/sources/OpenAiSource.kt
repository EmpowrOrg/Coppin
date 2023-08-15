package org.empowrco.coppin.sources

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import org.empowrco.coppin.models.responses.AiResponse
import kotlin.time.Duration.Companion.seconds

interface OpenAiSource {
    suspend fun prompt(query: String, user: String): AiResponse
}

internal class RealOpenAiSource : OpenAiSource {
    private val config = OpenAIConfig(
        token = System.getenv("OPEN_AI_KEY") ?: "",
        timeout = Timeout(socket = 60.seconds),
        organization = System.getenv("OPEN_AI_ORG_KEY") ?: "",
    )
    private val openAI = OpenAI(config)
    private val model = ModelId(System.getenv("OPEN_AI_MODEL") ?: "")

    @OptIn(BetaOpenAI::class)
    override suspend fun prompt(query: String, user: String): AiResponse {
        val prePrompt =
            "Please ensure that the instructions are written in Markdown language. Do not return any text other than the instructions. Use syntax highlighting with code blocks. "
        val promptRestriction = ChatMessage(
            content = prePrompt,
            role = ChatRole.System,
        )

        val completionRequest = ChatCompletionRequest(
            model = model,
            messages = listOf(
                promptRestriction,
                promptRestriction.copy(role = ChatRole.User),
                ChatMessage(
                    content = "$prePrompt. Now answer the following query: $query",
                    role = ChatRole.User,
                    name = user,
                )
            ),
            topP = 0.1,
            frequencyPenalty = -0.0,
            n = 1,
            user = user,
        )
        val completion = openAI.chatCompletion(completionRequest)
        return AiResponse(
            response = completion.choices.firstOrNull()?.message?.content,
            promptTokens = completion.usage?.promptTokens ?: 0,
            responseTokens = completion.usage?.completionTokens ?: 0,
            stopReason = completion.choices.firstOrNull()?.finishReason
        )
    }
}
