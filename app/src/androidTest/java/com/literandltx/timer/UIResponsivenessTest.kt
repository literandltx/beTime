package com.literandltx.timer

import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.longClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.literandltx.betime.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class TimerUiResponsivenessTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun measureTimerClickResponseLatency() {
        composeTestRule.waitForIdle()

        val responseTimeMs = measureTimeMillis {
            composeTestRule.onNodeWithText("25:00").performClick()
            composeTestRule.waitForIdle()
        }

        println("Performance Metric -> Timer Click Response Latency: ${responseTimeMs}ms")
        assert(responseTimeMs < 100) { "Timer click response took too long: ${responseTimeMs}ms" }
    }



    @Test
    fun measureRadialMenuOverlayOpenLatency() {
        composeTestRule.waitForIdle()

        val overlayOpenTimeMs = measureTimeMillis {
            // Long press the timer display to trigger the radial menu overlay
            composeTestRule.onNodeWithText("25:00").performTouchInput {
                longClick()
            }

            // Wait until the radial menu option appears
            composeTestRule.waitUntil(timeoutMillis = 2000) {
                try {
                    composeTestRule.onNodeWithText("+1 min").assertExists()
                    true
                } catch (e: AssertionError) {
                    false
                }
            }
            composeTestRule.waitForIdle()
        }

        println("Performance Metric -> Radial Menu Open Latency: ${overlayOpenTimeMs}ms")
        assert(overlayOpenTimeMs < 300) { "Radial menu overlay took too long to open: ${overlayOpenTimeMs}ms" }
    }
}
