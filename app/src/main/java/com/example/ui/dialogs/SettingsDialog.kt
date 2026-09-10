package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.SupportedModels
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterEmeraldLight
import com.example.ui.theme.PuterPurpleLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary

@Composable
fun SettingsDialog(
    currentModel: AiModel,
    nvidiaApiKey: String,
    nvidiaBaseUrl: String,
    geminiApiKey: String,
    onSaveSettings: (selectedModelId: String, nvidiaKey: String, nvidiaBaseUrl: String, geminiKey: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedModelId by remember { mutableStateOf(currentModel.id) }
    var currentNvidiaKey by remember { mutableStateOf(nvidiaApiKey) }
    var currentNvidiaBaseUrl by remember { mutableStateOf(nvidiaBaseUrl) }
    var currentGeminiKey by remember { mutableStateOf(geminiApiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = PuterBlueLight,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "AI Model & API Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PuterTextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Active Model Selection
                Text(
                    text = "Active Generation Model",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PuterTextPrimary
                )

                SupportedModels.allModels.forEach { model ->
                    val isChosen = model.id == selectedModelId

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) Color(0xFF142032) else Color(0xFF0F1520)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isChosen) PuterBlueLight else ObsidianBorder
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedModelId = model.id }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        if (isChosen) PuterBlueLight else Color.Transparent,
                                        CircleShape
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isChosen) PuterBlueLight else PuterTextSecondary,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChosen) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = model.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isChosen) Color.White else PuterTextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (model.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight.copy(alpha = 0.2f)
                                                else PuterPurpleLight.copy(alpha = 0.2f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = model.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = if (model.provider == ModelProvider.NVIDIA_NIM) PuterEmeraldLight else PuterPurpleLight
                                        )
                                    }
                                }
                                Text(
                                    text = model.provider.title,
                                    fontSize = 11.sp,
                                    color = PuterTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // NVIDIA NIM Configuration Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C141C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = PuterEmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "NVIDIA NIM API Configuration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PuterEmeraldLight
                            )
                        }

                        Text(
                            text = "Model: nvidia/nemotron-3-super-120b-a12b",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = PuterTextSecondary
                        )

                        Text(
                            text = "NVIDIA API Key:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PuterTextPrimary
                        )
                        OutlinedTextField(
                            value = currentNvidiaKey,
                            onValueChange = { currentNvidiaKey = it },
                            placeholder = { Text("nvapi-...", fontSize = 12.sp, color = PuterTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PuterTextPrimary,
                                unfocusedTextColor = PuterTextPrimary,
                                focusedBorderColor = PuterEmeraldLight,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = "NVIDIA Base URL:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PuterTextPrimary
                        )
                        OutlinedTextField(
                            value = currentNvidiaBaseUrl,
                            onValueChange = { currentNvidiaBaseUrl = it },
                            placeholder = { Text("https://integrate.api.nvidia.com/v1", fontSize = 12.sp, color = PuterTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PuterTextPrimary,
                                unfocusedTextColor = PuterTextPrimary,
                                focusedBorderColor = PuterEmeraldLight,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Google Gemini Configuration Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF140F1D)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = PuterPurpleLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Google Gemini API Configuration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = PuterPurpleLight
                            )
                        }

                        Text(
                            text = "Model: gemini-3.1-pro-preview (High Thinking)",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = PuterTextSecondary
                        )

                        Text(
                            text = "Gemini API Key:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PuterTextPrimary
                        )
                        OutlinedTextField(
                            value = currentGeminiKey,
                            onValueChange = { currentGeminiKey = it },
                            placeholder = { Text("Enter Gemini API key", fontSize = 12.sp, color = PuterTextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PuterTextPrimary,
                                unfocusedTextColor = PuterTextPrimary,
                                focusedBorderColor = PuterPurpleLight,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveSettings(
                        selectedModelId,
                        currentNvidiaKey,
                        currentNvidiaBaseUrl,
                        currentGeminiKey
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PuterBlueLight)
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = PuterTextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}
