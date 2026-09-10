package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SupportedModels
import com.example.ui.preview.ConsoleLogItem
import com.example.ui.preview.ViewportMode
import com.example.ui.viewmodel.BuilderTab
import com.example.ui.viewmodel.BuilderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BuilderViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var app: Application
    private lateinit var viewModel: BuilderViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
        val fakeRepo = com.example.data.repository.ProjectRepository(
            app,
            projectDaoProvider = { FakeProjectDao() }
        )
        viewModel = BuilderViewModel(app, repository = fakeRepo)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state has default model and settings`() {
        val state = viewModel.uiState.value
        assertEquals("nvidia/nemotron-3-super-120b-a12b", state.selectedModel.id)
        assertEquals(SupportedModels.DEFAULT_NVIDIA_API_KEY, state.nvidiaApiKey)
        assertEquals(SupportedModels.DEFAULT_NVIDIA_BASE_URL, state.nvidiaBaseUrl)
        assertEquals(BuilderTab.PREVIEW, state.activeTab)
        assertEquals(ViewportMode.RESPONSIVE, state.viewportMode)
        assertEquals(3, state.availableModels.size)
    }

    @Test
    fun `test model selection switching`() {
        // Switch to Gemini 3.1 Pro
        viewModel.selectModel("gemini-3.1-pro-preview")
        assertEquals("gemini-3.1-pro-preview", viewModel.uiState.value.selectedModel.id)
        assertEquals("Gemini 3.1 Pro", viewModel.uiState.value.selectedModel.name)

        // Switch to Llama 3.3
        viewModel.selectModel("meta/llama-3.3-70b-instruct")
        assertEquals("meta/llama-3.3-70b-instruct", viewModel.uiState.value.selectedModel.id)

        // Switch back to Nemotron 3 Super
        viewModel.selectModel("nvidia/nemotron-3-super-120b-a12b")
        assertEquals("nvidia/nemotron-3-super-120b-a12b", viewModel.uiState.value.selectedModel.id)
    }

    @Test
    fun `test tab navigation and viewport changes`() {
        viewModel.selectTab(BuilderTab.CODE)
        assertEquals(BuilderTab.CODE, viewModel.uiState.value.activeTab)

        viewModel.selectTab(BuilderTab.THINKING)
        assertEquals(BuilderTab.THINKING, viewModel.uiState.value.activeTab)

        viewModel.selectTab(BuilderTab.VERSIONS)
        assertEquals(BuilderTab.VERSIONS, viewModel.uiState.value.activeTab)

        viewModel.selectTab(BuilderTab.PREVIEW)
        assertEquals(BuilderTab.PREVIEW, viewModel.uiState.value.activeTab)

        viewModel.setViewportMode(ViewportMode.PHONE)
        assertEquals(ViewportMode.PHONE, viewModel.uiState.value.viewportMode)

        viewModel.setViewportMode(ViewportMode.TABLET)
        assertEquals(ViewportMode.TABLET, viewModel.uiState.value.viewportMode)

        viewModel.setViewportMode(ViewportMode.RESPONSIVE)
        assertEquals(ViewportMode.RESPONSIVE, viewModel.uiState.value.viewportMode)
    }

    @Test
    fun `test console logs handling`() {
        assertEquals(0, viewModel.uiState.value.consoleLogs.size)

        viewModel.addConsoleLog(ConsoleLogItem(level = "info", message = "Puter SDK initialized"))
        viewModel.addConsoleLog(ConsoleLogItem(level = "debug", message = "Window width: 375"))
        viewModel.addConsoleLog(ConsoleLogItem(level = "warn", message = "Storage read warning"))

        assertEquals(3, viewModel.uiState.value.consoleLogs.size)
        assertEquals("info", viewModel.uiState.value.consoleLogs[0].level)
        assertEquals("Storage read warning", viewModel.uiState.value.consoleLogs[2].message)

        viewModel.clearConsoleLogs()
        assertEquals(0, viewModel.uiState.value.consoleLogs.size)
    }

    @Test
    fun `test save settings updates state`() {
        viewModel.saveSettings(
            selectedModelId = "gemini-3.1-pro-preview",
            nvidiaApiKey = "nvapi-new-key-xyz",
            nvidiaBaseUrl = "https://integrate.api.nvidia.com/v1",
            geminiApiKey = "gemini-key-abc"
        )

        val updated = viewModel.uiState.value
        assertEquals("gemini-3.1-pro-preview", updated.selectedModel.id)
        assertEquals("nvapi-new-key-xyz", updated.nvidiaApiKey)
        assertEquals("gemini-key-abc", updated.geminiApiKey)
    }

    @Test
    fun `test error state clearing`() {
        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
