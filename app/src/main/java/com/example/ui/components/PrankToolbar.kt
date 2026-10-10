package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GhostPurpleLight
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.StealthBlack
import com.example.ui.theme.StealthBorder
import com.example.ui.theme.StealthSurfaceVariant

@Composable
fun PrankToolbar(
    isVisible: Boolean,
    onBillionairePrank: () -> Unit,
    onMatrixPrank: () -> Unit,
    onStampPrank: (String) -> Unit,
    onHeadlinePrank: (String) -> Unit,
    onCloseEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHeadlineDialog by remember { mutableStateOf(false) }
    var customHeadlineText by remember { mutableStateOf("BREAKING: Local Dog Elected Supreme Ruler of the Internet!") }

    var showStampDialog by remember { mutableStateOf(false) }
    var customStampText by remember { mutableStateOf("TOP SECRET") }

    if (showHeadlineDialog) {
        AlertDialog(
            onDismissRequest = { showHeadlineDialog = false },
            title = { Text("Swap Page Headline") },
            text = {
                Column {
                    Text("Type a prank headline to replace the main title on this webpage:", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customHeadlineText,
                        onValueChange = { customHeadlineText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onHeadlinePrank(customHeadlineText)
                        showHeadlineDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black)
                ) {
                    Text("Apply Headline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHeadlineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showStampDialog) {
        AlertDialog(
            onDismissRequest = { showStampDialog = false },
            title = { Text("Stamp Webpage") },
            text = {
                Column {
                    Text("Choose or enter stamp text:", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customStampText,
                        onValueChange = { customStampText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { customStampText = "BILLIONAIRE VERIFIED" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = StealthSurfaceVariant)
                        ) {
                            Text("Billionaire", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { customStampText = "100% PRANKED" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = StealthSurfaceVariant)
                        ) {
                            Text("Pranked", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onStampPrank(customStampText)
                        showStampDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed, contentColor = Color.White)
                ) {
                    Text("Stamp Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStampDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Surface(
            color = StealthBlack.copy(alpha = 0.95f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .shadow(12.dp, RoundedCornerShape(16.dp))
                .border(1.dp, CyberCyan, RoundedCornerShape(16.dp))
                .testTag("prank_toolbar")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Top status label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyberCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🎭 EDIT & PRANK MODE (Tap any text on page!)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }

                    IconButton(
                        onClick = onCloseEditMode,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Prank Mode",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Prank Action Tools
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrankToolChip(
                        icon = Icons.Default.AttachMoney,
                        label = "Billionaire",
                        tint = NeonGreen,
                        onClick = onBillionairePrank,
                        testTag = "prank_billionaire_button"
                    )

                    PrankToolChip(
                        icon = Icons.Default.Newspaper,
                        label = "Swap Headline",
                        tint = CyberCyan,
                        onClick = { showHeadlineDialog = true },
                        testTag = "prank_headline_button"
                    )

                    PrankToolChip(
                        icon = Icons.Default.Terminal,
                        label = "Hacker Matrix",
                        tint = NeonGreen,
                        onClick = onMatrixPrank,
                        testTag = "prank_matrix_button"
                    )

                    PrankToolChip(
                        icon = Icons.Default.Warning,
                        label = "Stamp Secret",
                        tint = NeonRed,
                        onClick = { showStampDialog = true },
                        testTag = "prank_stamp_button"
                    )

                    PrankToolChip(
                        icon = Icons.Default.Done,
                        label = "Done Editing",
                        tint = GhostPurpleLight,
                        onClick = onCloseEditMode,
                        testTag = "prank_done_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun PrankToolChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = StealthSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}
