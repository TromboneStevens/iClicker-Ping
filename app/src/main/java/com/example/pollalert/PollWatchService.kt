package com.example.pollalert

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Watches on-screen UI for the iClicker poll popup and fires an alert so the
 * user looks up and taps "Join" themselves. It never taps anything.
 */
class PollWatchService : AccessibilityService() {

    private lateinit var alerter: Alerter

    // Don't re-alert for the same poll over and over. One alert, then a cooldown.
    private var lastAlertAt = 0L
    private val cooldownMs = 20_000L

    override fun onServiceConnected() {
        super.onServiceConnected()
        alerter = Alerter(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        // Only bother looking when the iClicker app is in front. Comparing the
        // event's package keeps us from scanning every app on the phone.
        val pkg = event.packageName?.toString() ?: return
        if (!TARGET_PACKAGES.any { pkg.contains(it, ignoreCase = true) }) return

        val root = rootInActiveWindow ?: return
        try {
            if (pollIsVisible(root)) {
                maybeAlert()
            }
        } finally {
            root.recycle()
        }
    }

    private fun pollIsVisible(root: AccessibilityNodeInfo): Boolean {
        // Match the join prompt by its visible text. iClicker shows a "Join"
        // button when a poll opens; adjust POLL_KEYWORDS if your screen differs.
        for (keyword in POLL_KEYWORDS) {
            val matches = root.findAccessibilityNodeInfosByText(keyword)
            val found = matches?.isNotEmpty() == true
            matches?.forEach { it.recycle() }
            if (found) return true
        }
        return false
    }

    private fun maybeAlert() {
        val now = System.currentTimeMillis()
        if (now - lastAlertAt < cooldownMs) return
        lastAlertAt = now
        alerter.alert()
    }

    override fun onInterrupt() {
        // Required override. Nothing to clean up between events.
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        if (this::alerter.isInitialized) alerter.release()
        return super.onUnbind(intent)
    }

    companion object {
        // iClicker Student app package(s). Add others here if needed.
        private val TARGET_PACKAGES = listOf("iclicker", "macmillan")

        // Text that indicates a poll is open and joinable.
        private val POLL_KEYWORDS = listOf("Join", "Join Poll", "Join Activity")
    }
}
