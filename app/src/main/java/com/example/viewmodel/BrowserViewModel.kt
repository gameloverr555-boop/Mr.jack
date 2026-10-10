package com.example.viewmodel

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adblock.AdBlockEngine
import com.example.model.AdBlockGlobalStats
import com.example.model.Bookmark
import com.example.model.BrowserTab
import com.example.model.HistoryItem
import com.example.model.QuickDialSite
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.model.VpnStatus
import com.example.vpn.MockVpnManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URI
import java.util.Locale

class BrowserViewModel : ViewModel() {

    private val vpnManager = MockVpnManager(viewModelScope)

    val vpnStatus: StateFlow<VpnStatus> = vpnManager.vpnStatus
    val selectedVpnServer: StateFlow<VpnServer> = vpnManager.selectedServer
    val vpnStats: StateFlow<VpnSessionStats> = vpnManager.stats
    val availableVpnServers: List<VpnServer> = vpnManager.availableServers

    private val _isVanishMode = MutableStateFlow(false)
    val isVanishMode: StateFlow<Boolean> = _isVanishMode.asStateFlow()

    private val _normalTabs = MutableStateFlow<List<BrowserTab>>(
        listOf(
            BrowserTab(
                title = "GhostShield Home",
                url = "about:blank",
                isIncognito = false
            )
        )
    )
    val normalTabs: StateFlow<List<BrowserTab>> = _normalTabs.asStateFlow()

    private val _vanishTabs = MutableStateFlow<List<BrowserTab>>(
        listOf(
            BrowserTab(
                title = "Vanish Private Tab",
                url = "about:blank",
                isIncognito = true
            )
        )
    )
    val vanishTabs: StateFlow<List<BrowserTab>> = _vanishTabs.asStateFlow()

    private val _activeNormalTabId = MutableStateFlow(_normalTabs.value.first().id)
    private val _activeVanishTabId = MutableStateFlow(_vanishTabs.value.first().id)

    val activeTab: StateFlow<BrowserTab> = MutableStateFlow(
        _normalTabs.value.first()
    ).also { flow ->
        viewModelScope.launch {
            launch {
                _isVanishMode.collect { vanish ->
                    updateActiveTabFlow(flow, vanish)
                }
            }
            launch {
                _normalTabs.collect {
                    if (!_isVanishMode.value) updateActiveTabFlow(flow, false)
                }
            }
            launch {
                _vanishTabs.collect {
                    if (_isVanishMode.value) updateActiveTabFlow(flow, true)
                }
            }
            launch {
                _activeNormalTabId.collect {
                    if (!_isVanishMode.value) updateActiveTabFlow(flow, false)
                }
            }
            launch {
                _activeVanishTabId.collect {
                    if (_isVanishMode.value) updateActiveTabFlow(flow, true)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, _normalTabs.value.first())

    private fun updateActiveTabFlow(flow: MutableStateFlow<BrowserTab>, vanish: Boolean) {
        if (vanish) {
            val id = _activeVanishTabId.value
            val tab = _vanishTabs.value.find { it.id == id } ?: _vanishTabs.value.firstOrNull()
            if (tab != null) flow.value = tab
        } else {
            val id = _activeNormalTabId.value
            val tab = _normalTabs.value.find { it.id == id } ?: _normalTabs.value.firstOrNull()
            if (tab != null) flow.value = tab
        }
    }

    // AdBlock Stats
    private val _adBlockStats = MutableStateFlow(AdBlockGlobalStats())
    val adBlockStats: StateFlow<AdBlockGlobalStats> = _adBlockStats.asStateFlow()

    // History and Bookmarks
    private val _bookmarks = MutableStateFlow<List<Bookmark>>(
        listOf(
            Bookmark(title = "DuckDuckGo Privacy Search", url = "https://duckduckgo.com"),
            Bookmark(title = "Electronic Frontier Foundation (EFF)", url = "https://www.eff.org"),
            Bookmark(title = "Privacy Guides", url = "https://www.privacyguides.org"),
            Bookmark(title = "Hacker News", url = "https://news.ycombinator.com"),
            Bookmark(title = "Wikipedia", url = "https://en.wikipedia.org")
        )
    )
    val bookmarks: StateFlow<List<Bookmark>> = _bookmarks.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    val quickDialSites = listOf(
        QuickDialSite("DuckDuckGo", "https://duckduckgo.com", "Privacy Search", "🦆", "Default"),
        QuickDialSite("Wikipedia", "https://en.wikipedia.org", "Knowledge", "🌐"),
        QuickDialSite("Reddit", "https://reddit.com", "Community", "🤖"),
        QuickDialSite("GitHub", "https://github.com", "Developers", "🐙"),
        QuickDialSite("Hacker News", "https://news.ycombinator.com", "Tech News", "⚡"),
        QuickDialSite("Brave Search", "https://search.brave.com", "Private Index", "🦁"),
        QuickDialSite("BBC News", "https://www.bbc.com/news", "World News", "📰"),
        QuickDialSite("Internet Archive", "https://archive.org", "Wayback Machine", "🏛️")
    )

    // UI Dialog & Sheet states
    val isVpnSheetOpen = MutableStateFlow(false)
    val isAdBlockSheetOpen = MutableStateFlow(false)
    val isTabOverviewOpen = MutableStateFlow(false)
    val isBookmarksHistoryOpen = MutableStateFlow(false)
    val isQrScannerOpen = MutableStateFlow(false)
    val isQrGeneratorOpen = MutableStateFlow(false)
    val showVanishPurgeAnimation = MutableStateFlow(false)

    // Action pending execution on active WebView
    val pendingJsScript = MutableStateFlow<String?>(null)
    val pendingNavigationUrl = MutableStateFlow<String?>(null)

    fun toggleVanishMode() {
        val newMode = !_isVanishMode.value
        _isVanishMode.value = newMode
    }

    fun toggleVpn() = vpnManager.toggleVpn()
    fun selectVpnServer(server: VpnServer) = vpnManager.selectServer(server)
    fun toggleVpnKillSwitch() = vpnManager.toggleKillSwitch()
    fun toggleVpnDnsProtection() = vpnManager.toggleDnsProtection()

    fun toggleAdBlockForCurrentTab() {
        val current = activeTab.value
        updateTab(current.copy(isAdBlockEnabled = !current.isAdBlockEnabled))
    }

    fun toggleGlobalAdBlockShield() {
        _adBlockStats.value = _adBlockStats.value.copy(
            isShieldEnabled = !_adBlockStats.value.isShieldEnabled
        )
    }

    fun toggleDesktopMode() {
        val current = activeTab.value
        val newDesktop = !current.isDesktopMode
        updateTab(current.copy(isDesktopMode = newDesktop))
        reloadCurrentTab()
    }

    fun toggleEditPrankMode() {
        val current = activeTab.value
        val newEditMode = !current.isEditMode
        updateTab(current.copy(isEditMode = newEditMode))
        if (newEditMode) {
            executeScript(com.example.prank.WebEditorScript.getEnableEditingScript())
        } else {
            executeScript(com.example.prank.WebEditorScript.getDisableEditingScript())
        }
    }

    fun runPrankBillionaire() {
        executeScript(com.example.prank.WebEditorScript.getBillionairePrankScript())
    }

    fun runPrankMatrix() {
        executeScript(com.example.prank.WebEditorScript.getMatrixThemePrankScript())
    }

    fun runPrankStamp(text: String = "TOP SECRET") {
        executeScript(com.example.prank.WebEditorScript.getStampPrankScript(text))
    }

    fun runPrankHeadline(text: String) {
        executeScript(com.example.prank.WebEditorScript.getReplaceHeadlineScript(text))
    }

    fun executeScript(script: String) {
        pendingJsScript.value = script
    }

    fun clearPendingScript() {
        pendingJsScript.value = null
    }

    fun loadUrl(input: String) {
        val formatted = formatInputToUrl(input)
        updateTab(activeTab.value.copy(url = formatted, isEditMode = false))
        pendingNavigationUrl.value = formatted
    }

    fun reloadCurrentTab() {
        val current = activeTab.value
        if (!current.isBlank) {
            pendingNavigationUrl.value = current.url
        }
    }

    fun onUrlNavigated() {
        pendingNavigationUrl.value = null
    }

    fun formatInputToUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return "about:blank"
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("about:")) {
            return trimmed
        }
        val isDomainLike = trimmed.contains(".") && !trimmed.contains(" ") && trimmed.indexOf(".") > 0
        return if (isDomainLike) {
            "https://$trimmed"
        } else {
            "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }
    }

    fun createNewTab(url: String = "about:blank") {
        val isVanish = _isVanishMode.value
        val newTab = BrowserTab(
            title = if (url == "about:blank") (if (isVanish) "Vanish Tab" else "GhostShield Home") else "Loading...",
            url = url,
            isIncognito = isVanish
        )
        if (isVanish) {
            _vanishTabs.value = _vanishTabs.value + newTab
            _activeVanishTabId.value = newTab.id
        } else {
            _normalTabs.value = _normalTabs.value + newTab
            _activeNormalTabId.value = newTab.id
        }
        if (url != "about:blank") {
            pendingNavigationUrl.value = url
        }
    }

    fun selectTab(id: String) {
        if (_isVanishMode.value) {
            _activeVanishTabId.value = id
        } else {
            _activeNormalTabId.value = id
        }
    }

    fun closeTab(id: String) {
        if (_isVanishMode.value) {
            val list = _vanishTabs.value.filter { it.id != id }
            if (list.isEmpty()) {
                val fresh = BrowserTab(title = "Vanish Tab", url = "about:blank", isIncognito = true)
                _vanishTabs.value = listOf(fresh)
                _activeVanishTabId.value = fresh.id
            } else {
                _vanishTabs.value = list
                if (_activeVanishTabId.value == id) {
                    _activeVanishTabId.value = list.last().id
                }
            }
        } else {
            val list = _normalTabs.value.filter { it.id != id }
            if (list.isEmpty()) {
                val fresh = BrowserTab(title = "GhostShield Home", url = "about:blank", isIncognito = false)
                _normalTabs.value = listOf(fresh)
                _activeNormalTabId.value = fresh.id
            } else {
                _normalTabs.value = list
                if (_activeNormalTabId.value == id) {
                    _activeNormalTabId.value = list.last().id
                }
            }
        }
    }

    fun closeAllTabs(isVanishOnly: Boolean) {
        if (isVanishOnly) {
            val fresh = BrowserTab(title = "Vanish Tab", url = "about:blank", isIncognito = true)
            _vanishTabs.value = listOf(fresh)
            _activeVanishTabId.value = fresh.id
        } else {
            val fresh = BrowserTab(title = "GhostShield Home", url = "about:blank", isIncognito = false)
            _normalTabs.value = listOf(fresh)
            _activeNormalTabId.value = fresh.id
        }
    }

    fun updateTab(tab: BrowserTab) {
        if (tab.isIncognito) {
            _vanishTabs.value = _vanishTabs.value.map { if (it.id == tab.id) tab else it }
        } else {
            _normalTabs.value = _normalTabs.value.map { if (it.id == tab.id) tab else it }
        }
    }

    fun onPageStarted(url: String) {
        val current = activeTab.value
        val isSsl = url.startsWith("https://")
        updateTab(
            current.copy(
                url = url,
                isLoading = true,
                progress = 10,
                isSecureSsl = isSsl,
                adsBlockedOnPage = 0,
                trackersBlockedOnPage = 0
            )
        )
    }

    fun onPageFinished(url: String, title: String?) {
        val current = activeTab.value
        val safeTitle = title?.takeIf { it.isNotBlank() } ?: current.displayHost
        updateTab(
            current.copy(
                url = url,
                title = safeTitle,
                isLoading = false,
                progress = 100,
                isSecureSsl = url.startsWith("https://")
            )
        )

        // Record history only if NOT incognito
        if (!current.isIncognito && url != "about:blank") {
            val historyItem = HistoryItem(
                title = safeTitle,
                url = url,
                isIncognito = false
            )
            _history.value = listOf(historyItem) + _history.value.take(199)
        }
    }

    fun onProgressChanged(newProgress: Int) {
        val current = activeTab.value
        updateTab(
            current.copy(
                progress = newProgress,
                isLoading = newProgress < 100
            )
        )
    }

    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        val current = activeTab.value
        updateTab(current.copy(canGoBack = canGoBack, canGoForward = canGoForward))
    }

    fun recordBlockedItem(isTracker: Boolean) {
        val current = activeTab.value
        if (isTracker) {
            updateTab(current.copy(trackersBlockedOnPage = current.trackersBlockedOnPage + 1))
            _adBlockStats.value = _adBlockStats.value.copy(
                totalTrackersBlocked = _adBlockStats.value.totalTrackersBlocked + 1,
                estimatedDataSavedMb = _adBlockStats.value.estimatedDataSavedMb + 0.04f
            )
        } else {
            updateTab(current.copy(adsBlockedOnPage = current.adsBlockedOnPage + 1))
            _adBlockStats.value = _adBlockStats.value.copy(
                totalAdsBlocked = _adBlockStats.value.totalAdsBlocked + 1,
                estimatedDataSavedMb = _adBlockStats.value.estimatedDataSavedMb + 0.12f,
                estimatedTimeSavedSeconds = _adBlockStats.value.estimatedTimeSavedSeconds + 1
            )
        }
    }

    fun toggleBookmark(tab: BrowserTab) {
        val existing = _bookmarks.value.find { it.url == tab.url }
        if (existing != null) {
            _bookmarks.value = _bookmarks.value.filter { it.id != existing.id }
        } else if (!tab.isBlank) {
            _bookmarks.value = listOf(
                Bookmark(title = tab.title.ifEmpty { tab.url }, url = tab.url)
            ) + _bookmarks.value
        }
    }

    fun removeBookmark(id: String) {
        _bookmarks.value = _bookmarks.value.filter { it.id != id }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    /**
     * Vanish / Incognito Mode Wipe:
     * Instantly purges all cookies, WebStorage, web cache, active vanish tabs, and session traces.
     */
    fun vanishAndPurgeAll(context: Context) {
        viewModelScope.launch {
            showVanishPurgeAnimation.value = true

            // Purge all cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            // Purge WebStorage
            WebStorage.getInstance().deleteAllData()

            // Reset vanish tabs
            val fresh = BrowserTab(
                title = "Vanish Private Tab",
                url = "about:blank",
                isIncognito = true
            )
            _vanishTabs.value = listOf(fresh)
            _activeVanishTabId.value = fresh.id

            kotlinx.coroutines.delay(1600)
            showVanishPurgeAnimation.value = false
        }
    }
}
