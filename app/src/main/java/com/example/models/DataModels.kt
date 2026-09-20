package com.example.models

enum class AccountStatus {
    ACTIVE,
    PAUSED,
    DEMO_MODE
}

data class UserProfile(
    val fullName: String = "Muhammad Ali",
    val phoneNumber: String = "+92 300 1234567",
    val email: String = "ali.datacash@gmail.com",
    val referralCode: String = "DATAPK88",
    val joinedDate: String = "15 Jan 2026",
    val accountStatus: AccountStatus = AccountStatus.DEMO_MODE,
    val isWifiOnly: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val dailyLimitMb: Double = 12000.0
)

enum class TransactionType {
    EARNING,
    WITHDRAWAL,
    BONUS,
    REFERRAL
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum class PaymentMethod(val displayName: String, val iconName: String) {
    EASYPAISA("EasyPaisa", "phone_android"),
    JAZZCASH("JazzCash", "account_balance_wallet"),
    BANK_TRANSFER("Bank Transfer", "account_balance")
}

data class TransactionItem(
    val id: String,
    val date: String,
    val time: String,
    val amountPkr: Double,
    val type: TransactionType,
    val status: TransactionStatus,
    val paymentMethod: PaymentMethod? = null,
    val accountTitle: String? = null,
    val accountNumber: String? = null,
    val description: String = ""
)

enum class BonusClaimStatus {
    CLAIMED,
    AVAILABLE,
    LOCKED
}

data class DailyBonusDay(
    val dayNumber: Int,
    val rewardPkr: Double,
    val status: BonusClaimStatus
)

data class AchievementItem(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Double,
    val maxProgress: Double,
    val isUnlocked: Boolean,
    val rewardPkr: Double
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val category: String = "General"
)

data class BandwidthSessionStats(
    val isSellingActive: Boolean = false,
    val uploadSpeedMbps: Double = 2.4,
    val downloadSpeedMbps: Double = 4.8,
    val sessionDurationSeconds: Long = 0L,
    val sessionTransferredMb: Double = 0.0,
    val sessionEarningsPkr: Double = 0.0,
    val todayMbSold: Double = 1200.0,
    val todayEarningsPkr: Double = 400.0,
    val availableBalancePkr: Double = 1250.0,
    val pendingEarningsPkr: Double = 100.0,
    val totalEarnedPkr: Double = 4500.0,
    val totalWithdrawnPkr: Double = 3250.0,
    val isWifiOnly: Boolean = false,
    val isOffline: Boolean = false
)
