package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiModel
import com.example.data.model.ModelProvider
import com.example.ui.code.CodeEditorView
import com.example.ui.dialogs.ConsoleBottomSheet
import com.example.ui.dialogs.ExportShareDialog
import com.example.ui.dialogs.ModelSelectorDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.dialogs.TemplatesBottomSheet
import com.example.ui.preview.PuterLivePreview
import com.example.ui.preview.ViewportMode
import com.example.ui.prompt.PromptBar
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterEmeraldLight
import com.example.ui.theme.PuterPurpleLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary
import com.example.ui.thinking.ThinkingView
import com.example.ui.viewmodel.BuilderTab
import com.example.ui.viewmodel.BuilderViewModel
import com.example.ui.versions.VersionsView

class MainActivity : ComponentActivity() {
    private val viewModel: BuilderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                BuilderApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BuilderApp(viewModel: BuilderViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showTemplatesSheet by remember { mutableStateOf(false) }
    var showConsoleSheet by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showModelSelectorDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showViewportMenu by remember { mutableStateOf(false) }

    val currentProj = uiState.currentProject

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        bottomBar = {
            PromptBar(
                isGenerating = uiState.isGenerating,
                generationStatus = uiState.generationStatus,
                selectedModel = uiState.selectedModel,
                onOpenModelSelector = { showModelSelectorDialog = true },
                onSubmitPrompt = { prompt -> viewModel.submitPrompt(prompt) },
                modifier = Modifier.testTag("prompt_bar")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBg)
        ) {
            // Header Top App Bar
            BuilderTopBar(
                projectTitle = currentProj?.title ?: "Puter App",
                versionNumber = currentProj?.currentVersion ?: 1,
                activeModel = uiState.selectedModel,
                consoleLogCount = uiState.consoleLogs.size,
                viewportMode = uiState.viewportMode,
                onTemplatesClick = { showTemplatesSheet = true },
                onModelClick = { showModelSelectorDialog = true },
                onViewportMenuToggle = { showViewportMenu = !showViewportMenu },
                onReloadClick = { viewModel.reloadPreview() },
                onConsoleClick = { showConsoleSheet = true },
                onExportClick = { showExportDialog = true },
                onSettingsClick = { showSettingsDialog = true }
            )

            // Viewport Menu Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                DropdownMenu(
                    expanded = showViewportMenu,
                    onDismissRequest = { showViewportMenu = false },
                    modifier = Modifier.background(ObsidianSurfaceElevated)
                ) {
                    ViewportMode.values().forEach { mode ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = mode.title,
                                    color = if (uiState.viewportMode == mode) PuterBlueLight else PuterTextPrimary,
                                    fontWeight = if (uiState.viewportMode == mode) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                viewModel.setViewportMode(mode)
                                showViewportMenu = false
                            }
                        )
                    }
                }
            }

            // Error Message Banner (if any)
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                uiState.errorMessage?.let { errorText ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF381519)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF85149)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorText,
                                color = Color(0xFFFF7B72),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss error",
                                    tint = Color(0xFFFF7B72)
                                )
                            }
                        }
                    }
                }
            }

            // Tab Navigation Bar
            BuilderTabBar(
                activeTab = uiState.activeTab,
                onTabSelect = { viewModel.selectTab(it) }
            )

            // Active Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.activeTab) {
                    BuilderTab.PREVIEW -> {
                        PuterLivePreview(
                            htmlContent = currentProj?.htmlContent ?: "",
                            cssContent = currentProj?.cssContent ?: "",
                            jsContent = currentProj?.jsContent ?: "",
                            viewportMode = uiState.viewportMode,
                            reloadTrigger = uiState.reloadTrigger,
                            onConsoleLog = { log -> viewModel.addConsoleLog(log) },
                            modifier = Modifier.testTag("puter_preview")
                        )
                    }
                    BuilderTab.CODE -> {
                        CodeEditorView(
                            htmlContent = currentProj?.htmlContent ?: "",
                            cssContent = currentProj?.cssContent ?: "",
                            jsContent = currentProj?.jsContent ?: "",
                            manifestContent = currentProj?.manifestContent ?: "",
                            onFilesChanged = { html, css, js, manifest ->
                                viewModel.updateCode(html, css, js, manifest)
                            },
                            modifier = Modifier.testTag("code_editor")
                        )
                    }
                    BuilderTab.THINKING -> {
                        ThinkingView(
                            thinkingLog = currentProj?.thinkingLog ?: "",
                            prompt = currentProj?.initialPrompt ?: "",
                            modifier = Modifier.testTag("thinking_view")
                        )
                    }
                    BuilderTab.VERSIONS -> {
                        VersionsView(
                            versions = uiState.versions,
                            currentVersion = currentProj?.currentVersion ?: 1,
                            onRevertToVersion = { vNum -> viewModel.revertToVersion(vNum) },
                            modifier = Modifier.testTag("versions_view")
                        )
                    }
                }
            }
        }
    }

    // Dialogs & Sheets
    if (showModelSelectorDialog) {
        ModelSelectorDialog(
            currentModel = uiState.selectedModel,
            onSelectModel = { modelId -> viewModel.selectModel(modelId) },
            onOpenSettings = { showSettingsDialog = true },
            onDismiss = { showModelSelectorDialog = false }
        )
    }

    if (showTemplatesSheet) {
        TemplatesBottomSheet(
            onDismiss = { showTemplatesSheet = false },
            onSelectTemplate = { template -> viewModel.loadTemplate(template.id) }
        )
    }

    if (showConsoleSheet) {
        ConsoleBottomSheet(
            logs = uiState.consoleLogs,
            onClearLogs = { viewModel.clearConsoleLogs() },
            onDismiss = { showConsoleSheet = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentModel = uiState.selectedModel,
            nvidiaApiKey = uiState.nvidiaApiKey,
            nvidiaBaseUrl = uiState.nvidiaBaseUrl,
            geminiApiKey = uiState.geminiApiKey,
            onSaveSettings = { modelId, nvidiaKey, nvidiaBaseUrl, geminiKey ->
                viewModel.saveSettings(modelId, nvidiaKey, nvidiaBaseUrl, geminiKey)
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showExportDialog && currentProj != null) {
        ExportShareDialog(
            projectTitle = currentProj.title,
            html = currentProj.htmlContent,
            css = currentProj.cssContent,
            js = currentProj.jsContent,
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun BuilderTopBar(
    projectTitle: String,
    versionNumber: Int,
    activeModel: AiModel,
    consoleLogCount: Int,
    viewportMode: ViewportMode,
    onTemplatesClick: () -> Unit,
    onModelClick: () -> Unit,
    onViewportMenuToggle: () -> Unit,
    onReloadClick: () -> Unit,
    onConsoleClick: () -> Unit,
    onExportClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(PuterBlueLight.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .border(1.dp, PuterBlueLight.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = PuterBlueLight
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = projectTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PuterTextPrimary,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF21262D), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "v$versionNumber",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = PuterBlueLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Puter AI Builder • Live",
                    fontSize = 11.sp,
                    color = PuterEmeraldLight
                )
            }
        }

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Model Quick Switch Pill
            Box(
                modifier = Modifier
                    .background(Color(0xFF161E2E), RoundedCornerShape(8.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
                    .clickable { onModelClick() }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (activeModel.provider == ModelProvider.NVIDIA_NIM) Icons.Default.Memory else Icons.Default.Psychology,
                        contentDescription = "Active Model",
                        tint = if (activeModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = activeModel.badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (activeModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight
                    )
                }
            }

            // Gallery Templates
            IconButton(onClick = onTemplatesClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "Templates Gallery",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Viewport Mode
            IconButton(onClick = onViewportMenuToggle, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Devices,
                    contentDescription = "Viewport: ${viewportMode.title}",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Reload Preview
            IconButton(onClick = onReloadClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload app",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Console Logs
            IconButton(onClick = onConsoleClick, modifier = Modifier.size(36.dp)) {
                BadgedBox(
                    badge = {
                        if (consoleLogCount > 0) {
                            Box(
                                modifier = Modifier
                                    .background(PuterBlueLight, CircleShape)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "$consoleLogCount",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Console Logs",
                        tint = PuterTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Share / Export
            IconButton(onClick = onExportClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Export App",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Settings
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun BuilderTabBar(
    activeTab: BuilderTab,
    onTabSelect: (BuilderTab) -> Unit
) {
    TabRow(
        selectedTabIndex = activeTab.ordinal,
        containerColor = ObsidianSurface,
        contentColor = PuterBlueLight,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                color = PuterBlueLight,
                height = 2.dp
            )
        },
        divider = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ObsidianBorder)
            )
        }
    ) {
        BuilderTab.values().forEach { tab ->
            val isSelected = activeTab == tab
            val (iconVector, label) = when (tab) {
                BuilderTab.PREVIEW -> Pair(Icons.Default.PlayArrow, "Preview")
                BuilderTab.CODE -> Pair(Icons.Default.Code, "Code")
                BuilderTab.THINKING -> Pair(Icons.Default.Psychology, "Thinking")
                BuilderTab.VERSIONS -> Pair(Icons.Default.History, "Versions")
            }

            Tab(
                selected = isSelected,
                onClick = { onTabSelect(tab) },
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = if (isSelected) {
                                if (tab == BuilderTab.THINKING) PuterPurpleLight else PuterBlueLight
                            } else PuterTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PuterTextPrimary else PuterTextSecondary
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
