package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.models.PaymentMethod
import com.example.models.TransactionItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalScreen(
    availableBalance: Double,
    onBack: () -> Unit,
    onSubmitWithdrawal: (Double, PaymentMethod, String, String, (TransactionItem) -> Unit) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.EASYPAISA) }
    var amountInput by remember { mutableStateOf("500") }
    var accountTitle by remember { mutableStateOf("Muhammad Ali") }
    var accountNumber by remember { mutableStateOf("03001234567") }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var submittedTransaction by remember { mutableStateOf<TransactionItem?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val amountDouble = amountInput.toDoubleOrNull() ?: 0.0
    val adminFee = AppConfig.WITHDRAWAL_ADMIN_FEE_PKR
    val youReceive = EarningCalculator.calculateWithdrawalReceived(amountDouble)

    val isAmountValid = amountDouble >= AppConfig.MIN_WITHDRAWAL_PKR && amountDouble <= availableBalance

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Withdraw Funds",
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
            // Available Balance Reminder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Balance",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = EarningCalculator.formatPkr(availableBalance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TealGreen
                    )
                }
            }

            // Payment Method Selector
            Text(
                text = "Select Payout Method",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedMethod == method
                    val methodColor = when (method) {
                        PaymentMethod.EASYPAISA -> SuccessGreen
                        PaymentMethod.JAZZCASH -> ErrorRed
                        PaymentMethod.BANK_TRANSFER -> VibrantBlue
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = method }
                            .testTag("method_${method.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) methodColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) methodColor else Slate300)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) methodColor else Slate200),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (method) {
                                        PaymentMethod.EASYPAISA -> Icons.Default.PhoneAndroid
                                        PaymentMethod.JAZZCASH -> Icons.Default.AccountBalanceWallet
                                        PaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                                    },
                                    contentDescription = method.displayName,
                                    tint = if (isSelected) Color.White else Slate700,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = method.displayName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) methodColor else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Input Fields Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            amountInput = it
                            errorMessage = null
                        },
                        label = { Text("Withdrawal Amount (PKR)") },
                        placeholder = { Text("Min 500") },
                        leadingIcon = {
                            Text(
                                text = "Rs.",
                                fontWeight = FontWeight.Bold,
                                color = TealGreen,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdrawal_amount_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        supportingText = {
                            Text("Minimum withdrawal is Rs. 500")
                        }
                    )

                    OutlinedTextField(
                        value = accountTitle,
                        onValueChange = {
                            accountTitle = it
                            errorMessage = null
                        },
                        label = { Text("Account Title (Full Name)") },
                        placeholder = { Text("e.g. Muhammad Ali") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TealGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_title_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = {
                            accountNumber = it
                            errorMessage = null
                        },
                        label = {
                            Text(
                                if (selectedMethod == PaymentMethod.BANK_TRANSFER) "IBAN / Account Number" else "Mobile Account Number"
                            )
                        },
                        placeholder = {
                            Text(if (selectedMethod == PaymentMethod.BANK_TRANSFER) "PK36MEZN..." else "03001234567")
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Pin, contentDescription = null, tint = TealGreen)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_number_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // Fee and Payout Calculation Card (CRITICAL REQUIREMENT)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fee_breakdown_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Withdrawal Calculation Breakdown",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Withdrawal Amount:", fontSize = 13.sp, color = Slate700)
                        Text(
                            text = EarningCalculator.formatPkr(amountDouble),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Admin Processing Fee:", fontSize = 13.sp, color = ErrorRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = ErrorRed.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "FIXED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "- ${EarningCalculator.formatPkr(adminFee)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Slate300)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "You Receive:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (amountDouble >= adminFee) EarningCalculator.formatPkr(youReceive) else "Rs. 0",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Formula: You Receive = Withdrawal Amount - Rs. 50 admin fee",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }
            }

            if (errorMessage != null) {
                Surface(
                    color = ErrorRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = ErrorRed,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Proceed Button
            Button(
                onClick = {
                    when {
                        amountDouble < AppConfig.MIN_WITHDRAWAL_PKR -> {
                            errorMessage = "Minimum withdrawal amount is Rs. 500"
                        }
                        amountDouble > availableBalance -> {
                            errorMessage = "Withdrawal amount exceeds available balance (${EarningCalculator.formatPkr(availableBalance)})"
                        }
                        accountTitle.isBlank() -> {
                            errorMessage = "Please enter the account title"
                        }
                        accountNumber.length < 8 -> {
                            errorMessage = "Please enter a valid mobile / account number"
                        }
                        else -> {
                            errorMessage = null
                            showConfirmDialog = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_withdrawal_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen)
            ) {
                Text(
                    text = "Review & Confirm Withdrawal",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Confirm Withdrawal Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Please verify your payout details before submitting:",
                        fontSize = 12.sp,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    ConfirmRow("Payment Method", selectedMethod.displayName)
                    ConfirmRow("Account Title", accountTitle)
                    ConfirmRow("Account / Mobile", accountNumber)
                    ConfirmRow("Requested Amount", EarningCalculator.formatPkr(amountDouble))
                    ConfirmRow("Admin Fee", "- Rs. 50", isRed = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    ConfirmRow("Net Amount Received", EarningCalculator.formatPkr(youReceive), isBold = true)

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = GoldBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Status will be marked as Pending (Demo) in prototype mode.",
                            fontSize = 10.sp,
                            color = Slate700,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onSubmitWithdrawal(
                            amountDouble,
                            selectedMethod,
                            accountTitle,
                            accountNumber
                        ) { tx ->
                            submittedTransaction = tx
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen)
                ) {
                    Text("CONFIRM WITHDRAWAL", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = Slate700)
                }
            }
        )
    }

    // Success Result Dialog
    submittedTransaction?.let { tx ->
        AlertDialog(
            onDismissRequest = {
                submittedTransaction = null
                onBack()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TealGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Withdrawal Submitted",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Your withdrawal request has been registered in Demo Mode.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ConfirmRow("Transaction ID", tx.id)
                    ConfirmRow("Method", tx.paymentMethod?.displayName ?: "Payout")
                    ConfirmRow("Gross Amount", EarningCalculator.formatPkr(tx.amountPkr))
                    ConfirmRow("Admin Fee", "Rs. 50")
                    ConfirmRow("You Receive", EarningCalculator.formatPkr(EarningCalculator.calculateWithdrawalReceived(tx.amountPkr)), isBold = true)
                    ConfirmRow("Status", "Pending (Demo)")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        submittedTransaction = null
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen)
                ) {
                    Text("Back to Wallet", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun ConfirmRow(label: String, value: String, isRed: Boolean = false, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Slate400)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isRed) ErrorRed else if (isBold) TealGreen else Slate700
        )
    }
}
