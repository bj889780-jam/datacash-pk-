package com.example.core

/**
 * Centralized business constants and configuration for DataCash PK.
 */
object AppConfig {
    const val APP_NAME = "DataCash PK"
    const val CURRENCY_SYMBOL = "Rs."

    // Earning formula constants (CRITICAL: Never use a different formula)
    const val MB_PER_RUPEE = 3.0 // 3 MB = Rs. 1
    const val MAX_DAILY_MB = 12000.0 // 12,000 MB per day
    const val MAX_THEORETICAL_DAILY_EARNINGS = 4000.0 // 12,000 / 3 = Rs. 4,000

    // Withdrawal constants
    const val MIN_WITHDRAWAL_PKR = 500.0
    const val WITHDRAWAL_ADMIN_FEE_PKR = 50.0

    // Referral rewards (centralized configuration)
    const val REFERRAL_REWARD_PKR = 50.0

    // Daily Limit Selectable Options (MB)
    val DAILY_LIMIT_OPTIONS = listOf(500, 1000, 2000, 5000, 10000, 12000)

    // Demo simulation speeds (in MB per tick)
    const val SIMULATION_TICK_MS = 1000L
    const val DEFAULT_SIMULATED_MB_PER_SECOND = 4.2
}
