package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnServer
import com.example.model.VpnSessionStats
import com.example.model.VpnStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.StealthBlack
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.StealthSurface
import com.example.ui.theme.StealthSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnBottomSheet(
    vpnStatus: VpnStatus,
    selectedServer: VpnServer,
    servers: List<VpnServer>,
    stats: VpnSessionStats,
    onToggleVpn: () -> Unit,
    onSelectServer: (VpnServer) -> Unit,
    onToggleKillSwitch: () -> Unit,
    onToggleDnsProtection: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StealthBlack,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .testTag("vpn_bottom_sheet"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VpnLock,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GhostShield Stealth VPN",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mock Encrypted Proxy Tunnel",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pulsing Power Toggle Button
            val isConnected = vpnStatus == VpnStatus.CONNECTED
            val isConnecting = vpnStatus == VpnStatus.CONNECTING

            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (isConnecting) 1.15f else if (isConnected) 1.05f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            val buttonBorderColor by animateColorAsState(
                targetValue = when {
                    isConnected -> NeonGreen
                    isConnecting -> CyberCyan
                    else -> StealthBorder
                },
                label = "borderColor"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
                    .testTag("vpn_power_button_container")
            ) {
                // Outer glow halo
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    when {
                                        isConnected -> NeonGreen.copy(alpha = 0.35f)
                                        isConnecting -> CyberCyan.copy(alpha = 0.35f)
                                        else -> Color.Transparent
                                    },
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main Button
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            when {
                                isConnected -> Color(0xFF003822)
                                isConnecting -> Color(0xFF003038)
                                else -> StealthSurfaceVariant
                            }
                        )
                        .border(3.dp, buttonBorderColor, CircleShape)
                        .clickable { onToggleVpn() }
                        .testTag("vpn_power_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle VPN",
                        tint = when {
                            isConnected -> NeonGreen
                            isConnecting -> CyberCyan
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Text
            Text(
                text = when (vpnStatus) {
                    VpnStatus.CONNECTED -> "CONNECTED & ENCRYPTED"
                    VpnStatus.CONNECTING -> "ESTABLISHING STEALTH TUNNEL..."
                    VpnStatus.DISCONNECTING -> "DISCONNECTING..."
                    VpnStatus.DISCONNECTED -> "NOT PROTECTED"
                },
                color = when (vpnStatus) {
                    VpnStatus.CONNECTED -> NeonGreen
                    VpnStatus.CONNECTING -> CyberCyan
                    else -> TextMuted
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Text(
                text = if (isConnected) "Virtual IP: ${selectedServer.ipAddress} • ${selectedServer.protocol}" else "Real IP Exposed to Web Trackers",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Telemetry / Speed Metrics Card
            Card(
                colors = CardDefaults.cardColors(containerColor = StealthSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricItem(
                        title = "DOWNLOAD",
                        value = if (isConnected) String.format("%.1f MB/s", stats.currentDownloadSpeedKbps / 1024) else "0.0 MB/s",
                        color = CyberCyan
                    )
                    MetricItem(
                        title = "UPLOAD",
                        value = if (isConnected) String.format("%.1f MB/s", stats.currentUploadSpeedKbps / 1024) else "0.0 MB/s",
                        color = GhostPurpleLight
                    )
                    MetricItem(
                        title = "LATENCY",
                        value = "${selectedServer.pingMs} ms",
                        color = NeonGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Shield Toggles
            Card(
                colors = CardDefaults.cardColors(containerColor = StealthSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Kill Switch Protection", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Blocks traffic if VPN connection drops", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = stats.killSwitchActive,
                            onCheckedChange = { onToggleKillSwitch() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = CyberCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Oblivious DNS Shield", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Encrypted zero-knowledge DNS queries", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = stats.dnsProtectionActive,
                            onCheckedChange = { onToggleDnsProtection() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Server Location Selector Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SELECT PROXY NODE",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${servers.size} Active Nodes",
                    color = CyberCyan,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Server List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                items(servers) { server ->
                    val isSelected = server.id == selectedServer.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) StealthSurfaceVariant else StealthSurface)
                            .border(1.dp, if (isSelected) CyberCyan else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { onSelectServer(server) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = server.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${server.country} (${server.city})",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (server.isRecommended) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("FAST", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = "${server.featureBadge} • Load ${server.loadPercentage}%",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${server.pingMs}ms",
                                color = NeonGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}
