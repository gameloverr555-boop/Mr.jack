package com.example.model

import java.util.UUID

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val dateAdded: Long = System.currentTimeMillis()
)

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isIncognito: Boolean = false
)

data class QuickDialSite(
    val title: String,
    val url: String,
    val category: String,
    val iconEmoji: String,
    val badge: String? = null
)
