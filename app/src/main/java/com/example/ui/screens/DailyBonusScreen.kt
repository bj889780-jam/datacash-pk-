package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.EarningCalculator
import com.example.models.BonusClaimStatus
import com.example.models.DailyBonusDay
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyBonusScreen(
    bonusDays: List<DailyBonusDay>,
    onClaimBonus: (Int) -> Unit,
    onBack: () -> Unit
) {
    val availableDay = bonusDays.firstOrNull { it.status == BonusClaimStatus.AVAILABLE }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "7-Day Daily Streak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Streak Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySlate)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldEarnings.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldEarnings,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Daily Login Rewards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Check in every day to claim bonus PKR rewards. Reach Day 7 to unlock Rs. 50 bonus!",
                        fontSize = 12.sp,
                        color = Slate300,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }

            // 7-Day Grid
            Text(
                text = "Streak Calendar",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // First 4 days
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bonusDays.take(4).forEach { day ->
                        DayBonusCard(
                            day = day,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (day.status == BonusClaimStatus.AVAILABLE) {
                                    onClaimBonus(day.dayNumber)
                                }
                            }
                        )
                    }
                }

                // Last 3 days
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    bonusDays.drop(4).forEach { day ->
                        DayBonusCard(
                            day = day,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (day.status == BonusClaimStatus.AVAILABLE) {
                                    onClaimBonus(day.dayNumber)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Claim Button
            Button(
                onClick = {
                    availableDay?.let { onClaimBonus(it.dayNumber) }
                },
                enabled = availableDay != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("claim_daily_bonus_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldEarnings,
                    disabledContainerColor = Slate300
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (availableDay != null) {
                        "Claim Day ${availableDay.dayNumber} Bonus (+${EarningCalculator.formatPkr(availableDay.rewardPkr)})"
                    } else {
                        "Today's Bonus Already Claimed"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun DayBonusCard(
    day: DailyBonusDay,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (bgColor, borderColor, icon, statusText, statusColor) = when (day.status) {
        BonusClaimStatus.CLAIMED -> listOf(
            TealGreenBg,
            TealGreen,
            Icons.Default.Check,
            "Claimed",
            TealGreen
        )
        BonusClaimStatus.AVAILABLE -> listOf(
            GoldBg,
            GoldEarnings,
            Icons.Default.Star,
            "Available",
            GoldEarnings
        )
        BonusClaimStatus.LOCKED -> listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            Slate300,
            Icons.Default.Lock,
            "Locked",
            Slate400
        )
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("bonus_day_${day.dayNumber}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor as Color),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(borderColor as Color),
            width = if (day.status == BonusClaimStatus.AVAILABLE) 1.5.dp else 0.6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Day ${day.dayNumber}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                contentDescription = null,
                tint = statusColor as Color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rs. ${day.rewardPkr.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (day.status == BonusClaimStatus.AVAILABLE) GoldEarnings else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = statusText as String,
                fontSize = 9.sp,
                color = statusColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
