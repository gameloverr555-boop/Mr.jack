package com.example.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.model.VpnStatus
import com.example.ui.components.AddressBar
import com.example.ui.components.AdBlockSheet
import com.example.ui.components.BookmarksHistoryDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.BrowserWebView
import com.example.ui.components.HomeSpeedDial
import com.example.ui.components.PrankToolbar
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.TabOverviewSheet
import com.example.ui.components.VanishPurgeAnimation
import com.example.ui.components.VpnBottomSheet
import com.example.ui.theme.GhostShieldTheme
import com.example.ui.theme.StealthBlack
import com.example.ui.theme.VanishDark
import com.example.viewmodel.BrowserViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostShieldApp(
    viewModel: BrowserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current

    val isVanishMode by viewModel.isVanishMode.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val normalTabs by viewModel.normalTabs.collectAsState()
    val vanishTabs by viewModel.vanishTabs.collectAsState()

    val vpnStatus by viewModel.vpnStatus.collectAsState()
    val selectedVpnServer by viewModel.selectedVpnServer.collectAsState()
    val vpnStats by viewModel.vpnStats.collectAsState()
    val adBlockStats by viewModel.adBlockStats.collectAsState()

    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()

    val isVpnSheetOpen by viewModel.isVpnSheetOpen.collectAsState()
    val isAdBlockSheetOpen by viewModel.isAdBlockSheetOpen.collectAsState()
    val isTabOverviewOpen by viewModel.isTabOverviewOpen.collectAsState()
    val isBookmarksHistoryOpen by viewModel.isBookmarksHistoryOpen.collectAsState()
    val isQrScannerOpen by viewModel.isQrScannerOpen.collectAsState()
    val isQrGeneratorOpen by viewModel.isQrGeneratorOpen.collectAsState()
    val showVanishPurgeAnimation by viewModel.showVanishPurgeAnimation.collectAsState()

    val pendingNavigationUrl by viewModel.pendingNavigationUrl.collectAsState()
    val pendingScript by viewModel.pendingJsScript.collectAsState()

    // Voice Search Speech Recognizer Intent Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.loadUrl(spokenText)
            }
        }
    }

    val isCurrentBookmarked = remember(bookmarks, activeTab.url) {
        bookmarks.any { it.url == activeTab.url }
    }

    GhostShieldTheme(isVanishMode = isVanishMode) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ghost_shield_root"),
            containerColor = if (isVanishMode) VanishDark else StealthBlack,
            topBar = {
                AddressBar(
                    tab = activeTab,
                    isVanishMode = isVanishMode,
                    vpnStatus = vpnStatus,
                    selectedVpnServer = selectedVpnServer,
                    tabCount = if (isVanishMode) vanishTabs.size else normalTabs.size,
                    onUrlSubmit = { viewModel.loadUrl(it) },
                    onReload = { viewModel.reloadCurrentTab() },
                    onOpenVpnSheet = { viewModel.isVpnSheetOpen.value = true },
                    onOpenAdBlockSheet = { viewModel.isAdBlockSheetOpen.value = true },
                    onOpenTabOverview = { viewModel.isTabOverviewOpen.value = true },
                    onOpenQrScanner = { viewModel.isQrScannerOpen.value = true },
                    onOpenQrGenerator = { viewModel.isQrGeneratorOpen.value = true },
                    onStartVoiceSearch = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search securely...")
                            }
                            speechRecognizerLauncher.launch(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Voice search is unavailable on this device", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onToggleVanishMode = { viewModel.toggleVanishMode() },
                    onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                    onToggleEditMode = { viewModel.toggleEditPrankMode() },
                    onOpenBookmarksHistory = { viewModel.isBookmarksHistoryOpen.value = true },
                    onVanishPurgeNow = { viewModel.vanishAndPurgeAll(context) },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                BottomNavBar(
                    tab = activeTab,
                    isVanishMode = isVanishMode,
                    isBookmarked = isCurrentBookmarked,
                    onBack = { viewModel.executeScript("window.history.back();") },
                    onForward = { viewModel.executeScript("window.history.forward();") },
                    onHome = { viewModel.createNewTab("about:blank") },
                    onBookmarkToggle = { viewModel.toggleBookmark(activeTab) },
                    onToggleVanishMode = { viewModel.toggleVanishMode() },
                    onToggleEditMode = { viewModel.toggleEditPrankMode() }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // If current tab is home / speed dial
                if (activeTab.isBlank) {
                    HomeSpeedDial(
                        isVanishMode = isVanishMode,
                        vpnStatus = vpnStatus,
                        selectedVpnServer = selectedVpnServer,
                        adBlockStats = adBlockStats,
                        quickDials = viewModel.quickDialSites,
                        onSelectSite = { viewModel.loadUrl(it) },
                        onOpenVpn = { viewModel.isVpnSheetOpen.value = true },
                        onOpenAdBlock = { viewModel.isAdBlockSheetOpen.value = true },
                        onOpenQrScanner = { viewModel.isQrScannerOpen.value = true },
                        onStartVoiceSearch = {
                            try {
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
                                }
                                speechRecognizerLauncher.launch(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Voice search unavailable", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onToggleVanishMode = { viewModel.toggleVanishMode() }
                    )
                } else {
                    // Optimized WebView with Gesture Swipe Navigation
                    BrowserWebView(
                        tab = activeTab,
                        isVanishMode = isVanishMode,
                        pendingNavigationUrl = pendingNavigationUrl,
                        pendingScript = pendingScript,
                        onPageStarted = { url -> viewModel.onPageStarted(url) },
                        onPageFinished = { url, title -> viewModel.onPageFinished(url, title) },
                        onProgressChanged = { progress -> viewModel.onProgressChanged(progress) },
                        onNavigationStateChanged = { canBack, canForward -> viewModel.onNavigationStateChanged(canBack, canForward) },
                        onBlockedItem = { isTracker -> viewModel.recordBlockedItem(isTracker) },
                        onUrlNavigated = { viewModel.onUrlNavigated() },
                        onScriptExecuted = { viewModel.clearPendingScript() }
                    )
                }

                // Webpage Content Editor Floating Prank Toolbar
                PrankToolbar(
                    isVisible = activeTab.isEditMode,
                    onBillionairePrank = { viewModel.runPrankBillionaire() },
                    onMatrixPrank = { viewModel.runPrankMatrix() },
                    onStampPrank = { stamp -> viewModel.runPrankStamp(stamp) },
                    onHeadlinePrank = { headline -> viewModel.runPrankHeadline(headline) },
                    onCloseEditMode = { viewModel.toggleEditPrankMode() }
                )
            }
        }

        // VPN Bottom Sheet
        if (isVpnSheetOpen) {
            VpnBottomSheet(
                vpnStatus = vpnStatus,
                selectedServer = selectedVpnServer,
                servers = viewModel.availableVpnServers,
                stats = vpnStats,
                onToggleVpn = { viewModel.toggleVpn() },
                onSelectServer = { viewModel.selectVpnServer(it) },
                onToggleKillSwitch = { viewModel.toggleVpnKillSwitch() },
                onToggleDnsProtection = { viewModel.toggleVpnDnsProtection() },
                onDismiss = { viewModel.isVpnSheetOpen.value = false }
            )
        }

        // AdBlocker Bottom Sheet
        if (isAdBlockSheetOpen) {
            AdBlockSheet(
                tab = activeTab,
                globalStats = adBlockStats,
                onToggleTabAdBlock = { viewModel.toggleAdBlockForCurrentTab() },
                onToggleGlobalShield = { viewModel.toggleGlobalAdBlockShield() },
                onDismiss = { viewModel.isAdBlockSheetOpen.value = false }
            )
        }

        // Tab Overview Dialog
        if (isTabOverviewOpen) {
            TabOverviewSheet(
                normalTabs = normalTabs,
                vanishTabs = vanishTabs,
                activeTabId = activeTab.id,
                isCurrentVanish = isVanishMode,
                onSelectTab = { id, isVanish ->
                    if (isVanishMode != isVanish) {
                        viewModel.toggleVanishMode()
                    }
                    viewModel.selectTab(id)
                },
                onCloseTab = { id, _ -> viewModel.closeTab(id) },
                onNewTab = { isVanish ->
                    if (isVanishMode != isVanish) {
                        viewModel.toggleVanishMode()
                    }
                    viewModel.createNewTab("about:blank")
                },
                onCloseAll = { isVanish -> viewModel.closeAllTabs(isVanish) },
                onDismiss = { viewModel.isTabOverviewOpen.value = false }
            )
        }

        // Bookmarks & History Dialog
        if (isBookmarksHistoryOpen) {
            BookmarksHistoryDialog(
                bookmarks = bookmarks,
                history = history,
                onOpenUrl = { viewModel.loadUrl(it) },
                onRemoveBookmark = { viewModel.removeBookmark(it) },
                onClearHistory = { viewModel.clearHistory() },
                onDismiss = { viewModel.isBookmarksHistoryOpen.value = false }
            )
        }

        // QR Code Scanner / Generator Dialog
        if (isQrScannerOpen) {
            QrScannerDialog(
                initialTab = 0,
                currentPageUrl = activeTab.url,
                onQrScanned = { scannedUrl -> viewModel.loadUrl(scannedUrl) },
                onDismiss = { viewModel.isQrScannerOpen.value = false }
            )
        }

        if (isQrGeneratorOpen) {
            QrScannerDialog(
                initialTab = 1,
                currentPageUrl = activeTab.url,
                onQrScanned = { scannedUrl -> viewModel.loadUrl(scannedUrl) },
                onDismiss = { viewModel.isQrGeneratorOpen.value = false }
            )
        }

        // Vanish Mode Purge & Shredder Animation
        VanishPurgeAnimation(
            isVisible = showVanishPurgeAnimation
        )
    }
}
