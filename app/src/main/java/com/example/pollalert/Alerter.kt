package com.example.pollalert

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Plays the alert sound and vibrates. Both are togglable via Settings.
 * Drop your own sound file at app/src/main/res/raw/ding (e.g. ding.mp3).
 */
class Alerter(private val context: Context) {

    private var player: MediaPlayer? = null

    fun alert() {
        val prefs = Settings(context)
        if (prefs.soundEnabled) playSound()
        if (prefs.vibrationEnabled) vibrate()
    }

    private fun playSound() {
        try {
            // Re-create each time so overlapping alerts don't clash.
            player?.release()
            player = MediaPlayer.create(context, R.raw.ding)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setOnCompletionListener {
                    it.release()
                    if (player === it) player = null
                }
                start()
            }
        } catch (_: Exception) {
            // If the sound file is missing or fails, stay silent rather than crash.
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        vibrator ?: return
        val pattern = longArrayOf(0, 400, 200, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            vibrator.vibrate(pattern, -1)
        }
    }

    fun release() {
        player?.release()
        player = null
    }
}
