package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.SupportedModels
import com.example.ui.dialogs.ModelSelectorDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.prompt.PromptBar
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BuilderUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `test ModelSelectorDialog displays models and handles selection`() {
        var selectedModelId = ""
        var dismissed = false
        var openedSettings = false

        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                ModelSelectorDialog(
                    currentModel = SupportedModels.NEMOTRON_3_SUPER,
                    onSelectModel = { selectedModelId = it },
                    onOpenSettings = { openedSettings = true },
                    onDismiss = { dismissed = true }
                )
            }
        }

        // Check header
        composeTestRule.onNodeWithText("Select AI Generation Engine").assertExists()

        // Check models listed
        composeTestRule.onNodeWithText("Nemotron 3 Super (120B)").assertExists()
        composeTestRule.onNodeWithText("Gemini 3.1 Pro").assertExists()
        composeTestRule.onNodeWithText("Llama 3.3 70B").assertExists()

        // Click Gemini 3.1 Pro Card
        composeTestRule.onNodeWithTag("model_card_gemini-3.1-pro-preview").performClick()
        assertEquals("gemini-3.1-pro-preview", selectedModelId)
    }

    @Test
    fun `test PromptBar displays active model and chips`() {
        var modelSelectorOpened = false
        var submittedPrompt = ""

        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                PromptBar(
                    isGenerating = false,
                    generationStatus = "",
                    selectedModel = SupportedModels.NEMOTRON_3_SUPER,
                    onOpenModelSelector = { modelSelectorOpened = true },
                    onSubmitPrompt = { submittedPrompt = it }
                )
            }
        }

        // Active model display
        composeTestRule.onNodeWithText("Nemotron 3 Super (120B)").assertExists()
        composeTestRule.onNodeWithText("120B NIM").assertExists()

        // Click model pill
        composeTestRule.onNodeWithText("Nemotron 3 Super (120B)").performClick()
        assertTrue(modelSelectorOpened)

        // Verify suggestion chips are present
        composeTestRule.onNodeWithText("+ Add sound effects").assertExists()
        composeTestRule.onNodeWithText("+ Add dark/light toggle").assertExists()
    }

    @Test
    fun `test SettingsDialog renders fields and save button`() {
        var savedModelId = ""
        var savedNvidiaKey = ""

        composeTestRule.setContent {
            MyApplicationTheme(darkTheme = true) {
                SettingsDialog(
                    currentModel = SupportedModels.NEMOTRON_3_SUPER,
                    nvidiaApiKey = "nvapi-test-key",
                    nvidiaBaseUrl = "https://integrate.api.nvidia.com/v1",
                    geminiApiKey = "gemini-test-key",
                    onSaveSettings = { modelId, nKey, _, _ ->
                        savedModelId = modelId
                        savedNvidiaKey = nKey
                    },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("AI Model & API Settings").assertExists()
        composeTestRule.onNodeWithText("NVIDIA NIM API Configuration").assertExists()
        composeTestRule.onNodeWithText("Google Gemini API Configuration").assertExists()

        composeTestRule.onNodeWithText("Save Settings").performClick()
        assertEquals(SupportedModels.NEMOTRON_3_SUPER.id, savedModelId)
        assertEquals("nvapi-test-key", savedNvidiaKey)
    }
}
