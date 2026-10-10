package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adblock.AdBlockEngine
import com.example.model.BrowserTab
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import kotlin.math.abs
import kotlin.math.roundToInt

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    isVanishMode: Boolean,
    pendingNavigationUrl: String?,
    pendingScript: String?,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String, String?) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onNavigationStateChanged: (Boolean, Boolean) -> Unit,
    onBlockedItem: (Boolean) -> Unit,
    onUrlNavigated: () -> Unit,
    onScriptExecuted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Gesture indicator states
    var dragHorizontalOffset by remember { mutableFloatStateOf(0f) }
    var isDraggingFromLeftEdge by remember { mutableStateOf(false) }
    var isDraggingFromRightEdge by remember { mutableStateOf(false) }

    val swipeThresholdPx = 140f

    // Hardware back button navigation
    BackHandler(enabled = tab.canGoBack) {
        webViewRef?.let {
            if (it.canGoBack()) {
                it.goBack()
            }
        }
    }

    // React to pending navigation URL
    LaunchedEffect(pendingNavigationUrl) {
        pendingNavigationUrl?.let { url ->
            webViewRef?.loadUrl(url)
            onUrlNavigated()
        }
    }

    // React to pending JavaScript execution
    LaunchedEffect(pendingScript) {
        pendingScript?.let { script ->
            webViewRef?.evaluateJavascript(script, null)
            onScriptExecuted()
        }
    }

    // React to Desktop Mode changes
    LaunchedEffect(tab.isDesktopMode) {
        webViewRef?.settings?.let { settings ->
            if (tab.isDesktopMode) {
                settings.userAgentString = DESKTOP_USER_AGENT
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
            } else {
                settings.userAgentString = null
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
            }
        }
    }

    // React to Vanish / Incognito Mode privacy settings
    LaunchedEffect(isVanishMode) {
        val cookieManager = CookieManager.getInstance()
        if (isVanishMode) {
            cookieManager.setAcceptCookie(false)
        } else {
            cookieManager.setAcceptCookie(true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("browser_webview_container")
            .pointerInput(tab.canGoBack, tab.canGoForward) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        val screenWidth = size.width
                        // Check if swipe initiated near screen edges
                        isDraggingFromLeftEdge = offset.x < 120f && tab.canGoBack
                        isDraggingFromRightEdge = offset.x > (screenWidth - 120f) && tab.canGoForward
                        dragHorizontalOffset = 0f
                    },
                    onDragEnd = {
                        if (isDraggingFromLeftEdge && dragHorizontalOffset > swipeThresholdPx) {
                            webViewRef?.goBack()
                        } else if (isDraggingFromRightEdge && dragHorizontalOffset < -swipeThresholdPx) {
                            webViewRef?.goForward()
                        }
                        dragHorizontalOffset = 0f
                        isDraggingFromLeftEdge = false
                        isDraggingFromRightEdge = false
                    },
                    onDragCancel = {
                        dragHorizontalOffset = 0f
                        isDraggingFromLeftEdge = false
                        isDraggingFromRightEdge = false
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        if (isDraggingFromLeftEdge || isDraggingFromRightEdge) {
                            dragHorizontalOffset += dragAmount
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Optimize WebView settings
                    configureSecureWebView(this, isVanishMode, tab.isDesktopMode)

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            onProgressChanged(newProgress)
                        }

                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            onNavigationStateChanged(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val url = request?.url?.toString() ?: return null

                            if (tab.isAdBlockEnabled && AdBlockEngine.isAdOrTracker(url)) {
                                val isTracker = AdBlockEngine.isTrackerOnly(url)
                                onBlockedItem(isTracker)
                                return AdBlockEngine.createBlockedResponse()
                            }

                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            url?.let {
                                onPageStarted(it)
                                // Inject cosmetic CSS filter early
                                if (tab.isAdBlockEnabled) {
                                    view?.evaluateJavascript(AdBlockEngine.getCosmeticFilterScript(), null)
                                }
                            }
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            url?.let {
                                onPageFinished(it, view?.title)
                                onNavigationStateChanged(view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                                // Inject cosmetic CSS filter on finish
                                if (tab.isAdBlockEnabled) {
                                    view?.evaluateJavascript(AdBlockEngine.getCosmeticFilterScript(), null)
                                }
                                if (tab.isEditMode) {
                                    view?.evaluateJavascript(com.example.prank.WebEditorScript.getEnableEditingScript(), null)
                                }
                            }
                        }
                    }

                    if (!tab.isBlank) {
                        loadUrl(tab.url)
                    }
                    webViewRef = this
                }
            },
            update = { webView ->
                webViewRef = webView
                configureSecureWebView(webView, isVanishMode, tab.isDesktopMode)
            },
            modifier = Modifier.fillMaxSize()
        )

        // Smooth Edge Swipe Nav Indicator (Left Edge -> Back)
        AnimatedVisibility(
            visible = isDraggingFromLeftEdge && dragHorizontalOffset > 30f,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(x = dragHorizontalOffset.coerceAtMost(160f).roundToInt(), y = 0) }
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (isVanishMode) GhostPurpleLight else CyberCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Swipe Back",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Smooth Edge Swipe Nav Indicator (Right Edge -> Forward)
        AnimatedVisibility(
            visible = isDraggingFromRightEdge && dragHorizontalOffset < -30f,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset { IntOffset(x = dragHorizontalOffset.coerceAtLeast(-160f).roundToInt(), y = 0) }
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (isVanishMode) GhostPurpleLight else CyberCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Swipe Forward",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.destroy()
            webViewRef = null
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun configureSecureWebView(webView: WebView, isVanish: Boolean, isDesktop: Boolean) {
    val settings = webView.settings
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = !isVanish
    settings.databaseEnabled = !isVanish
    settings.cacheMode = if (isVanish) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT

    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW

    settings.builtInZoomControls = true
    settings.displayZoomControls = false
    settings.useWideViewPort = true
    settings.loadWithOverviewMode = true

    if (isDesktop) {
        settings.userAgentString = DESKTOP_USER_AGENT
    } else {
        settings.userAgentString = null
    }

    // Hardware acceleration
    webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
}
