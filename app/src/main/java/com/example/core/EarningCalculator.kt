package com.example.core

import java.text.DecimalFormat
import java.util.Locale

/**
 * Precision calculation engine for DataCash PK.
 * Strict rule: 3 MB = Rs. 1 => Earnings = MB Sold / 3
 */
object EarningCalculator {

    private val pkrFormat = DecimalFormat("#,##0.00")
    private val pkrIntFormat = DecimalFormat("#,##0")
    private val mbFormat = DecimalFormat("#,##0")

    /**
     * Calculates gross earnings in PKR from MB sold.
     * Formula: Earnings = MB Sold / 3
     */
    fun calculateEarnings(soldMb: Double): Double {
        if (soldMb <= 0.0) return 0.0
        return soldMb / AppConfig.MB_PER_RUPEE
    }

    /**
     * Calculates remaining MB allowance for today based on configured daily limit.
     */
    fun calculateRemainingMb(dailyLimitMb: Double, soldMb: Double): Double {
        return (dailyLimitMb - soldMb).coerceAtLeast(0.0)
    }

    /**
     * Calculates net payout received by user after deducting admin fee.
     * Formula: You Receive = Withdrawal Amount - Rs. 50
     */
    fun calculateWithdrawalReceived(amount: Double): Double {
        return (amount - AppConfig.WITHDRAWAL_ADMIN_FEE_PKR).coerceAtLeast(0.0)
    }

    /**
     * Formats PKR amount with symbol and 2 decimals or integer if whole.
     */
    fun formatPkr(amount: Double, forceDecimals: Boolean = false): String {
        return if (forceDecimals || amount % 1.0 != 0.0) {
            "Rs. ${pkrFormat.format(amount)}"
        } else {
            "Rs. ${pkrIntFormat.format(amount)}"
        }
    }

    /**
     * Formats MB with commas.
     */
    fun formatMb(mb: Double): String {
        return "${mbFormat.format(mb)} MB"
    }

    /**
     * Formats duration in seconds to MM:SS or HH:MM:SS
     */
    fun formatDuration(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hrs > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
        } else {
            String.format(Locale.US, "%02d:%02d", mins, secs)
        }
    }
}
