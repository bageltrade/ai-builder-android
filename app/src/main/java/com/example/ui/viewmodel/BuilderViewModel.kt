package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectVersionEntity
import com.example.data.model.AiModel
import com.example.data.model.SupportedModels
import com.example.data.repository.ProjectRepository
import com.example.ui.preview.ConsoleLogItem
import com.example.ui.preview.ViewportMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BuilderTab(val title: String) {
    PREVIEW("Preview"),
    CODE("Code"),
    THINKING("Thinking"),
    VERSIONS("Versions")
}

data class BuilderUiState(
    val currentProject: ProjectEntity? = null,
    val versions: List<ProjectVersionEntity> = emptyList(),
    val activeTab: BuilderTab = BuilderTab.PREVIEW,
    val viewportMode: ViewportMode = ViewportMode.RESPONSIVE,
    val isGenerating: Boolean = false,
    val generationStatus: String = "",
    val consoleLogs: List<ConsoleLogItem> = emptyList(),
    val errorMessage: String? = null,
    val reloadTrigger: Int = 0,
    val selectedModel: AiModel = SupportedModels.NEMOTRON_3_SUPER,
    val availableModels: List<AiModel> = SupportedModels.allModels,
    val nvidiaApiKey: String = SupportedModels.DEFAULT_NVIDIA_API_KEY,
    val nvidiaBaseUrl: String = SupportedModels.DEFAULT_NVIDIA_BASE_URL,
    val geminiApiKey: String = ""
)

class BuilderViewModel(
    application: Application,
    private val repository: ProjectRepository = ProjectRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        BuilderUiState(
            selectedModel = SupportedModels.findById(repository.getSelectedModelId()),
            nvidiaApiKey = repository.getNvidiaApiKey(),
            nvidiaBaseUrl = repository.getNvidiaBaseUrl(),
            geminiApiKey = repository.getGeminiApiKey()
        )
    )
    val uiState: StateFlow<BuilderUiState> = _uiState.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val projectId = repository.initializeDefaultProjectIfNeeded()
            loadProject(projectId)
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            repository.getProject(projectId).collect { project ->
                _uiState.update { it.copy(currentProject = project) }
            }
        }
        viewModelScope.launch {
            repository.getVersions(projectId).collect { versionList ->
                _uiState.update { it.copy(versions = versionList) }
            }
        }
    }

    fun selectTab(tab: BuilderTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun setViewportMode(mode: ViewportMode) {
        _uiState.update { it.copy(viewportMode = mode) }
    }

    fun selectModel(modelId: String) {
        val model = SupportedModels.findById(modelId)
        repository.setSelectedModelId(model.id)
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun submitPrompt(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() || _uiState.value.isGenerating) return

        val activeModel = _uiState.value.selectedModel

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    generationStatus = "${activeModel.name} reasoning in progress...",
                    errorMessage = null
                )
            }

            val currentId = _uiState.value.currentProject?.id
            val result = repository.generateOrUpdateApp(currentId, trimmed, activeModel.id)

            result.onSuccess { newProject ->
                _uiState.update {
                    it.copy(
                        currentProject = newProject,
                        isGenerating = false,
                        generationStatus = "",
                        reloadTrigger = it.reloadTrigger + 1
                    )
                }
                loadProject(newProject.id)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        generationStatus = "",
                        errorMessage = error.localizedMessage ?: "Failed to generate app. Please verify API configuration."
                    )
                }
            }
        }
    }

    fun loadTemplate(templateId: String) {
        viewModelScope.launch {
            val newId = repository.loadTemplate(templateId)
            loadProject(newId)
            _uiState.update {
                it.copy(
                    reloadTrigger = it.reloadTrigger + 1,
                    activeTab = BuilderTab.PREVIEW
                )
            }
        }
    }

    fun updateCode(html: String, css: String, js: String, manifest: String) {
        val current = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.updateProjectFiles(current.id, html, css, js, manifest)
            _uiState.update {
                it.copy(
                    currentProject = current.copy(
                        htmlContent = html,
                        cssContent = css,
                        jsContent = js,
                        manifestContent = manifest
                    ),
                    reloadTrigger = it.reloadTrigger + 1
                )
            }
        }
    }

    fun revertToVersion(versionNumber: Int) {
        val current = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            repository.revertToVersion(current.id, versionNumber)
            _uiState.update {
                it.copy(
                    reloadTrigger = it.reloadTrigger + 1,
                    activeTab = BuilderTab.PREVIEW
                )
            }
        }
    }

    fun reloadPreview() {
        _uiState.update { it.copy(reloadTrigger = it.reloadTrigger + 1) }
    }

    fun addConsoleLog(log: ConsoleLogItem) {
        _uiState.update {
            val updated = it.consoleLogs.toMutableList().apply { add(log) }
            if (updated.size > 200) updated.removeAt(0)
            it.copy(consoleLogs = updated)
        }
    }

    fun clearConsoleLogs() {
        _uiState.update { it.copy(consoleLogs = emptyList()) }
    }

    fun saveSettings(
        selectedModelId: String,
        nvidiaApiKey: String,
        nvidiaBaseUrl: String,
        geminiApiKey: String
    ) {
        repository.setSelectedModelId(selectedModelId)
        repository.saveNvidiaApiKey(nvidiaApiKey)
        repository.saveNvidiaBaseUrl(nvidiaBaseUrl)
        repository.saveGeminiApiKey(geminiApiKey)

        _uiState.update {
            it.copy(
                selectedModel = SupportedModels.findById(selectedModelId),
                nvidiaApiKey = nvidiaApiKey,
                nvidiaBaseUrl = nvidiaBaseUrl,
                geminiApiKey = geminiApiKey
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
