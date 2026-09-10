package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.preview.PuterHtmlBundler
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary

@Composable
fun ExportShareDialog(
    projectTitle: String,
    html: String,
    css: String,
    js: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bundled = PuterHtmlBundler.buildBundledHtml(html, css, js)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text(
                text = "Export & Share Application",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = PuterTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Export your web application as standalone HTML or share source code with other developers.",
                    fontSize = 13.sp,
                    color = PuterTextSecondary
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E131C)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Bundle Statistics:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PuterBlueLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• HTML: ${html.length} bytes\n• CSS: ${css.length} bytes\n• JS: ${js.length} bytes\n• Total Bundled: ${bundled.length} bytes",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = PuterTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Standalone HTML", bundled))
                            Toast.makeText(context, "Copied Standalone HTML bundle", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PuterBlueLight)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Copy HTML", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TITLE, projectTitle)
                                putExtra(Intent.EXTRA_TEXT, bundled)
                                type = "text/html"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Share $projectTitle")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PuterBlueLight)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Share App", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = PuterTextSecondary)
            ) {
                Text("Close")
            }
        }
    )
}
