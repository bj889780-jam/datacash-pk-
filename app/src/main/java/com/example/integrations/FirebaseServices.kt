package com.example.integrations

import com.example.models.UserProfile

/**
 * Firebase-ready Authentication Service interface.
 * Decouples the UI from direct SDK calls and supports seamless mock fallback.
 */
interface AuthService {
    suspend fun signInWithPhone(phoneNumber: String): Result<String> // returns verificationId
    suspend fun verifyOtp(verificationId: String, otpCode: String): Result<UserProfile>
    suspend fun signInWithGoogle(idToken: String? = null): Result<UserProfile>
    suspend fun signOut(): Boolean
    fun getCurrentUser(): UserProfile?
}

/**
 * Local mock implementation for prototype testing without requiring Firebase console credentials.
 */
class MockAuthService : AuthService {
    private var currentUser: UserProfile? = UserProfile()

    override suspend fun signInWithPhone(phoneNumber: String): Result<String> {
        return Result.success("mock-verification-id-7821")
    }

    override suspend fun verifyOtp(verificationId: String, otpCode: String): Result<UserProfile> {
        if (otpCode.length == 6 || otpCode == "123456" || otpCode.isNotEmpty()) {
            currentUser = UserProfile(
                fullName = "Muhammad Ali",
                phoneNumber = "+92 300 1234567"
            )
            return Result.success(currentUser!!)
        }
        return Result.failure(IllegalArgumentException("Invalid verification code"))
    }

    override suspend fun signInWithGoogle(idToken: String?): Result<UserProfile> {
        currentUser = UserProfile(
            fullName = "Muhammad Ali",
            email = "ali.datacash@gmail.com",
            phoneNumber = "+92 300 1234567"
        )
        return Result.success(currentUser!!)
    }

    override suspend fun signOut(): Boolean {
        currentUser = null
        return true
    }

    override fun getCurrentUser(): UserProfile? = currentUser
}

/**
 * Interface contract for Firestore database synchronization.
 */
interface FirestoreSyncService {
    suspend fun syncUserProfile(user: UserProfile): Boolean
    suspend fun fetchRealtimeEarningRate(): Double
}

/**
 * Interface contract for Analytics logging.
 */
interface AppAnalyticsService {
    fun logEvent(eventName: String, params: Map<String, String> = emptyMap())
}

class MockAnalyticsService : AppAnalyticsService {
    override fun logEvent(eventName: String, params: Map<String, String>) {
        // Log locally for demo verification
    }
}
