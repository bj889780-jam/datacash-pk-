package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class LegalTab {
    PRIVACY,
    TERMS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    initialTab: LegalTab = LegalTab.PRIVACY,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedTab == LegalTab.PRIVACY) "Privacy Policy" else "Terms of Service",
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
                .testTag("legal_screen")
        ) {
            TabRow(selectedTabIndex = if (selectedTab == LegalTab.PRIVACY) 0 else 1) {
                Tab(
                    selected = selectedTab == LegalTab.PRIVACY,
                    onClick = { selectedTab = LegalTab.PRIVACY },
                    text = { Text("Privacy Policy", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == LegalTab.TERMS,
                    onClick = { selectedTab = LegalTab.TERMS },
                    text = { Text("Terms of Service", fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedTab == LegalTab.PRIVACY) {
                    LegalSection(
                        title = "1. Information Collection & Zero-Traffic Guarantee",
                        body = "DataCash PK operates under strict privacy and safety guidelines. The current application is a prototype that DOES NOT route, intercept, decrypt, or log any personal browsing history, DNS queries, or app traffic from your device."
                    )
                    LegalSection(
                        title = "2. Device Permissions",
                        body = "We only declare normal network state permissions (INTERNET, ACCESS_NETWORK_STATE) required to check online status and simulate bandwidth statistics. We never request storage, media, contacts, or location permissions."
                    )
                    LegalSection(
                        title = "3. Third-Party Bandwidth Architecture",
                        body = "When commercial bandwidth monetization services are released in future builds, all operations will be strictly opt-in with explicit authorization. Users retain full control to set daily limits and stop sessions anytime."
                    )
                    LegalSection(
                        title = "4. Payout Information",
                        body = "Account titles and mobile wallet numbers submitted for EasyPaisa, JazzCash, or bank payouts are encrypted and utilized solely for processing requested disbursements."
                    )
                } else {
                    LegalSection(
                        title = "1. Earning Conditions & Rate",
                        body = "All earnings are calculated strictly based on the published formula: 3 MB = Rs. 1 (Earnings in PKR = MB Sold ÷ 3). No alternate formulas or hidden multipliers are permitted."
                    )
                    LegalSection(
                        title = "2. Daily Limits & Safeguards",
                        body = "The daily data sharing quota cannot exceed 12,000 MB per 24-hour cycle. The maximum theoretical daily earning ceiling is Rs. 4,000 (12,000 ÷ 3). The engine auto-pauses upon reaching your configured limit."
                    )
                    LegalSection(
                        title = "3. Withdrawal Minimums & Processing Fees",
                        body = "The minimum payout request is Rs. 500. A standard administrative service fee of Rs. 50 is deducted from every processed withdrawal. Net received amount equals: Withdrawal Amount minus Rs. 50."
                    )
                    LegalSection(
                        title = "4. Prototype / Demo Disclaimer",
                        body = "This version is distributed for interface evaluation, workflow validation, and demonstration purposes. No commercial financial warranties or guaranteed liquidity are implied during prototype evaluation."
                    )
                }
            }
        }
    }
}

@Composable
fun LegalSection(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
