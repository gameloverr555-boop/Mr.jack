package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.model.VpnServer
import com.example.model.VpnStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.StealthSurface
import com.example.ui.theme.VanishGlow

@Composable
fun AddressBar(
    tab: BrowserTab,
    isVanishMode: Boolean,
    vpnStatus: VpnStatus,
    selectedVpnServer: VpnServer,
    tabCount: Int,
    onUrlSubmit: (String) -> Unit,
    onReload: () -> Unit,
    onOpenVpnSheet: () -> Unit,
    onOpenAdBlockSheet: () -> Unit,
    onOpenTabOverview: () -> Unit,
    onOpenQrScanner: () -> Unit,
    onOpenQrGenerator: () -> Unit,
    onStartVoiceSearch: () -> Unit,
    onToggleVanishMode: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onToggleEditMode: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onVanishPurgeNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf(if (tab.isBlank) "" else tab.url) }
    var isEditing by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(tab.url) {
        if (!isEditing) {
            textInput = if (tab.isBlank) "" else tab.url
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (tab.isLoading) tab.progress / 100f else 0f,
        label = "web_progress"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .testTag("address_bar_container")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // VPN Quick Status Sub-Bar
            VpnQuickStatusPill(
                vpnStatus = vpnStatus,
                server = selectedVpnServer,
                onClick = onOpenVpnSheet,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AdBlock Shield Pill
                BadgedBox(
                    badge = {
                        val totalBlocked = tab.adsBlockedOnPage + tab.trackersBlockedOnPage
                        if (totalBlocked > 0) {
                            Badge(
                                containerColor = if (isVanishMode) VanishGlow else CyberCyan,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = if (totalBlocked > 99) "99+" else "$totalBlocked",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    modifier = Modifier.testTag("adblock_shield_badge")
                ) {
                    IconButton(
                        onClick = onOpenAdBlockSheet,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("adblock_shield_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Shield Settings",
                            tint = if (tab.isAdBlockEnabled) (if (isVanishMode) GhostPurpleLight else NeonGreen) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Search & URL Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(StealthSurface)
                        .border(1.dp, if (isEditing) (if (isVanishMode) GhostPurpleLight else CyberCyan) else StealthBorder, RoundedCornerShape(22.dp))
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SSL Lock Indicator
                        if (!isEditing && !tab.isBlank) {
                            Icon(
                                imageVector = if (tab.isSecureSsl) Icons.Default.Lock else Icons.Outlined.LockOpen,
                                contentDescription = if (tab.isSecureSsl) "Secure SSL" else "Not Secure",
                                tint = if (tab.isSecureSsl) NeonGreen else NeonRed,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }

                        // Search Text input
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState ->
                                    isEditing = focusState.isFocused
                                    if (focusState.isFocused && tab.isBlank) {
                                        textInput = ""
                                    }
                                }
                                .testTag("address_bar_input"),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(if (isVanishMode) GhostPurpleLight else CyberCyan),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    focusManager.clearFocus()
                                    isEditing = false
                                    onUrlSubmit(textInput)
                                }
                            ),
                            decorationBox = { innerTextField ->
                                if (textInput.isEmpty()) {
                                    Text(
                                        text = if (isVanishMode) "Search privately or enter URL..." else "Search or enter address...",
                                        color = Color.Gray,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                innerTextField()
                            }
                        )

                        // Clear / Voice / QR actions
                        if (isEditing && textInput.isNotEmpty()) {
                            IconButton(
                                onClick = { textInput = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            // Voice Search Button
                            IconButton(
                                onClick = onStartVoiceSearch,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("voice_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice Search",
                                    tint = if (isVanishMode) GhostPurpleLight else CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // QR Scanner Button
                            IconButton(
                                onClick = onOpenQrScanner,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("qr_scanner_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "QR Scanner",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Tab Switcher Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, if (isVanishMode) GhostPurpleLight else CyberCyan, RoundedCornerShape(8.dp))
                        .clickable { onOpenTabOverview() }
                        .testTag("tab_switcher_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabCount",
                        color = if (isVanishMode) GhostPurpleLight else CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Overflow Menu
                Box {
                    IconButton(
                        onClick = { isMenuOpen = true },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("more_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = { isMenuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(if (tab.isEditMode) "✏️ Exit Edit & Prank" else "🎭 Edit & Prank Webpage")
                            },
                            onClick = {
                                isMenuOpen = false
                                onToggleEditMode()
                            },
                            modifier = Modifier.testTag("menu_prank_mode")
                        )

                        DropdownMenuItem(
                            text = { Text("⚡ Mock VPN Proxy") },
                            onClick = {
                                isMenuOpen = false
                                onOpenVpnSheet()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(if (tab.isDesktopMode) "📱 Switch to Mobile View" else "💻 Desktop Site")
                            },
                            onClick = {
                                isMenuOpen = false
                                onToggleDesktopMode()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Computer, contentDescription = null)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("📲 Share Page QR Code") },
                            onClick = {
                                isMenuOpen = false
                                onOpenQrGenerator()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("⭐ Bookmarks & History") },
                            onClick = {
                                isMenuOpen = false
                                onOpenBookmarksHistory()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (isVanishMode) "🛡️ Switch to Standard Mode" else "👻 Switch to Vanish Mode"
                                )
                            },
                            onClick = {
                                isMenuOpen = false
                                onToggleVanishMode()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text("🔥 Vanish & Purge Session", color = NeonRed, fontWeight = FontWeight.Bold)
                            },
                            onClick = {
                                isMenuOpen = false
                                onVanishPurgeNow()
                            },
                            modifier = Modifier.testTag("menu_vanish_purge")
                        )

                        DropdownMenuItem(
                            text = { Text("🔄 Reload Page") },
                            onClick = {
                                isMenuOpen = false
                                onReload()
                            }
                        )
                    }
                }
            }

            // Web Loading Progress Bar
            if (tab.isLoading) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp),
                    color = if (isVanishMode) GhostPurpleLight else CyberCyan,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun VpnQuickStatusPill(
    vpnStatus: VpnStatus,
    server: VpnServer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnStatus == VpnStatus.CONNECTED
    val isConnecting = vpnStatus == VpnStatus.CONNECTING

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isConnected) Color(0xFF00363D) else Color(0xFF1E2638))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isConnected -> NeonGreen
                            isConnecting -> NeonRed
                            else -> Color.Gray
                        }
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when {
                    isConnected -> "SHIELD ACTIVE: ${server.flag} ${server.city} (${server.ipAddress})"
                    isConnecting -> "CONNECTING TO TUNNEL..."
                    else -> "MOCK VPN: DISCONNECTED (TAP TO PROTECT)"
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isConnected) CyberCyan else Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isConnected) {
            Text(
                text = "${server.pingMs}ms",
                fontSize = 10.sp,
                color = NeonGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
