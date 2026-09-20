package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandMonogramLogo
import com.example.ui.theme.*

data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val badge: String
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            title = "Monetize Unused Internet",
            description = "Eligible Pakistani users can monetize surplus data quota. Earn at the official rate: 3 MB = Rs. 1.",
            icon = Icons.Default.WifiTethering,
            iconColor = TealGreen,
            badge = "EARNING CONCEPT"
        ),
        OnboardingPageData(
            title = "Safe Prototype Simulation",
            description = "This prototype operates in safe demo mode. No personal network traffic is routed or intercepted.",
            icon = Icons.Default.Security,
            iconColor = VibrantBlue,
            badge = "ZERO TRAFFIC ROUTING"
        ),
        OnboardingPageData(
            title = "Fast Local Payouts",
            description = "Withdraw your earnings directly to EasyPaisa, JazzCash, or any 1Link Pakistani bank account.",
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = GoldEarnings,
            badge = "EASYPAISA & JAZZCASH"
        )
    )

    val currentData = pages[currentPage]

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen"),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandMonogramLogo(size = 40)
                TextButton(
                    onClick = onFinishOnboarding,
                    modifier = Modifier.testTag("skip_onboarding_button")
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Surface(
                    color = currentData.iconColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = currentData.icon,
                            contentDescription = currentData.title,
                            tint = currentData.iconColor,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Surface(
                    color = currentData.iconColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = currentData.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentData.iconColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentData.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentData.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Bottom Navigation
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (index == currentPage) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPage) TealGreen else Slate300
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealGreen
                    )
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) "Get Started" else "Continue",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

val Slate300 = Color(0xFFCBD5E1)
