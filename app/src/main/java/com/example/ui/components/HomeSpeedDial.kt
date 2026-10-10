package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdBlockGlobalStats
import com.example.model.QuickDialSite
import com.example.model.VpnServer
import com.example.model.VpnStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.StealthSurface
import com.example.ui.theme.StealthSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VanishGlow

@Composable
fun HomeSpeedDial(
    isVanishMode: Boolean,
    vpnStatus: VpnStatus,
    selectedVpnServer: VpnServer,
    adBlockStats: AdBlockGlobalStats,
    quickDials: List<QuickDialSite>,
    onSelectSite: (String) -> Unit,
    onOpenVpn: () -> Unit,
    onOpenAdBlock: () -> Unit,
    onOpenQrScanner: () -> Unit,
    onStartVoiceSearch: () -> Unit,
    onToggleVanishMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("home_speed_dial"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))

            // Branding Header
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isVanishMode) VanishGlow.copy(alpha = 0.4f) else CyberCyan.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVanishMode) Icons.Default.VisibilityOff else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isVanishMode) GhostPurpleLight else CyberCyan,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isVanishMode) "VANISH INCOGNITO" else "GHOSTSHIELD",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = if (isVanishMode) "Zero footprints • Auto wipes cookies, cache, and history" else "Next-Gen Stealth Browser with Mock VPN & Content Editor",
                color = if (isVanishMode) GhostPurpleLight else TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy & Utilities Quick Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UtilityPillButton(
                    icon = Icons.Default.VpnLock,
                    label = if (vpnStatus == VpnStatus.CONNECTED) "${selectedVpnServer.flag} Connected" else "Mock VPN",
                    color = if (vpnStatus == VpnStatus.CONNECTED) NeonGreen else CyberCyan,
                    onClick = onOpenVpn,
                    modifier = Modifier.weight(1f)
                )

                UtilityPillButton(
                    icon = Icons.Default.Security,
                    label = "${adBlockStats.totalAdsBlocked + adBlockStats.totalTrackersBlocked} Blocked",
                    color = NeonGreen,
                    onClick = onOpenAdBlock,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UtilityPillButton(
                    icon = Icons.Default.QrCodeScanner,
                    label = "QR Scanner",
                    color = CyberCyan,
                    onClick = onOpenQrScanner,
                    modifier = Modifier.weight(1f)
                )

                UtilityPillButton(
                    icon = Icons.Default.Mic,
                    label = "Voice Search",
                    color = GhostPurpleLight,
                    onClick = onStartVoiceSearch,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Speed Dial Grid Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPEED DIAL",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tap to browse",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Speed Dial Items (rendered as 2-column rows)
        val rows = quickDials.chunked(2)
        items(rows.size) { rowIndex ->
            val rowItems = rows[rowIndex]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (site in rowItems) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = StealthSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .border(1.dp, StealthBorder, RoundedCornerShape(14.dp))
                            .clickable { onSelectSite(site.url) }
                            .testTag("speed_dial_${site.title}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = site.iconEmoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = site.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = site.category,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun UtilityPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = StealthSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(48.dp)
            .border(1.dp, StealthBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
