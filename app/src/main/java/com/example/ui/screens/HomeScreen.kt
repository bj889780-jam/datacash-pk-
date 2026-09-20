package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.models.BandwidthSessionStats
import com.example.models.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    sessionStats: BandwidthSessionStats,
    onToggleSelling: () -> Unit,
    onOpenWithdrawal: () -> Unit,
    onOpenDailyBonus: () -> Unit,
    onOpenReferral: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenAdmin: () -> Unit,
    onSetDailyLimit: (Double) -> Unit,
    unreadNotificationsCount: Int = 0
) {
    var showLimitDialog by remember { mutableStateOf(false) }

    val remainingMb = EarningCalculator.calculateRemainingMb(
        dailyLimitMb = userProfile.dailyLimitMb,
        soldMb = sessionStats.todayMbSold
    )

    val progressFraction = (sessionStats.todayMbSold / userProfile.dailyLimitMb)
        .toFloat()
        .coerceIn(0f, 1f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandMonogramLogo(size = 44)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Good Morning, ${userProfile.fullName.split(" ").firstOrNull() ?: "Ali"}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = GoldEarnings.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "DEMO MODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldEarnings,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (userProfile.isWifiOnly) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = VibrantBlue.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Wi-Fi Only",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = VibrantBlue,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notification_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = ErrorRed,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotificationsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenAdmin,
                        modifier = Modifier.testTag("demo_admin_shortcut_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tune,
                            contentDescription = "Demo Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Safety Transparency Banner
        item {
            DemoSafetyBanner()
        }

        // Primary Balances & Earnings Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_balance_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NavySlate
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Balance",
                            fontSize = 13.sp,
                            color = Slate400,
                            fontWeight = FontWeight.Medium
                        )
                        Surface(
                            color = Slate800,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PulsingDot(color = GoldEarnings, size = 6)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "3 MB = Rs. 1",
                                    fontSize = 11.sp,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = EarningCalculator.formatPkr(sessionStats.availableBalancePkr),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Slate700)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Today's Earnings",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = EarningCalculator.formatPkr(sessionStats.todayEarningsPkr),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                        }

                        Column {
                            Text(
                                text = "MB Sold Today",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = EarningCalculator.formatMb(sessionStats.todayMbSold),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealGreenLight
                            )
                        }

                        Column {
                            Text(
                                text = "Remaining Allowance",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = EarningCalculator.formatMb(remainingMb),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate200
                            )
                        }
                    }
                }
            }
        }

        // Bandwidth Meter & Daily Quota Progress
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bandwidth_meter_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = VibrantBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bandwidth Meter",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = VibrantBlueBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "DEMO SIMULATION",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speeds Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SpeedTile(
                            label = "Upload Speed",
                            speed = if (sessionStats.isSellingActive) "${String.format("%.1f", sessionStats.uploadSpeedMbps)} MB/s" else "0.0 MB/s",
                            icon = Icons.Default.ArrowUpward,
                            color = TealGreen,
                            modifier = Modifier.weight(1f)
                        )
                        SpeedTile(
                            label = "Download Speed",
                            speed = if (sessionStats.isSellingActive) "${String.format("%.1f", sessionStats.downloadSpeedMbps)} MB/s" else "0.0 MB/s",
                            icon = Icons.Default.ArrowDownward,
                            color = VibrantBlue,
                            modifier = Modifier.weight(1f)
                        )
                        SpeedTile(
                            label = "Session Time",
                            speed = EarningCalculator.formatDuration(sessionStats.sessionDurationSeconds),
                            icon = Icons.Default.Timer,
                            color = GoldEarnings,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Quota: ${sessionStats.todayMbSold.toInt()} / ${userProfile.dailyLimitMb.toInt()} MB",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = { showLimitDialog = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Adjust Limit",
                                fontSize = 12.sp,
                                color = TealGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (progressFraction >= 1f) ErrorRed else TealGreen,
                        trackColor = Slate200
                    )
                }
            }
        }

        // Large Dashboard Control Button: START SELLING DATA / PAUSE SELLING
        item {
            val isActive = sessionStats.isSellingActive
            val buttonColor by animateColorAsState(
                targetValue = if (isActive) GoldEarnings else TealGreen,
                label = "button_color"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selling_control_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) TealGreenBg else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = if (isActive) 1.5.dp else 0.5.dp,
                    brush = if (isActive) Brush.horizontalGradient(listOf(TealGreen, VibrantBlue)) else Brush.horizontalGradient(listOf(Slate300, Slate300))
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isActive) {
                            PulsingDot(color = TealGreen, size = 10)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Demo Selling Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TealGreen
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Slate400)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Selling Paused",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = Slate700
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onToggleSelling,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("toggle_selling_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isActive) "PAUSE SELLING" else "START SELLING DATA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    if (isActive) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Simulated this session: +${EarningCalculator.formatMb(sessionStats.sessionTransferredMb)} (${EarningCalculator.formatPkr(sessionStats.sessionEarningsPkr)})",
                            fontSize = 12.sp,
                            color = TealGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Earning Formula Card
        item {
            EarningFormulaCard()
        }

        // Quick Navigation Grid (Withdrawal, Daily Bonus, Referral)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "Withdraw",
                    subtitle = "EasyPaisa/JazzCash",
                    icon = Icons.Default.Payments,
                    iconColor = GoldEarnings,
                    onClick = onOpenWithdrawal,
                    modifier = Modifier.weight(1f),
                    tag = "quick_withdraw_button"
                )
                QuickActionCard(
                    title = "Daily Bonus",
                    subtitle = "7-Day Streak",
                    icon = Icons.Default.CardGiftcard,
                    iconColor = TealGreen,
                    onClick = onOpenDailyBonus,
                    modifier = Modifier.weight(1f),
                    tag = "quick_daily_bonus_button"
                )
                QuickActionCard(
                    title = "Invite & Earn",
                    subtitle = "Rs. 50 / Friend",
                    icon = Icons.Default.Share,
                    iconColor = VibrantBlue,
                    onClick = onOpenReferral,
                    modifier = Modifier.weight(1f),
                    tag = "quick_referral_button"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showLimitDialog) {
        DailyLimitDialog(
            currentLimit = userProfile.dailyLimitMb,
            onDismiss = { showLimitDialog = false },
            onSelectLimit = { limit ->
                onSetDailyLimit(limit)
                showLimitDialog = false
            }
        )
    }
}

@Composable
fun SpeedTile(
    label: String,
    speed: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = speed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = Slate400,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = Slate400,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DailyLimitDialog(
    currentLimit: Double,
    onDismiss: () -> Unit,
    onSelectLimit: (Double) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Daily MB Limit",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Configure how much data you want to allow DataCash PK to simulate selling per day (Max: 12,000 MB).",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                AppConfig.DAILY_LIMIT_OPTIONS.forEach { optionMb ->
                    val isSelected = currentLimit.toInt() == optionMb
                    val earnings = optionMb / AppConfig.MB_PER_RUPEE

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectLimit(optionMb.toDouble()) },
                        color = if (isSelected) TealGreenBg else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(TealGreen, VibrantBlue)),
                            width = 1.dp
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${optionMb} MB / day",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TealGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Max Rs. ${earnings.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldEarnings
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TealGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}
