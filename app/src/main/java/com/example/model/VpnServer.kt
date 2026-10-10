package com.example.model

data class VpnServer(
    val id: String,
    val country: String,
    val city: String,
    val flag: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercentage: Int,
    val protocol: String = "WireGuard+Ghost",
    val featureBadge: String = "Zero-Logs",
    val isRecommended: Boolean = false
)

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class VpnSessionStats(
    val connectedSince: Long = 0L,
    val uploadBytes: Long = 0L,
    val downloadBytes: Long = 0L,
    val currentUploadSpeedKbps: Float = 0f,
    val currentDownloadSpeedKbps: Float = 0f,
    val dnsProtectionActive: Boolean = true,
    val killSwitchActive: Boolean = true
)
