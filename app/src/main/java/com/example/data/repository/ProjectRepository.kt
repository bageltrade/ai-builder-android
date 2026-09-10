package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.example.data.api.ChatMessage
import com.example.data.api.ChatCompletionRequest
import com.example.data.api.Content
import com.example.data.api.GeminiApiClient
import com.example.data.api.GeminiRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.OpenAiApiClient
import com.example.data.api.Part
import com.example.data.api.ThinkingConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectVersionEntity
import com.example.data.model.AiModel
import com.example.data.model.ModelProvider
import com.example.data.model.PuterTemplates
import com.example.data.model.SupportedModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class ProjectRepository(
    private val context: Context,
    projectDaoProvider: () -> ProjectDao = { AppDatabase.getDatabase(context).projectDao() }
) {
    private val projectDao: ProjectDao by lazy { projectDaoProvider() }
    private val prefs: SharedPreferences =
        context.getSharedPreferences("puter_builder_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_MODEL = "selected_ai_model_id"
        private const val KEY_NVIDIA_API_KEY = "custom_nvidia_api_key"
        private const val KEY_NVIDIA_BASE_URL = "custom_nvidia_base_url"
        private const val KEY_GEMINI_API_KEY = "custom_gemini_api_key"
    }

    fun getSelectedModelId(): String {
        return prefs.getString(KEY_SELECTED_MODEL, SupportedModels.DEFAULT_MODEL_ID)
            ?: SupportedModels.DEFAULT_MODEL_ID
    }

    fun setSelectedModelId(modelId: String) {
        prefs.edit().putString(KEY_SELECTED_MODEL, modelId.trim()).apply()
    }

    fun getNvidiaApiKey(): String {
        val stored = prefs.getString(KEY_NVIDIA_API_KEY, null)
        if (!stored.isNullOrBlank()) return stored
        return SupportedModels.DEFAULT_NVIDIA_API_KEY
    }

    fun saveNvidiaApiKey(key: String) {
        prefs.edit().putString(KEY_NVIDIA_API_KEY, key.trim()).apply()
    }

    fun getNvidiaBaseUrl(): String {
        val stored = prefs.getString(KEY_NVIDIA_BASE_URL, null)
        if (!stored.isNullOrBlank()) return stored
        return SupportedModels.DEFAULT_NVIDIA_BASE_URL
    }

    fun saveNvidiaBaseUrl(url: String) {
        prefs.edit().putString(KEY_NVIDIA_BASE_URL, url.trim()).apply()
    }

    fun getGeminiApiKey(): String {
        val stored = prefs.getString(KEY_GEMINI_API_KEY, null)
        if (!stored.isNullOrBlank()) return stored
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun saveGeminiApiKey(key: String) {
        prefs.edit().putString(KEY_GEMINI_API_KEY, key.trim()).apply()
    }

    // Backward compatibility alias
    fun getApiKey(): String = getGeminiApiKey()
    fun saveApiKey(key: String) = saveGeminiApiKey(key)

    fun getAllProjects(): Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProject(id: String): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    suspend fun getProjectDirect(id: String): ProjectEntity? = projectDao.getProjectDirect(id)

    fun getVersions(projectId: String): Flow<List<ProjectVersionEntity>> =
        projectDao.getVersionsForProject(projectId)

    suspend fun initializeDefaultProjectIfNeeded(): String = withContext(Dispatchers.IO) {
        val existing = projectDao.getProjectDirect("default_streakflow")
        if (existing != null) return@withContext existing.id

        val defaultTemplate = PuterTemplates.templates.first()
        val defaultProject = ProjectEntity(
            id = "default_streakflow",
            title = defaultTemplate.title,
            description = defaultTemplate.description,
            initialPrompt = defaultTemplate.prompt,
            htmlContent = defaultTemplate.html,
            cssContent = defaultTemplate.css,
            jsContent = defaultTemplate.js,
            manifestContent = defaultTemplate.manifest,
            thinkingLog = defaultTemplate.thinkingSummary,
            currentVersion = 1,
            updatedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )
        projectDao.insertOrUpdateProject(defaultProject)

        projectDao.insertVersion(
            ProjectVersionEntity(
                projectId = defaultProject.id,
                versionNumber = 1,
                prompt = defaultTemplate.prompt,
                htmlContent = defaultTemplate.html,
                cssContent = defaultTemplate.css,
                jsContent = defaultTemplate.js,
                manifestContent = defaultTemplate.manifest,
                thinkingSummary = defaultTemplate.thinkingSummary,
                timestamp = System.currentTimeMillis()
            )
        )

        defaultProject.id
    }

    suspend fun loadTemplate(templateId: String): String = withContext(Dispatchers.IO) {
        val template = PuterTemplates.templates.find { it.id == templateId }
            ?: PuterTemplates.templates.first()
        val newId = "proj_" + UUID.randomUUID().toString().take(8)
        val project = ProjectEntity(
            id = newId,
            title = template.title,
            description = template.description,
            initialPrompt = template.prompt,
            htmlContent = template.html,
            cssContent = template.css,
            jsContent = template.js,
            manifestContent = template.manifest,
            thinkingLog = template.thinkingSummary,
            currentVersion = 1,
            updatedAt = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )
        projectDao.insertOrUpdateProject(project)
        projectDao.insertVersion(
            ProjectVersionEntity(
                projectId = newId,
                versionNumber = 1,
                prompt = template.prompt,
                htmlContent = template.html,
                cssContent = template.css,
                jsContent = template.js,
                manifestContent = template.manifest,
                thinkingSummary = template.thinkingSummary,
                timestamp = System.currentTimeMillis()
            )
        )
        newId
    }

    suspend fun updateProjectFiles(
        projectId: String,
        html: String,
        css: String,
        js: String,
        manifest: String
    ) = withContext(Dispatchers.IO) {
        val current = projectDao.getProjectDirect(projectId) ?: return@withContext
        val updated = current.copy(
            htmlContent = html,
            cssContent = css,
            jsContent = js,
            manifestContent = manifest,
            updatedAt = System.currentTimeMillis()
        )
        projectDao.updateProject(updated)
    }

    suspend fun revertToVersion(projectId: String, versionNumber: Int) = withContext(Dispatchers.IO) {
        val targetVersion = projectDao.getVersion(projectId, versionNumber) ?: return@withContext
        val current = projectDao.getProjectDirect(projectId) ?: return@withContext
        val reverted = current.copy(
            htmlContent = targetVersion.htmlContent,
            cssContent = targetVersion.cssContent,
            jsContent = targetVersion.jsContent,
            manifestContent = targetVersion.manifestContent,
            thinkingLog = targetVersion.thinkingSummary,
            currentVersion = targetVersion.versionNumber,
            updatedAt = System.currentTimeMillis()
        )
        projectDao.updateProject(reverted)
    }

    /**
     * Generates or updates an app using the configured AI model (NVIDIA NIM or Google Gemini).
     */
    suspend fun generateOrUpdateApp(
        projectId: String?,
        userPrompt: String,
        modelIdOverride: String? = null
    ): Result<ProjectEntity> = withContext(Dispatchers.IO) {
        try {
            val modelId = modelIdOverride ?: getSelectedModelId()
            val modelInfo = SupportedModels.findById(modelId)

            val currentProject = if (!projectId.isNullOrBlank()) {
                projectDao.getProjectDirect(projectId)
            } else null

            val systemPrompt = """
You are Puter AI Builder, an expert full-stack engineer and web app architect.
Your goal is to build or modify interactive, responsive web applications running inside an Android WebView environment with Puter.js integration.

Rules:
1. Always write clean, modern, standalone code without external bundlers.
2. Puter.js is available at window.puter (including window.puter.kv.get/set/del/list, window.puter.auth.isSignedIn, etc.). Fallback to localStorage gracefully.
3. Organize files using these clear delimiting markers:
=== THINKING ===
Detailed technical plan, architecture reasoning, edge case handling, and styling strategies.

=== FILE: index.html ===
Full valid HTML5 with responsive viewport, linking styles.css and app.js.

=== FILE: styles.css ===
Modern CSS with CSS variables, sleek dark theme, smooth transitions, mobile-responsive layout.

=== FILE: app.js ===
Vanilla JavaScript handling user interactions, dynamic DOM updates, Web Audio sound feedback if applicable, and Puter KV storage.

=== FILE: manifest.json ===
Standard web app manifest JSON.
""".trimIndent()

            val (modelThoughts, modelText) = if (modelInfo.provider == ModelProvider.NVIDIA_NIM) {
                val nvidiaKey = getNvidiaApiKey()
                if (nvidiaKey.isBlank()) {
                    return@withContext Result.failure(
                        IllegalStateException("NVIDIA API key is missing. Please set it in Settings.")
                    )
                }
                val baseUrl = getNvidiaBaseUrl().ifBlank { SupportedModels.DEFAULT_NVIDIA_BASE_URL }

                val messages = mutableListOf<ChatMessage>()
                messages.add(ChatMessage(role = "system", content = systemPrompt))

                if (currentProject != null) {
                    val contextMessage = """
Current App Files:
[index.html]
${currentProject.htmlContent}

[styles.css]
${currentProject.cssContent}

[app.js]
${currentProject.jsContent}

User modification request:
$userPrompt
Update or rewrite the necessary files to fulfill this request.
""".trimIndent()
                    messages.add(ChatMessage(role = "user", content = contextMessage))
                } else {
                    messages.add(ChatMessage(role = "user", content = "Build a complete web app for: $userPrompt"))
                }

                val request = ChatCompletionRequest(
                    model = modelId,
                    messages = messages,
                    temperature = 0.6f,
                    maxTokens = 4096
                )

                val endpoint = "${baseUrl.trimEnd('/')}/chat/completions"
                val response = OpenAiApiClient.service.createChatCompletion(
                    url = endpoint,
                    authorization = "Bearer $nvidiaKey",
                    request = request
                )

                val choice = response.choices?.firstOrNull()
                    ?: return@withContext Result.failure(Exception("No choices returned by NVIDIA NIM model."))

                val responseMsg = choice.message
                var thoughts = responseMsg?.reasoningContent ?: ""
                var text = responseMsg?.content ?: ""

                val thinkTagRegex = Regex("<think>([\\s\\S]*?)</think>", RegexOption.IGNORE_CASE)
                thinkTagRegex.find(text)?.let { m ->
                    if (thoughts.isBlank()) {
                        thoughts = m.groupValues[1].trim()
                    }
                    text = thinkTagRegex.replace(text, "").trim()
                }

                Pair(thoughts, text)
            } else {
                // Gemini API
                val geminiKey = getGeminiApiKey()
                if (geminiKey.isBlank() || geminiKey == "MY_GEMINI_API_KEY") {
                    return@withContext Result.failure(
                        IllegalStateException("Gemini API key is not configured. Please add your key in Settings.")
                    )
                }

                val conversationParts = mutableListOf<Part>()
                if (currentProject != null) {
                    val contextMessage = """
Current App Files:
[index.html]
${currentProject.htmlContent}

[styles.css]
${currentProject.cssContent}

[app.js]
${currentProject.jsContent}

User modification request:
$userPrompt
Update or rewrite the necessary files to fulfill this request.
""".trimIndent()
                    conversationParts.add(Part(text = contextMessage))
                } else {
                    conversationParts.add(Part(text = "Build a complete web app for: $userPrompt"))
                }

                val request = GeminiRequest(
                    contents = listOf(
                        Content(role = "user", parts = conversationParts)
                    ),
                    generationConfig = GenerationConfig(
                        thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                        temperature = 0.7f
                    ),
                    systemInstruction = Content(
                        parts = listOf(Part(text = systemPrompt))
                    )
                )

                val response = GeminiApiClient.service.generateAppWithHighThinking(geminiKey, request)
                val candidate = response.candidates?.firstOrNull()
                    ?: return@withContext Result.failure(Exception("No generation candidate returned by Gemini."))

                var thoughts = ""
                var text = ""
                candidate.content?.parts?.forEach { part ->
                    if (part.thought == true) {
                        thoughts += (part.text ?: "") + "\n"
                    } else {
                        text += (part.text ?: "") + "\n"
                    }
                }
                Pair(thoughts, text)
            }

            // Parse response content
            val parsedFiles = parseGeneratedContent(modelText)

            val finalThinking = if (modelThoughts.isNotBlank()) {
                "Architectural reasoning by ${modelInfo.name}:\n\n" + modelThoughts.trim() + "\n\n" + (parsedFiles.thinking.ifBlank { "" })
            } else {
                parsedFiles.thinking.ifBlank { "Deep reasoning completed by ${modelInfo.name}." }
            }

            val targetId = currentProject?.id ?: ("proj_" + UUID.randomUUID().toString().take(8))
            val newVersion = (currentProject?.currentVersion ?: 0) + 1

            val titleFromPrompt = if (currentProject != null) {
                currentProject.title
            } else {
                userPrompt.take(28).capitalizeWords()
            }

            val newProject = ProjectEntity(
                id = targetId,
                title = titleFromPrompt,
                description = userPrompt.take(120),
                initialPrompt = currentProject?.initialPrompt ?: userPrompt,
                htmlContent = parsedFiles.html.ifBlank { currentProject?.htmlContent ?: "<h1>App</h1>" },
                cssContent = parsedFiles.css.ifBlank { currentProject?.cssContent ?: "body{background:#111;color:#fff;}" },
                jsContent = parsedFiles.js.ifBlank { currentProject?.jsContent ?: "console.log('ready');" },
                manifestContent = parsedFiles.manifest.ifBlank { currentProject?.manifestContent ?: "{}" },
                thinkingLog = finalThinking,
                currentVersion = newVersion,
                updatedAt = System.currentTimeMillis(),
                createdAt = currentProject?.createdAt ?: System.currentTimeMillis()
            )

            projectDao.insertOrUpdateProject(newProject)

            projectDao.insertVersion(
                ProjectVersionEntity(
                    projectId = targetId,
                    versionNumber = newVersion,
                    prompt = userPrompt,
                    htmlContent = newProject.htmlContent,
                    cssContent = newProject.cssContent,
                    jsContent = newProject.jsContent,
                    manifestContent = newProject.manifestContent,
                    thinkingSummary = finalThinking,
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(newProject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    internal data class ParsedOutput(
        val thinking: String = "",
        val html: String = "",
        val css: String = "",
        val js: String = "",
        val manifest: String = ""
    )

    internal fun parseGeneratedContent(rawText: String): ParsedOutput {
        var thinking = ""
        var html = ""
        var css = ""
        var js = ""
        var manifest = ""

        val thinkingRegex = Regex("=== THINKING ===([\\s\\S]*?)(?==== FILE:|\$)", RegexOption.IGNORE_CASE)
        thinkingRegex.find(rawText)?.let {
            thinking = it.groupValues[1].trim()
        }

        val htmlRegex = Regex("=== FILE: index\\.html ===([\\s\\S]*?)(?==== FILE:|\$)", RegexOption.IGNORE_CASE)
        htmlRegex.find(rawText)?.let {
            html = cleanCodeBlock(it.groupValues[1].trim())
        }

        val cssRegex = Regex("=== FILE: styles?\\.css ===([\\s\\S]*?)(?==== FILE:|\$)", RegexOption.IGNORE_CASE)
        cssRegex.find(rawText)?.let {
            css = cleanCodeBlock(it.groupValues[1].trim())
        }

        val jsRegex = Regex("=== FILE: app\\.js ===([\\s\\S]*?)(?==== FILE:|\$)", RegexOption.IGNORE_CASE)
        jsRegex.find(rawText)?.let {
            js = cleanCodeBlock(it.groupValues[1].trim())
        }

        val manifestRegex = Regex("=== FILE: manifest\\.json ===([\\s\\S]*?)(?==== FILE:|\$)", RegexOption.IGNORE_CASE)
        manifestRegex.find(rawText)?.let {
            manifest = cleanCodeBlock(it.groupValues[1].trim())
        }

        // Fallback: If delimiters weren't strictly used, extract ```html, ```css, ```javascript blocks
        if (html.isBlank()) {
            val htmlBlock = Regex("```(?:html)?\\s*([\\s\\S]*?)```", RegexOption.IGNORE_CASE).find(rawText)
            htmlBlock?.let { html = it.groupValues[1].trim() }
        }
        if (css.isBlank()) {
            val cssBlock = Regex("```css\\s*([\\s\\S]*?)```", RegexOption.IGNORE_CASE).find(rawText)
            cssBlock?.let { css = it.groupValues[1].trim() }
        }
        if (js.isBlank()) {
            val jsBlock = Regex("```(?:javascript|js)\\s*([\\s\\S]*?)```", RegexOption.IGNORE_CASE).find(rawText)
            jsBlock?.let { js = it.groupValues[1].trim() }
        }

        return ParsedOutput(thinking, html, css, js, manifest)
    }

    private fun cleanCodeBlock(content: String): String {
        var res = content.trim()
        if (res.startsWith("```")) {
            val firstLineEnd = res.indexOf('\n')
            if (firstLineEnd != -1) {
                res = res.substring(firstLineEnd + 1)
            }
        }
        if (res.endsWith("```")) {
            res = res.substring(0, res.length - 3)
        }
        return res.trim()
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
