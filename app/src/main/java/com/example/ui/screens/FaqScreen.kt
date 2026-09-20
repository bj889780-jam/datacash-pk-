package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class FaqQuestion(
    val question: String,
    val answer: String,
    val isHighlight: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(
    onBack: () -> Unit
) {
    val faqList = listOf(
        FaqQuestion(
            question = "What is DataCash PK?",
            answer = "DataCash PK is a Pakistan-focused mobile-data monetization application concept. In future production releases, eligible users will be able to monetize unused internet quota via verified bandwidth exchanges. The current app is a complete UI prototype with a safe mock engine."
        ),
        FaqQuestion(
            question = "How are MB earnings calculated?",
            answer = "Earnings follow the strict official rule: 3 MB = Rs. 1. This means:\n• 3 MB = Rs. 1\n• 30 MB = Rs. 10\n• 300 MB = Rs. 100\n• 1,200 MB = Rs. 400\n• 12,000 MB = Rs. 4,000\nFormula: Earnings (PKR) = MB Sold ÷ 3.",
            isHighlight = true
        ),
        FaqQuestion(
            question = "What is Demo Mode?",
            answer = "In Demo Mode, no real internet traffic is routed, proxied, or redirected through your device. The speeds and transfers you see are simulated to let you experience the earning interface and financial flows safely."
        ),
        FaqQuestion(
            question = "Is real bandwidth selling currently active?",
            answer = "No. Real bandwidth selling is not active in the current Demo version. The app is intentionally architected with mock providers (MockBandwidthProvider and MockPaymentProvider) so that legitimate enterprise integrations can be connected later.",
            isHighlight = true
        ),
        FaqQuestion(
            question = "What is the daily selling limit?",
            answer = "The maximum selling limit is set to 12,000 MB (12 GB) per day. Users can choose lower safety thresholds such as 500 MB, 1,000 MB, 2,000 MB, 5,000 MB, or 10,000 MB from Settings."
        ),
        FaqQuestion(
            question = "What happens when I reach my daily limit?",
            answer = "The selling engine automatically pauses data transfer simulation as soon as today's MB reaches your configured daily limit. This protects your mobile allowance from exceeding planned limits."
        ),
        FaqQuestion(
            question = "How does withdrawal work?",
            answer = "You can request payouts to Pakistani payment methods: EasyPaisa, JazzCash, or any 1Link Bank Account. The minimum withdrawal threshold is Rs. 500."
        ),
        FaqQuestion(
            question = "What is the Rs. 50 admin fee on withdrawals?",
            answer = "Every withdrawal request has a fixed Rs. 50 administrative processing fee deducted. For example, if you withdraw Rs. 500, you will receive Rs. 450 (Formula: You Receive = Withdrawal Amount - Rs. 50)."
        ),
        FaqQuestion(
            question = "How does the referral program work?",
            answer = "Each user has a unique referral code. When a friend joins using your code, you receive a bonus of Rs. 50 credited directly to your available balance."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Frequently Asked Questions",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("faq_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Everything you need to know about DataCash PK's calculations, limits, and demo features.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(faqList) { item ->
                FaqExpandableCard(faq = item)
            }
        }
    }
}

@Composable
fun FaqExpandableCard(faq: FaqQuestion) {
    var expanded by remember { mutableStateOf(faq.isHighlight) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (faq.isHighlight) TealGreenBg else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (faq.isHighlight) 1.2.dp else 0.6.dp,
            brush = if (faq.isHighlight) androidx.compose.ui.graphics.SolidColor(TealGreen) else androidx.compose.ui.graphics.SolidColor(Slate300)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (faq.isHighlight) TealGreen else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (faq.isHighlight) TealGreen else Slate400
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 12.sp,
                        color = if (faq.isHighlight) Slate800 else MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
