package com.example.vpn

import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.model.VpnStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class MockVpnManager(private val scope: CoroutineScope) {

    val availableServers = listOf(
        VpnServer(
            id = "ch-zurich",
            country = "Switzerland",
            city = "Zurich",
            flag = "🇨🇭",
            ipAddress = "185.220.101.4",
            pingMs = 18,
            loadPercentage = 28,
            protocol = "WireGuard+Ghost",
            featureBadge = "Zero-Logs (Audited)",
            isRecommended = true
        ),
        VpnServer(
            id = "is-reykjavik",
            country = "Iceland",
            city = "Reykjavik",
            flag = "🇮🇸",
            ipAddress = "194.187.249.12",
            pingMs = 24,
            loadPercentage = 31,
            protocol = "Quantum-Armor",
            featureBadge = "Strict Privacy Haven",
            isRecommended = true
        ),
        VpnServer(
            id = "se-stockholm",
            country = "Sweden",
            city = "Stockholm",
            flag = "🇸🇪",
            ipAddress = "193.180.119.88",
            pingMs = 29,
            loadPercentage = 42,
            protocol = "WireGuard Stealth",
            featureBadge = "RAM-Only Diskless"
        ),
        VpnServer(
            id = "nl-amsterdam",
            country = "Netherlands",
            city = "Amsterdam",
            flag = "🇳🇱",
            ipAddress = "195.206.105.14",
            pingMs = 22,
            loadPercentage = 54,
            protocol = "WireGuard Stealth",
            featureBadge = "High Bandwidth 10Gbps"
        ),
        VpnServer(
            id = "de-frankfurt",
            country = "Germany",
            city = "Frankfurt",
            flag = "🇩🇪",
            ipAddress = "46.165.245.152",
            pingMs = 26,
            loadPercentage = 49,
            protocol = "OpenVPN Scramble",
            featureBadge = "GDPR Shield"
        ),
        VpnServer(
            id = "jp-tokyo",
            country = "Japan",
            city = "Tokyo",
            flag = "🇯🇵",
            ipAddress = "103.152.220.5",
            pingMs = 68,
            loadPercentage = 38,
            protocol = "WireGuard+Ghost",
            featureBadge = "Asia Fast Node"
        ),
        VpnServer(
            id = "sg-singapore",
            country = "Singapore",
            city = "Singapore",
            flag = "🇸🇬",
            ipAddress = "139.99.125.77",
            pingMs = 74,
            loadPercentage = 45,
            protocol = "IKEv2 Armor",
            featureBadge = "Asia Hub"
        ),
        VpnServer(
            id = "us-ny",
            country = "United States",
            city = "New York",
            flag = "🇺🇸",
            ipAddress = "198.98.56.12",
            pingMs = 35,
            loadPercentage = 62,
            protocol = "WireGuard Stealth",
            featureBadge = "Streaming Optimized"
        )
    )

    private val _selectedServer = MutableStateFlow(availableServers.first())
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _vpnStatus = MutableStateFlow(VpnStatus.DISCONNECTED)
    val vpnStatus: StateFlow<VpnStatus> = _vpnStatus.asStateFlow()

    private val _stats = MutableStateFlow(VpnSessionStats())
    val stats: StateFlow<VpnSessionStats> = _stats.asStateFlow()

    private var simulationJob: Job? = null

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        if (_vpnStatus.value == VpnStatus.CONNECTED) {
            // Reconnect smoothly to new node
            reconnect()
        }
    }

    fun toggleVpn() {
        when (_vpnStatus.value) {
            VpnStatus.DISCONNECTED -> connect()
            VpnStatus.CONNECTED -> disconnect()
            VpnStatus.CONNECTING -> disconnect()
            VpnStatus.DISCONNECTING -> {}
        }
    }

    fun connect() {
        scope.launch(Dispatchers.Default) {
            _vpnStatus.value = VpnStatus.CONNECTING
            delay(1200) // Simulated cryptographic handshake & key exchange
            _vpnStatus.value = VpnStatus.CONNECTED
            _stats.value = _stats.value.copy(
                connectedSince = System.currentTimeMillis(),
                uploadBytes = 0L,
                downloadBytes = 0L
            )
            startTelemetrySimulation()
        }
    }

    fun disconnect() {
        simulationJob?.cancel()
        simulationJob = null
        scope.launch(Dispatchers.Default) {
            _vpnStatus.value = VpnStatus.DISCONNECTING
            delay(500)
            _vpnStatus.value = VpnStatus.DISCONNECTED
            _stats.value = _stats.value.copy(
                currentDownloadSpeedKbps = 0f,
                currentUploadSpeedKbps = 0f
            )
        }
    }

    private fun reconnect() {
        scope.launch(Dispatchers.Default) {
            _vpnStatus.value = VpnStatus.CONNECTING
            delay(800)
            _vpnStatus.value = VpnStatus.CONNECTED
        }
    }

    fun toggleKillSwitch() {
        _stats.value = _stats.value.copy(killSwitchActive = !_stats.value.killSwitchActive)
    }

    fun toggleDnsProtection() {
        _stats.value = _stats.value.copy(dnsProtectionActive = !_stats.value.dnsProtectionActive)
    }

    private fun startTelemetrySimulation() {
        simulationJob?.cancel()
        simulationJob = scope.launch(Dispatchers.Default) {
            while (isActive && _vpnStatus.value == VpnStatus.CONNECTED) {
                delay(1000)
                val downSpeed = Random.nextDouble(1200.0, 8500.0).toFloat()
                val upSpeed = Random.nextDouble(200.0, 1800.0).toFloat()
                val addedDown = (downSpeed * 1024 / 8).toLong()
                val addedUp = (upSpeed * 1024 / 8).toLong()

                _stats.value = _stats.value.copy(
                    currentDownloadSpeedKbps = downSpeed,
                    currentUploadSpeedKbps = upSpeed,
                    downloadBytes = _stats.value.downloadBytes + addedDown,
                    uploadBytes = _stats.value.uploadBytes + addedUp
                )
            }
        }
    }
}
