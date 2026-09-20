package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.ui.theme.*

/**
 * Modern D-C monogram branding logo component.
 */
@Composable
fun BrandMonogramLogo(
    size: Int = 48,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.28).dp))
            .background(Color.White)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(listOf(TealGreen, VibrantBlue, GoldEarnings)),
                shape = RoundedCornerShape((size * 0.28).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "D",
                fontSize = (size * 0.44).sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavySlate
            )
            Box(
                modifier = Modifier
                    .width((size * 0.08).dp)
                    .height((size * 0.35).dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(listOf(TealGreen, GoldEarnings))
                    )
            )
            Text(
                text = "C",
                fontSize = (size * 0.44).sp,
                fontWeight = FontWeight.ExtraBold,
                color = VibrantBlue
            )
        }
    }
}

/**
 * Prominent Demo Mode Safety Banner informing user clearly that no real traffic is routed.
 */
@Composable
fun DemoSafetyBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("demo_safety_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GoldBg
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(GoldEarnings, GoldLight)),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(GoldEarnings),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Demo Mode",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Demo Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NavySlate
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = GoldEarnings,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "SAFE PROTOTYPE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "No real internet traffic is being routed. UI and earning calculations only.",
                    fontSize = 11.sp,
                    color = Slate700,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

/**
 * Educational Card explaining the critical earning formula: 3 MB = Rs. 1
 */
@Composable
fun EarningFormulaCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("earning_formula_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = TealGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Official Earning Formula",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = TealGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "3 MB = Rs. 1",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TealGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Earnings (PKR) = MB Sold ÷ 3",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FormulaSamplePill(mb = "30 MB", pkr = "Rs. 10")
                FormulaSamplePill(mb = "300 MB", pkr = "Rs. 100")
                FormulaSamplePill(mb = "1,200 MB", pkr = "Rs. 400")
                FormulaSamplePill(mb = "12,000 MB", pkr = "Rs. 4,000")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Daily limit: Up to 12,000 MB/day (Max Rs. 4,000 daily earnings).",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FormulaSamplePill(mb: String, pkr: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mb,
                fontSize = 10.sp,
                color = Slate400,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = pkr,
                fontSize = 11.sp,
                color = GoldEarnings,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Animated Pulse Dot for active selling state.
 */
@Composable
fun PulsingDot(
    color: Color = SuccessGreen,
    size: Int = 10
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha))
    )
}
