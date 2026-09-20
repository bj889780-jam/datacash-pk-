package com.example.integrations

import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.models.PaymentMethod
import com.example.models.TransactionItem
import com.example.models.TransactionStatus
import com.example.models.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Interface contract for payment providers (EasyPaisa, JazzCash, 1Link Bank Transfer).
 * Future production builds connect to official authorized payment gateways with server-side validation.
 */
interface PaymentProvider {
    suspend fun createWithdrawal(
        amountPkr: Double,
        method: PaymentMethod,
        accountTitle: String,
        accountNumber: String
    ): Result<TransactionItem>

    suspend fun checkWithdrawalStatus(transactionId: String): TransactionStatus
    suspend fun cancelWithdrawal(transactionId: String): Boolean
}

/**
 * Mock implementation of PaymentProvider for safe prototype testing.
 * Enforces business rules:
 * - Minimum withdrawal: Rs. 500
 * - Rs. 50 admin fee clearly applied
 * - User receives = Withdrawal - Rs. 50
 * - Generates Demo Pending transaction
 */
class MockPaymentProvider : PaymentProvider {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    override suspend fun createWithdrawal(
        amountPkr: Double,
        method: PaymentMethod,
        accountTitle: String,
        accountNumber: String
    ): Result<TransactionItem> {
        if (amountPkr < AppConfig.MIN_WITHDRAWAL_PKR) {
            return Result.failure(
                IllegalArgumentException("Minimum withdrawal amount is ${EarningCalculator.formatPkr(AppConfig.MIN_WITHDRAWAL_PKR)}")
            )
        }
        if (accountTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Please enter the account title"))
        }
        if (accountNumber.isBlank() || accountNumber.length < 9) {
            return Result.failure(IllegalArgumentException("Please enter a valid mobile / account number"))
        }

        val now = Date()
        val randomSuffix = (10000..99999).random()
        val txId = "DC-TX-$randomSuffix"

        val item = TransactionItem(
            id = txId,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            amountPkr = amountPkr,
            type = TransactionType.WITHDRAWAL,
            status = TransactionStatus.PENDING,
            paymentMethod = method,
            accountTitle = accountTitle,
            accountNumber = accountNumber,
            description = "Withdrawal to ${method.displayName} ($accountNumber) - Admin Fee Rs. 50 applied"
        )

        return Result.success(item)
    }

    override suspend fun checkWithdrawalStatus(transactionId: String): TransactionStatus {
        return TransactionStatus.PENDING
    }

    override suspend fun cancelWithdrawal(transactionId: String): Boolean {
        return true
    }
}
