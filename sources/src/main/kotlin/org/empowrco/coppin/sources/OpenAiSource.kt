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
    suspend fun rawPrompt(query: String, user: String): AiResponse
    suspend fun isEnabled(): Boolean
}

internal class RealOpenAiSource(private val settingsSource: SettingsSource) : OpenAiSource {

    override suspend fun rawPrompt(query: String, user: String): AiResponse {
        val aiNotEnabledResponse = AiResponse(
            response = null,
            stopReason = "Ai is not enabled for your Coppin account. Please contact your administrator",
            promptTokens = 0,
            responseTokens = 0,
        )
        val aiSettings = settingsSource.getAiSettings() ?: return aiNotEnabledResponse
        if (!isEnabled()) {
            return aiNotEnabledResponse
        }
        val config = OpenAIConfig(
            token = aiSettings.key,
            timeout = Timeout(socket = 60.seconds),
            organization = aiSettings.orgKey,
        )
        val openAI = OpenAI(config)
        val model = ModelId(aiSettings.model)
        val completionRequest = ChatCompletionRequest(
            model = model,
            messages = listOf(
                ChatMessage(
                    content = query,
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
            stopReason = completion.choices.firstOrNull()?.finishReason?.value
        )
    }


    @OptIn(BetaOpenAI::class)
    override suspend fun prompt(query: String, user: String): AiResponse {
        val aiNotEnabledResponse = AiResponse(
            response = null,
            stopReason = "Ai is not enabled for your Coppin account. Please contact your administrator",
            promptTokens = 0,
            responseTokens = 0,
        )
        val aiSettings = settingsSource.getAiSettings() ?: return aiNotEnabledResponse
        if (!isEnabled()) {
            return aiNotEnabledResponse
        }
        val config = OpenAIConfig(
            token = aiSettings.key,
            timeout = Timeout(socket = 60.seconds),
            organization = aiSettings.orgKey,
        )
        val openAI = OpenAI(config)
        val model = ModelId(aiSettings.model)
        val promptRestriction = ChatMessage(
            content = aiSettings.prePrompt,
            role = ChatRole.System,
        )

        val completionRequest = ChatCompletionRequest(
            model = model,
            messages = listOf(
                promptRestriction,
                promptRestriction.copy(role = ChatRole.User),
                ChatMessage(
                    content = query,
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
            stopReason = completion.choices.firstOrNull()?.finishReason?.value
        )
    }

    override suspend fun isEnabled(): Boolean {
        val aiSettings = settingsSource.getAiSettings() ?: return false
        return aiSettings.key.isNotBlank() && aiSettings.orgKey.isNotBlank() && aiSettings.model.isNotBlank()
    }
}
