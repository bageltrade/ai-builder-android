package com.example.data.model

enum class ModelProvider(val title: String) {
    NVIDIA_NIM("NVIDIA NIM"),
    GOOGLE_GEMINI("Google Gemini")
}

data class AiModel(
    val id: String,
    val name: String,
    val provider: ModelProvider,
    val description: String,
    val defaultBaseUrl: String = "",
    val badge: String = ""
)

object SupportedModels {
    val NEMOTRON_3_SUPER = AiModel(
        id = "nvidia/nemotron-3-super-120b-a12b",
        name = "Nemotron 3 Super (120B)",
        provider = ModelProvider.NVIDIA_NIM,
        description = "NVIDIA's premier 120B reasoning & code model with architecture planning.",
        defaultBaseUrl = "https://integrate.api.nvidia.com/v1",
        badge = "120B NIM"
    )

    val GEMINI_3_1_PRO = AiModel(
        id = "gemini-3.1-pro-preview",
        name = "Gemini 3.1 Pro",
        provider = ModelProvider.GOOGLE_GEMINI,
        description = "Google's state-of-the-art multimodal model with High Thinking enabled.",
        defaultBaseUrl = "https://generativelanguage.googleapis.com/",
        badge = "HIGH THINKING"
    )

    val LLAMA_3_3_70B = AiModel(
        id = "meta/llama-3.3-70b-instruct",
        name = "Llama 3.3 70B",
        provider = ModelProvider.NVIDIA_NIM,
        description = "Meta Llama 3.3 70B Instruct accelerated on NVIDIA NIM platform.",
        defaultBaseUrl = "https://integrate.api.nvidia.com/v1",
        badge = "70B NIM"
    )

    val allModels = listOf(NEMOTRON_3_SUPER, GEMINI_3_1_PRO, LLAMA_3_3_70B)

    const val DEFAULT_MODEL_ID = "nvidia/nemotron-3-super-120b-a12b"
    const val DEFAULT_NVIDIA_API_KEY = "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"
    const val DEFAULT_NVIDIA_BASE_URL = "https://integrate.api.nvidia.com/v1"

    fun findById(id: String): AiModel {
        return allModels.find { it.id == id } ?: NEMOTRON_3_SUPER
    }
}
