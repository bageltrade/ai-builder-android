package com.example

import com.example.data.api.ChatChoice
import com.example.data.api.ChatCompletionRequest
import com.example.data.api.ChatCompletionResponse
import com.example.data.api.ChatMessage
import com.example.data.api.ChatResponseMessage
import com.example.data.api.ChatUsage
import com.example.data.api.Content
import com.example.data.api.GeminiRequest
import com.example.data.api.GeminiResponse
import com.example.data.api.GenerationConfig
import com.example.data.api.Part
import com.example.data.api.ThinkingConfig
import com.example.data.model.ModelProvider
import com.example.data.model.SupportedModels
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelIntegrationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `test supported models configuration`() {
        val nemotron = SupportedModels.NEMOTRON_3_SUPER
        assertEquals("nvidia/nemotron-3-super-120b-a12b", nemotron.id)
        assertEquals(ModelProvider.NVIDIA_NIM, nemotron.provider)
        assertEquals("https://integrate.api.nvidia.com/v1", nemotron.defaultBaseUrl)
        assertTrue(nemotron.name.contains("Nemotron 3 Super"))

        val gemini = SupportedModels.GEMINI_3_1_PRO
        assertEquals("gemini-3.1-pro-preview", gemini.id)
        assertEquals(ModelProvider.GOOGLE_GEMINI, gemini.provider)

        val llama = SupportedModels.LLAMA_3_3_70B
        assertEquals("meta/llama-3.3-70b-instruct", llama.id)
        assertEquals(ModelProvider.NVIDIA_NIM, llama.provider)

        assertEquals(3, SupportedModels.allModels.size)

        // Test lookup with fallback
        assertEquals(nemotron, SupportedModels.findById("nvidia/nemotron-3-super-120b-a12b"))
        assertEquals(gemini, SupportedModels.findById("gemini-3.1-pro-preview"))
        assertEquals(nemotron, SupportedModels.findById("unknown-model-fallback"))
    }

    @Test
    fun `test OpenAi ChatCompletionRequest serialization`() {
        val adapter = moshi.adapter(ChatCompletionRequest::class.java)
        val request = ChatCompletionRequest(
            model = SupportedModels.DEFAULT_MODEL_ID,
            messages = listOf(
                ChatMessage(role = "system", content = "You are an expert Puter.js app builder."),
                ChatMessage(role = "user", content = "Build an offline Pomodoro Matrix app.")
            ),
            temperature = 0.6f,
            maxTokens = 4096
        )

        val json = adapter.toJson(request)
        assertTrue(json.contains("nvidia/nemotron-3-super-120b-a12b"))
        assertTrue(json.contains("max_tokens"))
        assertTrue(json.contains("Pomodoro Matrix"))

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals(request.model, parsed?.model)
        assertEquals(2, parsed?.messages?.size)
        assertEquals(4096, parsed?.maxTokens)
    }

    @Test
    fun `test OpenAi ChatCompletionResponse deserialization with reasoning content`() {
        val adapter = moshi.adapter(ChatCompletionResponse::class.java)
        val sampleJson = """
            {
              "id": "chatcmpl-nv12345",
              "model": "nvidia/nemotron-3-super-120b-a12b",
              "choices": [
                {
                  "index": 0,
                  "message": {
                    "role": "assistant",
                    "content": "=== THINKING ===\nArchitecting layout\n=== FILE: index.html ===\n<!DOCTYPE html><html><body><h1>Pong</h1></body></html>",
                    "reasoning_content": "Deep reasoning step: Need to calculate paddle collision vectors."
                  },
                  "finish_reason": "stop"
                }
              ],
              "usage": {
                "prompt_tokens": 120,
                "completion_tokens": 850,
                "total_tokens": 970
              }
            }
        """.trimIndent()

        val response = adapter.fromJson(sampleJson)
        assertNotNull(response)
        val choice = response?.choices?.firstOrNull()
        assertNotNull(choice)
        assertEquals("stop", choice?.finishReason)
        assertEquals("Deep reasoning step: Need to calculate paddle collision vectors.", choice?.message?.reasoningContent)
        assertTrue(choice?.message?.content?.contains("=== FILE: index.html ===") == true)
        assertEquals(970, response?.usage?.totalTokens)
    }

    @Test
    fun `test Gemini request serialization with thinkingConfig`() {
        val adapter = moshi.adapter(GeminiRequest::class.java)
        val request = GeminiRequest(
            contents = listOf(
                Content(role = "user", parts = listOf(Part(text = "Create a Retro Synth Drum App")))
            ),
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                temperature = 0.7f
            ),
            systemInstruction = Content(parts = listOf(Part(text = "You are Puter AI Builder")))
        )

        val json = adapter.toJson(request)
        assertTrue(json.contains("HIGH"))
        assertTrue(json.contains("Retro Synth Drum App"))

        val parsed = adapter.fromJson(json)
        assertNotNull(parsed)
        assertEquals("HIGH", parsed?.generationConfig?.thinkingConfig?.thinkingLevel)
    }
}
