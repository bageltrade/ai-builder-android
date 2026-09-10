package com.example.ui.code

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CodeEditorBg
import com.example.ui.theme.CodeEditorLineNum
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PuterBlueLight
import com.example.ui.theme.PuterTextPrimary
import com.example.ui.theme.PuterTextSecondary
import com.example.ui.theme.SyntaxAttribute
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxTag

enum class ProjectFile(val fileName: String, val language: String) {
    HTML("index.html", "html"),
    CSS("styles.css", "css"),
    JS("app.js", "javascript"),
    MANIFEST("manifest.json", "json")
}

@Composable
fun CodeEditorView(
    htmlContent: String,
    cssContent: String,
    jsContent: String,
    manifestContent: String,
    onFilesChanged: (html: String, css: String, js: String, manifest: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFile by remember { mutableStateOf(ProjectFile.HTML) }
    var isEditing by remember { mutableStateOf(false) }

    val currentContent = when (selectedFile) {
        ProjectFile.HTML -> htmlContent
        ProjectFile.CSS -> cssContent
        ProjectFile.JS -> jsContent
        ProjectFile.MANIFEST -> manifestContent
    }

    val lines = remember(currentContent) {
        currentContent.lines()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CodeEditorBg)
    ) {
        // Tab Header & Actions Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedFile.ordinal,
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentColor = PuterBlueLight,
                edgePadding = 0.dp,
                divider = {}
            ) {
                ProjectFile.values().forEach { file ->
                    Tab(
                        selected = selectedFile == file,
                        onClick = { selectedFile = file },
                        text = {
                            Text(
                                text = file.fileName,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (selectedFile == file) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedFile == file) PuterBlueLight else PuterTextSecondary
                            )
                        }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Line Count Badge
                Text(
                    text = "${lines.size} lines",
                    fontSize = 11.sp,
                    color = PuterTextSecondary,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                // Edit Mode Toggle
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Visibility else Icons.Default.Edit,
                        contentDescription = if (isEditing) "View Mode" else "Edit Mode",
                        tint = if (isEditing) PuterBlueLight else PuterTextSecondary
                    )
                }

                // Copy File Button
                IconButton(onClick = {
                    val clipboard =
                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(selectedFile.fileName, currentContent)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied ${selectedFile.fileName}", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = PuterTextSecondary
                    )
                }
            }
        }

        // Code Editor Body with Line Numbers
        val vScrollState = rememberScrollState()
        val hScrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(vScrollState)
            ) {
                // Line numbers gutter
                Column(
                    modifier = Modifier
                        .width(42.dp)
                        .background(Color(0xFF090D14))
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..maxOf(1, lines.size)) {
                        Text(
                            text = "$i",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CodeEditorLineNum,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(ObsidianBorder)
                )

                // Code text content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 12.dp, end = 12.dp, bottom = 24.dp)
                        .horizontalScroll(hScrollState)
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = currentContent,
                            onValueChange = { updated ->
                                when (selectedFile) {
                                    ProjectFile.HTML -> onFilesChanged(updated, cssContent, jsContent, manifestContent)
                                    ProjectFile.CSS -> onFilesChanged(htmlContent, updated, jsContent, manifestContent)
                                    ProjectFile.JS -> onFilesChanged(htmlContent, cssContent, updated, manifestContent)
                                    ProjectFile.MANIFEST -> onFilesChanged(htmlContent, cssContent, jsContent, updated)
                                }
                            },
                            textStyle = TextStyle(
                                color = PuterTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 20.sp
                            ),
                            cursorBrush = SolidColor(PuterBlueLight),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        val annotated = remember(currentContent, selectedFile) {
                            highlightSyntax(currentContent, selectedFile)
                        }
                        Text(
                            text = annotated,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

private fun highlightSyntax(code: String, file: ProjectFile) = buildAnnotatedString {
    val lines = code.split("\n")
    lines.forEachIndexed { idx, line ->
        when (file) {
            ProjectFile.HTML -> highlightHtmlLine(line)
            ProjectFile.CSS -> highlightCssLine(line)
            ProjectFile.JS -> highlightJsLine(line)
            ProjectFile.MANIFEST -> highlightJsonLine(line)
        }
        if (idx < lines.size - 1) append("\n")
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.highlightHtmlLine(line: String) {
    var i = 0
    while (i < line.length) {
        when {
            line.startsWith("<!--", i) -> {
                val end = line.indexOf("-->", i)
                val cEnd = if (end != -1) end + 3 else line.length
                pushStyle(SpanStyle(color = SyntaxComment))
                append(line.substring(i, cEnd))
                pop()
                i = cEnd
            }
            line[i] == '<' -> {
                val nextSpace = line.indexOfAny(charArrayOf(' ', '>', '/'), i + 1)
                val tagEnd = if (nextSpace != -1) nextSpace else line.length
                pushStyle(SpanStyle(color = SyntaxTag, fontWeight = FontWeight.Bold))
                append(line.substring(i, tagEnd))
                pop()
                i = tagEnd
            }
            line[i] == '"' || line[i] == '\'' -> {
                val quote = line[i]
                val close = line.indexOf(quote, i + 1)
                val strEnd = if (close != -1) close + 1 else line.length
                pushStyle(SpanStyle(color = SyntaxString))
                append(line.substring(i, strEnd))
                pop()
                i = strEnd
            }
            else -> {
                pushStyle(SpanStyle(color = PuterTextPrimary))
                append(line[i].toString())
                pop()
                i++
            }
        }
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.highlightCssLine(line: String) {
    if (line.trim().startsWith("/*")) {
        pushStyle(SpanStyle(color = SyntaxComment))
        append(line)
        pop()
    } else if (line.contains(":")) {
        val parts = line.split(":", limit = 2)
        pushStyle(SpanStyle(color = SyntaxAttribute))
        append(parts[0])
        pop()
        pushStyle(SpanStyle(color = PuterTextPrimary))
        append(":")
        pop()
        pushStyle(SpanStyle(color = SyntaxString))
        append(parts[1])
        pop()
    } else {
        pushStyle(SpanStyle(color = SyntaxTag))
        append(line)
        pop()
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.highlightJsLine(line: String) {
    if (line.trim().startsWith("//")) {
        pushStyle(SpanStyle(color = SyntaxComment))
        append(line)
        pop()
        return
    }

    val tokens = line.split(Regex("(?<=\\s)|(?=\\s)|(?<=[(),;{}])|(?=[(),;{}])"))
    val keywords = setOf("function", "const", "let", "var", "return", "if", "else", "for", "while", "async", "await", "import", "export", "class", "new", "true", "false", "null", "undefined")

    tokens.forEach { token ->
        when {
            keywords.contains(token.trim()) -> {
                pushStyle(SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.Bold))
                append(token)
                pop()
            }
            token.trim().startsWith("\"") || token.trim().startsWith("'") || token.trim().startsWith("`") -> {
                pushStyle(SpanStyle(color = SyntaxString))
                append(token)
                pop()
            }
            token.trim().matches(Regex("""[a-zA-Z_]\w*(?=\()""")) -> {
                pushStyle(SpanStyle(color = SyntaxFunction))
                append(token)
                pop()
            }
            else -> {
                pushStyle(SpanStyle(color = PuterTextPrimary))
                append(token)
                pop()
            }
        }
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.highlightJsonLine(line: String) {
    if (line.contains(":")) {
        val parts = line.split(":", limit = 2)
        pushStyle(SpanStyle(color = SyntaxTag))
        append(parts[0])
        pop()
        pushStyle(SpanStyle(color = PuterTextPrimary))
        append(":")
        pop()
        pushStyle(SpanStyle(color = SyntaxString))
        append(parts[1])
        pop()
    } else {
        pushStyle(SpanStyle(color = PuterTextPrimary))
        append(line)
        pop()
    }
}
