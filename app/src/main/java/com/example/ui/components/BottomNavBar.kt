package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.VanishGlow

@Composable
fun BottomNavBar(
    tab: BrowserTab,
    isVanishMode: Boolean,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onToggleVanishMode: () -> Unit,
    onToggleEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .fillMaxWidth()
            .testTag("bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                enabled = tab.canGoBack,
                modifier = Modifier.testTag("nav_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (tab.canGoBack) MaterialTheme.colorScheme.onSurface else Color.DarkGray
                )
            }

            // Forward Button
            IconButton(
                onClick = onForward,
                enabled = tab.canGoForward,
                modifier = Modifier.testTag("nav_forward_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (tab.canGoForward) MaterialTheme.colorScheme.onSurface else Color.DarkGray
                )
            }

            // Vanish Mode Center Toggle
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isVanishMode) Color(0xFF4A0E78) else Color(0xFF1E2638)
                    )
                    .border(
                        1.dp,
                        if (isVanishMode) GhostPurpleLight else CyberCyan,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onToggleVanishMode,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("vanish_mode_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = "Toggle Vanish Mode",
                        tint = if (isVanishMode) GhostPurpleLight else CyberCyan
                    )
                }
            }

            // Edit & Prank Webpage Button
            IconButton(
                onClick = onToggleEditMode,
                modifier = Modifier.testTag("prank_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "Edit & Prank",
                    tint = if (tab.isEditMode) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bookmark Toggle
            IconButton(
                onClick = onBookmarkToggle,
                modifier = Modifier.testTag("bookmark_toggle_button")
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) NeonGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Home Button
            IconButton(
                onClick = onHome,
                modifier = Modifier.testTag("home_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
