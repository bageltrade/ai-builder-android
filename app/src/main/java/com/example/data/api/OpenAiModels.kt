package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float? = 0.6f,
    @param:Json(name = "max_tokens")
    val maxTokens: Int? = 4096
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val id: String? = null,
    val model: String? = null,
    val choices: List<ChatChoice>? = null,
    val usage: ChatUsage? = null
)

@JsonClass(generateAdapter = true)
data class ChatChoice(
    val index: Int? = null,
    val message: ChatResponseMessage? = null,
    @param:Json(name = "finish_reason")
    val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatResponseMessage(
    val role: String? = null,
    val content: String? = null,
    @param:Json(name = "reasoning_content")
    val reasoningContent: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatUsage(
    @param:Json(name = "prompt_tokens")
    val promptTokens: Int? = null,
    @param:Json(name = "completion_tokens")
    val completionTokens: Int? = null,
    @param:Json(name = "total_tokens")
    val totalTokens: Int? = null
)
