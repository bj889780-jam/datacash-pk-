package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.TransactionItem
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import com.example.viewmodel.DataCashViewModel
import com.example.viewmodel.HomeTab
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DataCashApp(
    viewModel: DataCashViewModel
) {
    val destination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val homeTab by viewModel.currentHomeTab.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val sessionStats by viewModel.sessionStats.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val dailyBonusDays by viewModel.dailyBonusDays.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val referralCount by viewModel.referralCount.collectAsStateWithLifecycle()
    val referralEarnings by viewModel.referralEarnings.collectAsStateWithLifecycle()
    val simulationMultiplier by viewModel.simulationMultiplier.collectAsStateWithLifecycle()

    val authPhone by viewModel.authPhoneInput.collectAsStateWithLifecycle()
    val authOtp by viewModel.authOtpInput.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    MyApplicationTheme(
        darkTheme = when (isDarkTheme) {
            true -> true
            false -> false
            null -> androidx.compose.foundation.isSystemInDarkTheme()
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (destination == AppDestination.MAIN_APP) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        HomeTab.values().forEach { tab ->
                            val isSelected = homeTab == tab
                            val (icon, selectedIcon) = when (tab) {
                                HomeTab.HOME -> Icons.Outlined.Home to Icons.Filled.Home
                                HomeTab.WALLET -> Icons.Outlined.AccountBalanceWallet to Icons.Filled.AccountBalanceWallet
                                HomeTab.REFERRAL -> Icons.Outlined.Share to Icons.Filled.Share
                                HomeTab.HISTORY -> Icons.Outlined.ReceiptLong to Icons.Filled.ReceiptLong
                                HomeTab.PROFILE -> Icons.Outlined.Person to Icons.Filled.Person
                            }

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectHomeTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) selectedIcon else icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TealGreen,
                                    selectedTextColor = TealGreen,
                                    indicatorColor = TealGreenBg
                                ),
                                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (destination) {
                    AppDestination.SPLASH -> {
                        SplashScreen(
                            onSplashFinished = {
                                viewModel.navigateTo(AppDestination.MAIN_APP)
                            }
                        )
                    }
                    AppDestination.ONBOARDING -> {
                        OnboardingScreen(
                            onFinishOnboarding = {
                                viewModel.navigateTo(AppDestination.AUTH_LOGIN)
                            }
                        )
                    }
                    AppDestination.AUTH_LOGIN -> {
                        AuthLoginScreen(
                            phoneInput = authPhone,
                            onPhoneChanged = { viewModel.onPhoneInputChanged(it) },
                            isLoading = isAuthLoading,
                            onSendOtp = { viewModel.sendPhoneOtp() },
                            onGoogleSignIn = { viewModel.loginWithGoogle() },
                            onSkipToDashboard = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.AUTH_OTP -> {
                        AuthOtpScreen(
                            phoneNumber = authPhone,
                            otpInput = authOtp,
                            onOtpChanged = { viewModel.onOtpInputChanged(it) },
                            isLoading = isAuthLoading,
                            onVerify = { viewModel.verifyOtpAndLogin() },
                            onBack = { viewModel.navigateTo(AppDestination.AUTH_LOGIN) }
                        )
                    }
                    AppDestination.MAIN_APP -> {
                        when (homeTab) {
                            HomeTab.HOME -> {
                                HomeScreen(
                                    userProfile = userProfile,
                                    sessionStats = sessionStats,
                                    onToggleSelling = { viewModel.toggleSellingEngine() },
                                    onOpenWithdrawal = { viewModel.navigateTo(AppDestination.WITHDRAWAL_FORM) },
                                    onOpenDailyBonus = { viewModel.navigateTo(AppDestination.DAILY_BONUS) },
                                    onOpenReferral = { viewModel.selectHomeTab(HomeTab.REFERRAL) },
                                    onOpenNotifications = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                                    onOpenAdmin = { viewModel.navigateTo(AppDestination.DEMO_ADMIN) },
                                    onSetDailyLimit = { viewModel.setDailyLimit(it) },
                                    unreadNotificationsCount = notifications.count { !it.isRead }
                                )
                            }
                            HomeTab.WALLET -> {
                                WalletScreen(
                                    sessionStats = sessionStats,
                                    transactions = transactions,
                                    onOpenWithdrawal = { viewModel.navigateTo(AppDestination.WITHDRAWAL_FORM) },
                                    onViewAllHistory = { viewModel.selectHomeTab(HomeTab.HISTORY) }
                                )
                            }
                            HomeTab.REFERRAL -> {
                                ReferralScreen(
                                    referralCode = userProfile.referralCode,
                                    totalReferrals = referralCount,
                                    referralEarnings = referralEarnings,
                                    onSimulateReferral = { viewModel.simulateNewReferral() }
                                )
                            }
                            HomeTab.HISTORY -> {
                                HistoryScreen(transactions = transactions)
                            }
                            HomeTab.PROFILE -> {
                                ProfileScreen(
                                    userProfile = userProfile,
                                    onEditProfile = { name, phone, email ->
                                        viewModel.updateProfile(name, phone, email)
                                    },
                                    onOpenSettings = { viewModel.navigateTo(AppDestination.SETTINGS) },
                                    onOpenDailyBonus = { viewModel.navigateTo(AppDestination.DAILY_BONUS) },
                                    onOpenAchievements = { viewModel.navigateTo(AppDestination.ACHIEVEMENTS) },
                                    onOpenFaq = { viewModel.navigateTo(AppDestination.FAQ) },
                                    onOpenPrivacy = { viewModel.navigateTo(AppDestination.PRIVACY_POLICY) },
                                    onOpenTerms = { viewModel.navigateTo(AppDestination.TERMS_OF_SERVICE) },
                                    onLogout = { viewModel.logout() }
                                )
                            }
                        }
                    }
                    AppDestination.WITHDRAWAL_FORM -> {
                        WithdrawalScreen(
                            availableBalance = sessionStats.availableBalancePkr,
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) },
                            onSubmitWithdrawal = { amount, method, title, number, onSuccess ->
                                viewModel.requestWithdrawal(amount, method, title, number, onSuccess)
                            }
                        )
                    }
                    AppDestination.DAILY_BONUS -> {
                        DailyBonusScreen(
                            bonusDays = dailyBonusDays,
                            onClaimBonus = { viewModel.claimBonus(it) },
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.ACHIEVEMENTS -> {
                        AchievementsScreen(
                            achievements = achievements,
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.NOTIFICATIONS -> {
                        NotificationsScreen(
                            notifications = notifications,
                            onMarkAsRead = { viewModel.markNotifRead(it) },
                            onClearAll = { viewModel.clearNotifs() },
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.SETTINGS -> {
                        SettingsScreen(
                            userProfile = userProfile,
                            isDarkMode = isDarkTheme,
                            onSetDarkMode = { viewModel.setDarkMode(it) },
                            onSetDailyLimit = { viewModel.setDailyLimit(it) },
                            onSetWifiOnly = { viewModel.setWifiOnlyMode(it) },
                            onOpenAdmin = { viewModel.navigateTo(AppDestination.DEMO_ADMIN) },
                            onResetDemoData = { viewModel.resetAllDemoData() },
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.FAQ -> {
                        FaqScreen(onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) })
                    }
                    AppDestination.PRIVACY_POLICY -> {
                        LegalScreen(
                            initialTab = LegalTab.PRIVACY,
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.TERMS_OF_SERVICE -> {
                        LegalScreen(
                            initialTab = LegalTab.TERMS,
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                    AppDestination.DEMO_ADMIN -> {
                        DemoAdminScreen(
                            currentMultiplier = simulationMultiplier,
                            onSetMultiplier = { viewModel.setSimulationMultiplier(it) },
                            onSimulateReferral = { viewModel.simulateNewReferral() },
                            onAddTestMb = { mb -> viewModel.repository.addSimulatedMb(mb) },
                            onResetData = { viewModel.resetAllDemoData() },
                            onBack = { viewModel.navigateTo(AppDestination.MAIN_APP) }
                        )
                    }
                }
            }
        }
    }
}
