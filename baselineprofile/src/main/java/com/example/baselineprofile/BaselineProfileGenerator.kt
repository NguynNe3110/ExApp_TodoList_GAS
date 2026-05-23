package com.example.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @get:Rule
  val baselineProfileRule = BaselineProfileRule()

  @Test
  fun generate() {
    baselineProfileRule.collect(
      packageName = "com.example",
      includeInStartupProfile = true
    ) {
      pressHome()
      startActivityAndWait()

      // Warm up initial composition and font/layout caches.
      device.waitForIdle()
      val x = device.displayWidth / 2
      val top = (device.displayHeight * 0.20f).toInt()
      val bottom = (device.displayHeight * 0.82f).toInt()

      // Train scrolling path for Home list.
      repeat(8) {
        device.swipe(x, bottom, x, top, 22)
        device.waitForIdle()
      }
      repeat(8) {
        device.swipe(x, top, x, bottom, 22)
        device.waitForIdle()
      }

      // Train navigation animation path: Home -> Sync -> Home.
      val navX = (device.displayWidth * 0.84f).toInt()
      val navY = (device.displayHeight * 0.10f).toInt()
      device.click(navX, navY)
      device.waitForIdle()
      device.pressBack()
      device.waitForIdle()

      // Re-train scroll once more after navigation.
      repeat(4) {
        device.swipe(x, bottom, x, top, 22)
        device.waitForIdle()
      }
    }
  }
}
