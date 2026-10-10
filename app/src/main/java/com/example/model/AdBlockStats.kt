package com.example.model

data class AdBlockGlobalStats(
    val totalAdsBlocked: Int = 1428,
    val totalTrackersBlocked: Int = 3892,
    val estimatedDataSavedMb: Float = 64.5f,
    val estimatedTimeSavedSeconds: Int = 420,
    val isShieldEnabled: Boolean = true,
    val blockMalware: Boolean = true,
    val blockFingerprinting: Boolean = true,
    val blockPopups: Boolean = true,
    val cosmeticFiltering: Boolean = true
)
