package com.example

import com.example.core.AppConfig
import com.example.core.EarningCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun earning_calculations_match_strict_formula() {
    // Rate: 3 MB = Rs. 1 => Earnings = MB Sold / 3
    assertEquals(1.0, EarningCalculator.calculateEarnings(3.0), 0.001)
    assertEquals(10.0, EarningCalculator.calculateEarnings(30.0), 0.001)
    assertEquals(100.0, EarningCalculator.calculateEarnings(300.0), 0.001)
    assertEquals(400.0, EarningCalculator.calculateEarnings(1200.0), 0.001)
    assertEquals(1000.0, EarningCalculator.calculateEarnings(3000.0), 0.001)
    assertEquals(4000.0, EarningCalculator.calculateEarnings(12000.0), 0.001)
  }

  @Test
  fun withdrawal_received_deducts_50_pkr_admin_fee() {
    // Formula: You Receive = Withdrawal Amount - Rs. 50
    assertEquals(450.0, EarningCalculator.calculateWithdrawalReceived(500.0), 0.001)
    assertEquals(950.0, EarningCalculator.calculateWithdrawalReceived(1000.0), 0.001)
    assertEquals(2200.0, EarningCalculator.calculateWithdrawalReceived(2250.0), 0.001)
  }

  @Test
  fun remaining_mb_calculation_is_accurate() {
    val remaining = EarningCalculator.calculateRemainingMb(
      dailyLimitMb = AppConfig.MAX_DAILY_MB,
      soldMb = 1200.0
    )
    assertEquals(10800.0, remaining, 0.001)
  }
}

