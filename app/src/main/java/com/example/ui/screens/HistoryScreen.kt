package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.core.EarningCalculator
import com.example.models.TransactionItem
import com.example.models.TransactionStatus
import com.example.models.TransactionType
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    transactions: List<TransactionItem>
) {
    var selectedFilter by remember { mutableStateOf<TransactionType?>(null) }
    var selectedTransaction by remember { mutableStateOf<TransactionItem?>(null) }

    val filterOptions = listOf(
        null to "All",
        TransactionType.EARNING to "Earning",
        TransactionType.WITHDRAWAL to "Withdrawal",
        TransactionType.BONUS to "Bonus",
        TransactionType.REFERRAL to "Referral"
    )

    val filteredList = if (selectedFilter == null) {
        transactions
    } else {
        transactions.filter { it.type == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Transaction History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Full statement of data monetization & payouts",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterOptions.forEach { (type, label) ->
                    val isSelected = selectedFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = type },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No transactions found in this category",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Slate400
                        )
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { tx ->
                TransactionCard(
                    transaction = tx,
                    onClick = { selectedTransaction = tx }
                )
            }
        }
    }

    // Detail Dialog
    selectedTransaction?.let { tx ->
        AlertDialog(
            onDismissRequest = { selectedTransaction = null },
            title = {
                Text(
                    text = "Transaction Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Transaction ID", tx.id)
                    DetailRow("Type", tx.type.name)
                    DetailRow("Date & Time", "${tx.date} • ${tx.time}")
                    DetailRow("Gross Amount", EarningCalculator.formatPkr(tx.amountPkr), isBold = true)
                    tx.paymentMethod?.let {
                        DetailRow("Payout Method", it.displayName)
                    }
                    tx.accountTitle?.let {
                        DetailRow("Account Title", it)
                    }
                    tx.accountNumber?.let {
                        DetailRow("Account / Mobile", it)
                    }
                    DetailRow("Status", tx.status.name)
                    if (tx.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tx.description,
                            fontSize = 11.sp,
                            color = Slate700,
                            lineHeight = 15.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTransaction = null }) {
                    Text("Close", color = TealGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun TransactionCard(
    transaction: TransactionItem,
    onClick: () -> Unit
) {
    val isWithdrawal = transaction.type == TransactionType.WITHDRAWAL

    val icon = when (transaction.type) {
        TransactionType.EARNING -> Icons.Default.WifiTethering
        TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
        TransactionType.BONUS -> Icons.Default.CardGiftcard
        TransactionType.REFERRAL -> Icons.Default.GroupAdd
    }

    val iconColor = when (transaction.type) {
        TransactionType.EARNING -> TealGreen
        TransactionType.WITHDRAWAL -> GoldEarnings
        TransactionType.BONUS -> VibrantBlue
        TransactionType.REFERRAL -> SuccessGreen
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("tx_${transaction.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(width = 0.6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.type.name,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = when (transaction.type) {
                            TransactionType.EARNING -> "Data Monetization"
                            TransactionType.WITHDRAWAL -> "${transaction.paymentMethod?.displayName ?: "Payout"} Withdrawal"
                            TransactionType.BONUS -> "Daily Streak Bonus"
                            TransactionType.REFERRAL -> "Friend Referral Reward"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${transaction.date} • ${transaction.time}",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                    Text(
                        text = transaction.id,
                        fontSize = 10.sp,
                        color = Slate400
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isWithdrawal) "- " else "+ "}${EarningCalculator.formatPkr(transaction.amountPkr)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isWithdrawal) MaterialTheme.colorScheme.onSurface else TealGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                StatusChip(status = transaction.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: TransactionStatus) {
    val (bg, textColor) = when (status) {
        TransactionStatus.COMPLETED -> TealGreenBg to TealGreen
        TransactionStatus.PENDING -> GoldBg to GoldEarnings
        TransactionStatus.FAILED -> ErrorRed.copy(alpha = 0.12f) to ErrorRed
        TransactionStatus.CANCELLED -> Slate200 to Slate700
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DetailRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate400)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = Slate700
        )
    }
}
