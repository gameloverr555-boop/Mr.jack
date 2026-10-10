package com.example.model

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "about:blank",
    val favicon: String? = null,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false,
    val isEditMode: Boolean = false,
    val isAdBlockEnabled: Boolean = true,
    val adsBlockedOnPage: Int = 0,
    val trackersBlockedOnPage: Int = 0,
    val isSecureSsl: Boolean = false,
    val lastVisitedTimestamp: Long = System.currentTimeMillis()
) {
    val isBlank: Boolean
        get() = url == "about:blank" || url.isEmpty()

    val displayTitle: String
        get() = if (isBlank) (if (isIncognito) "Vanish Tab" else "GhostShield Home") else title.ifEmpty { url }

    val displayHost: String
        get() = if (isBlank) "Home" else try {
            val uri = java.net.URI(url)
            uri.host ?: url
        } catch (_: Exception) {
            url
        }
}
