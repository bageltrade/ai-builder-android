package com.example.ui.preview

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

enum class ViewportMode(val title: String, val widthDp: Dp?) {
    PHONE("Mobile (375px)", 375.dp),
    TABLET("Tablet (768px)", 768.dp),
    RESPONSIVE("Full Width", null)
}

data class ConsoleLogItem(
    val level: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PuterAndroidBridge(
    private val onLog: (ConsoleLogItem) -> Unit
) {
    @JavascriptInterface
    fun logMessage(level: String, message: String) {
        onLog(ConsoleLogItem(level, message))
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PuterLivePreview(
    htmlContent: String,
    cssContent: String,
    jsContent: String,
    viewportMode: ViewportMode,
    reloadTrigger: Int,
    onConsoleLog: (ConsoleLogItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val bundledHtml = remember(htmlContent, cssContent, jsContent) {
        PuterHtmlBundler.buildBundledHtml(htmlContent, cssContent, jsContent)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val containerModifier = if (viewportMode.widthDp != null) {
            Modifier
                .width(viewportMode.widthDp)
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(12.dp))
        } else {
            Modifier.fillMaxSize()
        }

        AndroidView(
            modifier = containerModifier,
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        allowFileAccess = false
                        displayZoomControls = false
                    }

                    addJavascriptInterface(
                        PuterAndroidBridge(onConsoleLog),
                        "PuterAndroidBridge"
                    )

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            if (consoleMessage != null) {
                                val lvl = when (consoleMessage.messageLevel()) {
                                    ConsoleMessage.MessageLevel.ERROR -> "error"
                                    ConsoleMessage.MessageLevel.WARNING -> "warn"
                                    else -> "info"
                                }
                                onConsoleLog(
                                    ConsoleLogItem(
                                        level = lvl,
                                        message = consoleMessage.message()
                                    )
                                )
                            }
                            return super.onConsoleMessage(consoleMessage)
                        }
                    }

                    webViewClient = object : WebViewClient() {}
                    loadDataWithBaseURL(
                        "https://puter.site/",
                        bundledHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            update = { webView ->
                webView.loadDataWithBaseURL(
                    "https://puter.site/",
                    bundledHtml,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        )
    }
}
