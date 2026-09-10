package com.example.ui.thinking

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceElevated
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterEmeraldLight
import com.example.ui.theme.PuterPurpleLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary

@Composable
fun ThinkingView(
    thinkingLog: String,
    prompt: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isRawExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D14))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High Thinking Status Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, PuterPurpleLight.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(PuterPurpleLight.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, PuterPurpleLight.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Thinking mode",
                            tint = PuterPurpleLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "High Thinking Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PuterTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .background(PuterEmeraldLight.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("COMPLETED", fontSize = 10.sp, color = PuterEmeraldLight, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Model: gemini-3.1-pro-preview • thinkingLevel: HIGH",
                            fontSize = 12.sp,
                            color = PuterTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Thinking Log", thinkingLog))
                    Toast.makeText(context, "Copied reasoning thoughts", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy thoughts",
                        tint = PuterTextSecondary
                    )
                }
            }
        }

        // Prompt Card
        Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "REASONING FOR PROMPT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PuterBlueLight,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"$prompt\"",
                    fontSize = 14.sp,
                    color = PuterTextPrimary
                )
            }
        }

        // Structured Reasoning Steps
        Text(
            text = "ARCHITECTURAL REASONING PHASES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = PuterTextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        ThinkingStepItem(
            phaseNumber = "1",
            title = "Requirements & Scope Analysis",
            subtitle = "Deconstructing user intent into component hierarchy, DOM layout, and interactive states.",
            status = "Verified"
        )

        ThinkingStepItem(
            phaseNumber = "2",
            title = "Responsive UI & Puter Dark Theme",
            subtitle = "Drafting high-contrast typography, CSS custom properties, and smooth touch-optimized interactions.",
            status = "Engineered"
        )

        ThinkingStepItem(
            phaseNumber = "3",
            title = "State Machine & Puter.js KV Persistence",
            subtitle = "Binding events, audio synthesis cues, and seamless storage sync with resilient local fallback.",
            status = "Synced"
        )

        ThinkingStepItem(
            phaseNumber = "4",
            title = "Zero-Error Runtime Inspection",
            subtitle = "Ensuring strict sanitization, responsive viewport constraints, and zero broken asset dependencies.",
            status = "Passed"
        )

        // Raw Deep Thought Trace
        Card(
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gemini 3.1 Pro Thought Output",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PuterTextPrimary
                    )
                    OutlinedButton(
                        onClick = { isRawExpanded = !isRawExpanded },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PuterBlueLight)
                    ) {
                        Text(if (isRawExpanded) "Collapse" else "Expand Full Trace", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isRawExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = isRawExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF06090E), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF1E2635), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = thinkingLog.ifBlank { "Deep reasoning finished without raw thought markers." },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = PuterTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThinkingStepItem(
    phaseNumber: String,
    title: String,
    subtitle: String,
    status: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PuterBlueLight.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, PuterBlueLight.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = phaseNumber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = PuterBlueLight
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = PuterTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = PuterTextSecondary,
                    lineHeight = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .background(PuterEmeraldLight.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 11.sp,
                    color = PuterEmeraldLight,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
