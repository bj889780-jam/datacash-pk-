package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.AppConfig
import com.example.core.EarningCalculator
import com.example.data.DataCashRepository
import com.example.data.LocalDataCashRepository
import com.example.integrations.MockPaymentProvider
import com.example.integrations.PaymentProvider
import com.example.models.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    AUTH_LOGIN,
    AUTH_OTP,
    MAIN_APP,
    WITHDRAWAL_FORM,
    DAILY_BONUS,
    ACHIEVEMENTS,
    NOTIFICATIONS,
    SETTINGS,
    FAQ,
    PRIVACY_POLICY,
    TERMS_OF_SERVICE,
    DEMO_ADMIN
}

enum class HomeTab(val label: String) {
    HOME("Home"),
    WALLET("Wallet"),
    REFERRAL("Referral"),
    HISTORY("History"),
    PROFILE("Profile")
}

class DataCashViewModel(application: Application) : AndroidViewModel(application) {

    val repository: DataCashRepository = LocalDataCashRepository(application)
    private val paymentProvider: PaymentProvider = MockPaymentProvider()

    // Navigation state
    private val _currentDestination = MutableStateFlow(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _currentHomeTab = MutableStateFlow(HomeTab.HOME)
    val currentHomeTab: StateFlow<HomeTab> = _currentHomeTab.asStateFlow()

    // Theme state: null = system, true = dark, false = light
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    // UI feedback
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Simulation speed factor (1x, 2x, 5x, 10x for test flexibility)
    private val _simulationMultiplier = MutableStateFlow(1.0)
    val simulationMultiplier: StateFlow<Double> = _simulationMultiplier.asStateFlow()

    // Auth Form State
    private val _authPhoneInput = MutableStateFlow("+92 ")
    val authPhoneInput: StateFlow<String> = _authPhoneInput.asStateFlow()

    private val _authOtpInput = MutableStateFlow("")
    val authOtpInput: StateFlow<String> = _authOtpInput.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Active selling engine ticker job
    private var simulationJob: Job? = null

    // Streams from repo
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val sessionStats: StateFlow<BandwidthSessionStats> = repository.sessionStats
    val transactions: StateFlow<List<TransactionItem>> = repository.transactions
    val dailyBonusDays: StateFlow<List<DailyBonusDay>> = repository.dailyBonusDays
    val achievements: StateFlow<List<AchievementItem>> = repository.achievements
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
    val referralCount: StateFlow<Int> = repository.referralCount
    val referralEarnings: StateFlow<Double> = repository.referralEarnings

    init {
        // Automatically transition from splash after delay
        viewModelScope.launch {
            delay(1600)
            if (_currentDestination.value == AppDestination.SPLASH) {
                _currentDestination.value = AppDestination.MAIN_APP
            }
        }
    }

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun selectHomeTab(tab: HomeTab) {
        _currentHomeTab.value = tab
    }

    fun setDarkMode(dark: Boolean?) {
        _isDarkTheme.value = dark
    }

    fun setSimulationMultiplier(mult: Double) {
        _simulationMultiplier.value = mult.coerceIn(0.5, 20.0)
    }

    fun toggleSellingEngine() {
        val currentStats = sessionStats.value
        val dailyLimit = userProfile.value.dailyLimitMb
        if (currentStats.todayMbSold >= dailyLimit) {
            emitMessage("Daily MB limit ($dailyLimit MB) reached. Increase limit or wait until tomorrow.")
            return
        }

        val newActive = !currentStats.isSellingActive
        repository.setSellingActive(newActive)

        if (newActive) {
            startSimulationLoop()
            emitMessage("Demo Selling Active — Safe UI simulation running")
        } else {
            stopSimulationLoop()
            emitMessage("Selling paused")
        }
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (isActive) {
                delay(AppConfig.SIMULATION_TICK_MS)
                val currentStats = sessionStats.value
                val limit = userProfile.value.dailyLimitMb
                if (!currentStats.isSellingActive) break

                if (currentStats.todayMbSold >= limit) {
                    repository.setSellingActive(false)
                    emitMessage("Daily MB Limit Reached (${limit.toInt()} MB). Selling auto-paused.")
                    break
                }

                // Add simulated chunk
                val stepMb = AppConfig.DEFAULT_SIMULATED_MB_PER_SECOND * _simulationMultiplier.value
                repository.addSimulatedMb(stepMb)
            }
        }
    }

    private fun stopSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = null
    }

    fun setDailyLimit(limitMb: Double) {
        repository.setDailyMbLimit(limitMb)
        emitMessage("Daily limit set to ${limitMb.toInt()} MB")
    }

    fun setWifiOnlyMode(wifiOnly: Boolean) {
        repository.setWifiOnlyMode(wifiOnly)
        val mode = if (wifiOnly) "Wi-Fi Only Mode Enabled" else "Mobile Data + Wi-Fi Mode Enabled"
        emitMessage(mode)
    }

    fun updateProfile(name: String, phone: String, email: String) {
        val current = userProfile.value
        val updated = current.copy(fullName = name, phoneNumber = phone, email = email)
        repository.updateUserProfile(updated)
        emitMessage("Profile updated successfully")
    }

    fun onPhoneInputChanged(input: String) {
        _authPhoneInput.value = input
    }

    fun onOtpInputChanged(input: String) {
        if (input.length <= 6) {
            _authOtpInput.value = input
        }
    }

    fun sendPhoneOtp() {
        if (_authPhoneInput.value.length < 10) {
            emitMessage("Please enter a valid Pakistani phone number")
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            delay(800) // mock network
            _isAuthLoading.value = false
            _currentDestination.value = AppDestination.AUTH_OTP
            emitMessage("Demo OTP sent: 123456")
        }
    }

    fun verifyOtpAndLogin() {
        if (_authOtpInput.value.length < 4) {
            emitMessage("Please enter the 6-digit verification code")
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            delay(600)
            _isAuthLoading.value = false
            _currentDestination.value = AppDestination.MAIN_APP
            emitMessage("Welcome back to DataCash PK!")
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            delay(700)
            _isAuthLoading.value = false
            _currentDestination.value = AppDestination.MAIN_APP
            emitMessage("Signed in with Google (Demo)")
        }
    }

    fun logout() {
        stopSimulationLoop()
        repository.setSellingActive(false)
        _currentDestination.value = AppDestination.AUTH_LOGIN
        emitMessage("Logged out")
    }

    fun requestWithdrawal(
        amount: Double,
        method: PaymentMethod,
        accountTitle: String,
        accountNumber: String,
        onSuccess: (TransactionItem) -> Unit
    ) {
        viewModelScope.launch {
            val available = sessionStats.value.availableBalancePkr
            if (amount < AppConfig.MIN_WITHDRAWAL_PKR) {
                emitMessage("Minimum withdrawal is ${EarningCalculator.formatPkr(AppConfig.MIN_WITHDRAWAL_PKR)}")
                return@launch
            }
            if (amount > available) {
                emitMessage("Withdrawal amount exceeds available balance (${EarningCalculator.formatPkr(available)})")
                return@launch
            }

            val result = paymentProvider.createWithdrawal(amount, method, accountTitle, accountNumber)
            result.onSuccess { tx ->
                repository.submitWithdrawal(tx)
                emitMessage("Withdrawal request submitted! Status: Pending (Demo)")
                onSuccess(tx)
            }.onFailure { error ->
                emitMessage(error.message ?: "Failed to process withdrawal")
            }
        }
    }

    fun claimBonus(dayNumber: Int) {
        val success = repository.claimDailyBonus(dayNumber)
        if (success) {
            emitMessage("Day $dayNumber Bonus claimed successfully!")
        } else {
            emitMessage("Bonus is not available to claim yet")
        }
    }

    fun simulateNewReferral() {
        val friend = repository.simulateReferralJoin()
        emitMessage("Demo: $friend joined with your code! Rs. ${AppConfig.REFERRAL_REWARD_PKR.toInt()} earned.")
    }

    fun markNotifRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun clearNotifs() {
        repository.clearAllNotifications()
        emitMessage("Notifications cleared")
    }

    fun resetAllDemoData() {
        stopSimulationLoop()
        repository.resetDemoData()
        emitMessage("Demo data reset to initial values")
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulationLoop()
    }
}
