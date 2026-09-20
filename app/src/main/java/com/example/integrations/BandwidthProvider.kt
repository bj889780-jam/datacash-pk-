package com.example.integrations

import com.example.core.AppConfig
import com.example.models.BandwidthSessionStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interface contract for internet bandwidth selling provider.
 *
 * NOTE: The current implementation is ONLY A MOCK/DEMO ENGINE.
 * It strictly does NOT route, proxy, inspect or transfer real user network traffic.
 * Real bandwidth functionality will be hooked up via authorized backend providers in the future.
 */
interface BandwidthProvider {
    val isSessionActive: Flow<Boolean>
    fun startSession(): Boolean
    fun pauseSession(): Boolean
    fun stopSession()
    fun getTransferredMB(): Double
    fun getSessionStats(): BandwidthSessionStats
}

/**
 * Safe mock bandwidth provider for UI and earning calculations testing.
 * Strictly simulated - NO actual network routing occurs.
 */
class MockBandwidthProvider : BandwidthProvider {

    private val _isActive = MutableStateFlow(false)
    override val isSessionActive: Flow<Boolean> = _isActive.asStateFlow()

    private var transferredMb: Double = 0.0
    private var durationSeconds: Long = 0L

    override fun startSession(): Boolean {
        _isActive.value = true
        return true
    }

    override fun pauseSession(): Boolean {
        _isActive.value = false
        return true
    }

    override fun stopSession() {
        _isActive.value = false
        transferredMb = 0.0
        durationSeconds = 0L
    }

    override fun getTransferredMB(): Double = transferredMb

    fun incrementSimulation(deltaMb: Double) {
        if (_isActive.value) {
            transferredMb += deltaMb
            durationSeconds += 1
        }
    }

    override fun getSessionStats(): BandwidthSessionStats {
        return BandwidthSessionStats(
            isSellingActive = _isActive.value,
            uploadSpeedMbps = if (_isActive.value) 2.4 + (0.5 * Math.sin(durationSeconds.toDouble())) else 0.0,
            downloadSpeedMbps = if (_isActive.value) 4.8 + (0.8 * Math.cos(durationSeconds.toDouble())) else 0.0,
            sessionDurationSeconds = durationSeconds,
            sessionTransferredMb = transferredMb,
            sessionEarningsPkr = transferredMb / AppConfig.MB_PER_RUPEE
        )
    }
}
