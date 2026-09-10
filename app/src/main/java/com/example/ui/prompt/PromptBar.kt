package com.example.ui.prompt

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModel
import com.example.data.model.ModelProvider
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterEmeraldLight
import com.example.ui.theme.PuterPurpleLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary

@Composable
fun PromptBar(
    isGenerating: Boolean,
    generationStatus: String,
    selectedModel: AiModel,
    onOpenModelSelector: () -> Unit,
    onSubmitPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    val suggestionChips = listOf(
        "Add sound effects",
        "Add search & filter",
        "Add export to CSV",
        "Add confetti animation",
        "Save to Puter KV",
        "Add dark/light toggle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Model Selection & Active Engine Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clickable Model Chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .background(Color(0xFF161E2E), RoundedCornerShape(12.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                    .clickable(enabled = !isGenerating) { onOpenModelSelector() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (selectedModel.provider == ModelProvider.NVIDIA_NIM) Icons.Default.Memory else Icons.Default.Psychology,
                    contentDescription = null,
                    tint = if (selectedModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = selectedModel.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight
                )
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight.copy(alpha = 0.2f)
                            else PuterPurpleLight.copy(alpha = 0.2f),
                            RoundedCornerShape(3.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = selectedModel.badge,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (selectedModel.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Change Model",
                    tint = PuterTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }

            if (isGenerating) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = PuterBlueLight
                    )
                    Text(
                        text = "Reasoning...",
                        fontSize = 11.sp,
                        color = PuterBlueLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Text(
                    text = "Tap to switch model",
                    fontSize = 10.sp,
                    color = PuterTextSecondary.copy(alpha = 0.7f)
                )
            }
        }

        // Suggestions Scroll Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestionChips.forEach { chipText ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1B2230), RoundedCornerShape(12.dp))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isGenerating) {
                            promptInput = chipText
                        }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "+ $chipText",
                        color = PuterTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Main Input Field & Action Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = {
                    Text(
                        "Describe what to build or change in this app...",
                        fontSize = 13.sp,
                        color = PuterTextSecondary
                    )
                },
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PuterTextPrimary,
                    unfocusedTextColor = PuterTextPrimary,
                    focusedContainerColor = Color(0xFF0D1117),
                    unfocusedContainerColor = Color(0xFF0D1117),
                    focusedBorderColor = PuterBlueLight,
                    unfocusedBorderColor = ObsidianBorder
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (promptInput.isNotBlank() && !isGenerating) PuterBlueLight else Color(0xFF21262D),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = PuterBlueLight
                    )
                } else {
                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                onSubmitPrompt(promptInput)
                                promptInput = ""
                            }
                        },
                        enabled = promptInput.isNotBlank() && !isGenerating
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Prompt",
                            tint = if (promptInput.isNotBlank()) Color.White else PuterTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
