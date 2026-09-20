package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Interface contract for application state persistence and data operations.
 */
interface DataCashRepository {
    val userProfile: StateFlow<UserProfile>
    val sessionStats: StateFlow<BandwidthSessionStats>
    val transactions: StateFlow<List<TransactionItem>>
    val dailyBonusDays: StateFlow<List<DailyBonusDay>>
    val achievements: StateFlow<List<AchievementItem>>
    val notifications: StateFlow<List<NotificationItem>>
    val referralCount: StateFlow<Int>
    val referralEarnings: StateFlow<Double>

    fun updateUserProfile(profile: UserProfile)
    fun setDailyMbLimit(limitMb: Double)
    fun setWifiOnlyMode(wifiOnly: Boolean)
    fun setSellingActive(active: Boolean)
    fun addSimulatedMb(mb: Double)
    fun submitWithdrawal(transaction: TransactionItem)
    fun claimDailyBonus(dayNumber: Int): Boolean
    fun simulateReferralJoin(): String
    fun markNotificationAsRead(id: String)
    fun clearAllNotifications()
    fun resetDemoData()
}

/**
 * Robust local implementation backed by SharedPreferences for persistent demo storage.
 */
class LocalDataCashRepository(private val context: Context) : DataCashRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("datacash_pk_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadUserProfile())
    override val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _sessionStats = MutableStateFlow(loadInitialStats())
    override val sessionStats: StateFlow<BandwidthSessionStats> = _sessionStats.asStateFlow()

    private val _transactions = MutableStateFlow(loadInitialTransactions())
    override val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    private val _dailyBonusDays = MutableStateFlow(loadInitialDailyBonus())
    override val dailyBonusDays: StateFlow<List<DailyBonusDay>> = _dailyBonusDays.asStateFlow()

    private val _achievements = MutableStateFlow(loadInitialAchievements())
    override val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    private val _notifications = MutableStateFlow(loadInitialNotifications())
    override val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _referralCount = MutableStateFlow(prefs.getInt("referral_count", 6))
    override val referralCount: StateFlow<Int> = _referralCount.asStateFlow()

    private val _referralEarnings = MutableStateFlow(
        prefs.getFloat("referral_earnings", (6 * AppConfig.REFERRAL_REWARD_PKR).toFloat()).toDouble()
    )
    override val referralEarnings: StateFlow<Double> = _referralEarnings.asStateFlow()

    private fun loadUserProfile(): UserProfile {
        return UserProfile(
            fullName = prefs.getString("user_name", "Muhammad Ali") ?: "Muhammad Ali",
            phoneNumber = prefs.getString("user_phone", "+92 300 1234567") ?: "+92 300 1234567",
            email = prefs.getString("user_email", "ali.datacash@gmail.com") ?: "ali.datacash@gmail.com",
            referralCode = prefs.getString("user_ref_code", "DATAPK88") ?: "DATAPK88",
            joinedDate = "15 Jan 2026",
            accountStatus = AccountStatus.DEMO_MODE,
            isWifiOnly = prefs.getBoolean("wifi_only", false),
            dailyLimitMb = prefs.getFloat("daily_limit_mb", 12000f).toDouble()
        )
    }

    private fun loadInitialStats(): BandwidthSessionStats {
        val sold = prefs.getFloat("today_mb_sold", 1200f).toDouble()
        val balance = prefs.getFloat("available_balance", 1250f).toDouble()
        val pending = prefs.getFloat("pending_earnings", 100f).toDouble()
        val totalEarned = prefs.getFloat("total_earned", 4500f).toDouble()
        val totalWithdrawn = prefs.getFloat("total_withdrawn", 3250f).toDouble()

        return BandwidthSessionStats(
            isSellingActive = false,
            uploadSpeedMbps = 2.4,
            downloadSpeedMbps = 4.8,
            sessionDurationSeconds = 0L,
            sessionTransferredMb = 0.0,
            sessionEarningsPkr = 0.0,
            todayMbSold = sold,
            todayEarningsPkr = EarningCalculator.calculateEarnings(sold),
            availableBalancePkr = balance,
            pendingEarningsPkr = pending,
            totalEarnedPkr = totalEarned,
            totalWithdrawnPkr = totalWithdrawn,
            isWifiOnly = prefs.getBoolean("wifi_only", false),
            isOffline = false
        )
    }

    private fun loadInitialTransactions(): List<TransactionItem> {
        return listOf(
            TransactionItem(
                id = "DC-TX-98214",
                date = "19 Sep 2026",
                time = "02:15 PM",
                amountPkr = 400.0,
                type = TransactionType.EARNING,
                status = TransactionStatus.COMPLETED,
                description = "1,200 MB data monetization reward (3 MB = Rs. 1)"
            ),
            TransactionItem(
                id = "DC-TX-95102",
                date = "18 Sep 2026",
                time = "06:30 PM",
                amountPkr = 1000.0,
                type = TransactionType.WITHDRAWAL,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.EASYPAISA,
                accountTitle = "Muhammad Ali",
                accountNumber = "03001234567",
                description = "EasyPaisa payout - Admin fee Rs. 50 deducted"
            ),
            TransactionItem(
                id = "DC-TX-92041",
                date = "18 Sep 2026",
                time = "10:00 AM",
                amountPkr = 15.0,
                type = TransactionType.BONUS,
                status = TransactionStatus.COMPLETED,
                description = "Day 2 Daily Check-in Streak Reward"
            ),
            TransactionItem(
                id = "DC-TX-88491",
                date = "17 Sep 2026",
                time = "04:45 PM",
                amountPkr = 50.0,
                type = TransactionType.REFERRAL,
                status = TransactionStatus.COMPLETED,
                description = "Referral bonus for inviting Usman Khan"
            ),
            TransactionItem(
                id = "DC-TX-82301",
                date = "16 Sep 2026",
                time = "08:12 PM",
                amountPkr = 2250.0,
                type = TransactionType.WITHDRAWAL,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.JAZZCASH,
                accountTitle = "Muhammad Ali",
                accountNumber = "03001234567",
                description = "JazzCash payout - Admin fee Rs. 50 deducted"
            )
        )
    }

    private fun loadInitialDailyBonus(): List<DailyBonusDay> {
        return listOf(
            DailyBonusDay(dayNumber = 1, rewardPkr = 10.0, status = BonusClaimStatus.CLAIMED),
            DailyBonusDay(dayNumber = 2, rewardPkr = 15.0, status = BonusClaimStatus.CLAIMED),
            DailyBonusDay(dayNumber = 3, rewardPkr = 20.0, status = BonusClaimStatus.AVAILABLE),
            DailyBonusDay(dayNumber = 4, rewardPkr = 25.0, status = BonusClaimStatus.LOCKED),
            DailyBonusDay(dayNumber = 5, rewardPkr = 30.0, status = BonusClaimStatus.LOCKED),
            DailyBonusDay(dayNumber = 6, rewardPkr = 40.0, status = BonusClaimStatus.LOCKED),
            DailyBonusDay(dayNumber = 7, rewardPkr = 50.0, status = BonusClaimStatus.LOCKED)
        )
    }

    private fun loadInitialAchievements(): List<AchievementItem> {
        val currentMb = _sessionStats.value.todayMbSold
        return listOf(
            AchievementItem(
                id = "first_mb",
                title = "First MB Sold",
                description = "Complete your first data monetization session",
                currentProgress = currentMb.coerceAtMost(1.0),
                maxProgress = 1.0,
                isUnlocked = currentMb >= 1.0,
                rewardPkr = 5.0
            ),
            AchievementItem(
                id = "mb_100",
                title = "100 MB Sold",
                description = "Sell 100 MB of simulated bandwidth",
                currentProgress = currentMb.coerceAtMost(100.0),
                maxProgress = 100.0,
                isUnlocked = currentMb >= 100.0,
                rewardPkr = 15.0
            ),
            AchievementItem(
                id = "mb_1000",
                title = "1,000 MB Sold",
                description = "Reach 1,000 MB milestone in a single day",
                currentProgress = currentMb.coerceAtMost(1000.0),
                maxProgress = 1000.0,
                isUnlocked = currentMb >= 1000.0,
                rewardPkr = 50.0
            ),
            AchievementItem(
                id = "mb_5000",
                title = "5,000 MB Sold",
                description = "High volume bandwidth contributor",
                currentProgress = currentMb.coerceAtMost(5000.0),
                maxProgress = 5000.0,
                isUnlocked = currentMb >= 5000.0,
                rewardPkr = 150.0
            ),
            AchievementItem(
                id = "mb_10000",
                title = "10,000 MB Sold",
                description = "Master bandwidth contributor",
                currentProgress = currentMb.coerceAtMost(10000.0),
                maxProgress = 10000.0,
                isUnlocked = currentMb >= 10000.0,
                rewardPkr = 300.0
            ),
            AchievementItem(
                id = "first_withdrawal",
                title = "First Withdrawal",
                description = "Successfully submit your first payout request",
                currentProgress = 1.0,
                maxProgress = 1.0,
                isUnlocked = true,
                rewardPkr = 20.0
            ),
            AchievementItem(
                id = "first_referral",
                title = "First Referral",
                description = "Invite a friend to DataCash PK using your code",
                currentProgress = 1.0,
                maxProgress = 1.0,
                isUnlocked = true,
                rewardPkr = 50.0
            ),
            AchievementItem(
                id = "streak_7",
                title = "7-Day Streak",
                description = "Check-in 7 days consecutively",
                currentProgress = 2.0,
                maxProgress = 7.0,
                isUnlocked = false,
                rewardPkr = 100.0
            )
        )
    }

    private fun loadInitialNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "notif-1",
                title = "Daily Bonus Available!",
                message = "Day 3 check-in is ready to claim. Claim Rs. 20 now.",
                timestamp = "Just now",
                category = "Bonus"
            ),
            NotificationItem(
                id = "notif-2",
                title = "Withdrawal Completed (Demo)",
                message = "Your Rs. 1,000 EasyPaisa withdrawal has been simulated as completed.",
                timestamp = "Yesterday",
                category = "Wallet"
            ),
            NotificationItem(
                id = "notif-3",
                title = "Referral Reward Credited",
                message = "You earned Rs. 50 because your friend joined using DATAPK88.",
                timestamp = "2 days ago",
                category = "Referral"
            ),
            NotificationItem(
                id = "notif-4",
                title = "Safe Demo Mode Reminder",
                message = "DataCash PK is currently operating in safe UI demo mode. No internet traffic is routed.",
                timestamp = "3 days ago",
                category = "System"
            )
        )
    }

    override fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs.edit()
            .putString("user_name", profile.fullName)
            .putString("user_phone", profile.phoneNumber)
            .putString("user_email", profile.email)
            .putFloat("daily_limit_mb", profile.dailyLimitMb.toFloat())
            .putBoolean("wifi_only", profile.isWifiOnly)
            .apply()
    }

    override fun setDailyMbLimit(limitMb: Double) {
        val safeLimit = limitMb.coerceIn(500.0, AppConfig.MAX_DAILY_MB)
        val updated = _userProfile.value.copy(dailyLimitMb = safeLimit)
        updateUserProfile(updated)
    }

    override fun setWifiOnlyMode(wifiOnly: Boolean) {
        val updated = _userProfile.value.copy(isWifiOnly = wifiOnly)
        updateUserProfile(updated)
        _sessionStats.value = _sessionStats.value.copy(isWifiOnly = wifiOnly)
    }

    override fun setSellingActive(active: Boolean) {
        val current = _sessionStats.value
        _sessionStats.value = current.copy(
            isSellingActive = active,
            uploadSpeedMbps = if (active) 2.4 else 0.0,
            downloadSpeedMbps = if (active) 4.8 else 0.0
        )
    }

    override fun addSimulatedMb(mb: Double) {
        val current = _sessionStats.value
        val dailyLimit = _userProfile.value.dailyLimitMb

        // Ensure we do not exceed dailyLimit
        val newTodayMb = (current.todayMbSold + mb).coerceAtMost(dailyLimit)
        val deltaMb = newTodayMb - current.todayMbSold

        if (deltaMb <= 0.0) {
            // Reached limit
            setSellingActive(false)
            return
        }

        val deltaEarnings = deltaMb / AppConfig.MB_PER_RUPEE
        val newTodayEarnings = current.todayEarningsPkr + deltaEarnings
        val newBalance = current.availableBalancePkr + deltaEarnings
        val newTotalEarned = current.totalEarnedPkr + deltaEarnings

        val newSessionMb = current.sessionTransferredMb + deltaMb
        val newSessionDuration = current.sessionDurationSeconds + 1
        val newSessionEarnings = newSessionMb / AppConfig.MB_PER_RUPEE

        _sessionStats.value = current.copy(
            todayMbSold = newTodayMb,
            todayEarningsPkr = newTodayEarnings,
            availableBalancePkr = newBalance,
            totalEarnedPkr = newTotalEarned,
            sessionTransferredMb = newSessionMb,
            sessionEarningsPkr = newSessionEarnings,
            sessionDurationSeconds = newSessionDuration,
            uploadSpeedMbps = 2.4 + (0.6 * Math.sin(newSessionDuration.toDouble())),
            downloadSpeedMbps = 4.8 + (1.1 * Math.cos(newSessionDuration.toDouble()))
        )

        // Persist
        prefs.edit()
            .putFloat("today_mb_sold", newTodayMb.toFloat())
            .putFloat("available_balance", newBalance.toFloat())
            .putFloat("total_earned", newTotalEarned.toFloat())
            .apply()
    }

    override fun submitWithdrawal(transaction: TransactionItem) {
        val current = _sessionStats.value
        if (transaction.amountPkr > current.availableBalancePkr) return

        val newBalance = current.availableBalancePkr - transaction.amountPkr
        val newPending = current.pendingEarningsPkr // remains or tracked
        val newTotalWithdrawn = current.totalWithdrawnPkr + transaction.amountPkr

        _sessionStats.value = current.copy(
            availableBalancePkr = newBalance,
            totalWithdrawnPkr = newTotalWithdrawn
        )

        val updatedList = listOf(transaction) + _transactions.value
        _transactions.value = updatedList

        // Add a notification
        val notif = NotificationItem(
            id = "notif-w-${System.currentTimeMillis()}",
            title = "Withdrawal Submitted (${transaction.paymentMethod?.displayName ?: "Bank"})",
            message = "Rs. ${transaction.amountPkr.toInt()} withdrawal is pending. You will receive Rs. ${EarningCalculator.calculateWithdrawalReceived(transaction.amountPkr).toInt()} after Rs. 50 admin fee.",
            timestamp = "Just now",
            category = "Wallet"
        )
        _notifications.value = listOf(notif) + _notifications.value

        prefs.edit()
            .putFloat("available_balance", newBalance.toFloat())
            .putFloat("total_withdrawn", newTotalWithdrawn.toFloat())
            .apply()
    }

    override fun claimDailyBonus(dayNumber: Int): Boolean {
        val days = _dailyBonusDays.value.toMutableList()
        val index = days.indexOfFirst { it.dayNumber == dayNumber }
        if (index != -1 && days[index].status == BonusClaimStatus.AVAILABLE) {
            val reward = days[index].rewardPkr
            days[index] = days[index].copy(status = BonusClaimStatus.CLAIMED)
            if (index + 1 < days.size && days[index + 1].status == BonusClaimStatus.LOCKED) {
                days[index + 1] = days[index + 1].copy(status = BonusClaimStatus.AVAILABLE)
            }
            _dailyBonusDays.value = days

            // Credit balance
            val current = _sessionStats.value
            _sessionStats.value = current.copy(
                availableBalancePkr = current.availableBalancePkr + reward,
                totalEarnedPkr = current.totalEarnedPkr + reward
            )

            // Add transaction
            val now = Date()
            val tx = TransactionItem(
                id = "DC-TX-${(10000..99999).random()}",
                date = SimpleDateFormat("dd MMM yyyy", Locale.US).format(now),
                time = SimpleDateFormat("hh:mm a", Locale.US).format(now),
                amountPkr = reward,
                type = TransactionType.BONUS,
                status = TransactionStatus.COMPLETED,
                description = "Day $dayNumber Daily Check-in Bonus"
            )
            _transactions.value = listOf(tx) + _transactions.value

            return true
        }
        return false
    }

    override fun simulateReferralJoin(): String {
        val newCount = _referralCount.value + 1
        val newEarnings = _referralEarnings.value + AppConfig.REFERRAL_REWARD_PKR
        _referralCount.value = newCount
        _referralEarnings.value = newEarnings

        val current = _sessionStats.value
        _sessionStats.value = current.copy(
            availableBalancePkr = current.availableBalancePkr + AppConfig.REFERRAL_REWARD_PKR,
            totalEarnedPkr = current.totalEarnedPkr + AppConfig.REFERRAL_REWARD_PKR
        )

        prefs.edit()
            .putInt("referral_count", newCount)
            .putFloat("referral_earnings", newEarnings.toFloat())
            .putFloat("available_balance", _sessionStats.value.availableBalancePkr.toFloat())
            .apply()

        // Transaction
        val now = Date()
        val tx = TransactionItem(
            id = "DC-TX-${(10000..99999).random()}",
            date = SimpleDateFormat("dd MMM yyyy", Locale.US).format(now),
            time = SimpleDateFormat("hh:mm a", Locale.US).format(now),
            amountPkr = AppConfig.REFERRAL_REWARD_PKR,
            type = TransactionType.REFERRAL,
            status = TransactionStatus.COMPLETED,
            description = "Referral bonus for new friend join"
        )
        _transactions.value = listOf(tx) + _transactions.value

        return "Friend #${newCount}"
    }

    override fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    override fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    override fun resetDemoData() {
        prefs.edit().clear().apply()
        _userProfile.value = loadUserProfile()
        _sessionStats.value = loadInitialStats()
        _transactions.value = loadInitialTransactions()
        _dailyBonusDays.value = loadInitialDailyBonus()
        _achievements.value = loadInitialAchievements()
        _notifications.value = loadInitialNotifications()
        _referralCount.value = 6
        _referralEarnings.value = 6 * AppConfig.REFERRAL_REWARD_PKR
    }
}
