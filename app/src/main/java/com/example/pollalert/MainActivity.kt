package com.example.pollalert

import android.content.Intent
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch

class MainActivity : AppCompatActivity() {

    private lateinit var settings: Settings
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settings = Settings(this)

        statusText = findViewById(R.id.statusText)

        findViewById<Button>(R.id.openSettingsButton).setOnClickListener {
            startActivity(Intent(AndroidSettings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.testButton).setOnClickListener {
            Alerter(this).alert()
        }

        findViewById<MaterialSwitch>(R.id.soundSwitch).apply {
            isChecked = settings.soundEnabled
            setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                settings.soundEnabled = checked
            }
        }

        findViewById<MaterialSwitch>(R.id.vibrationSwitch).apply {
            isChecked = settings.vibrationEnabled
            setOnCheckedChangeListener { _: CompoundButton, checked: Boolean ->
                settings.vibrationEnabled = checked
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        statusText.text = if (isServiceEnabled()) {
            getString(R.string.status_on)
        } else {
            getString(R.string.status_off)
        }
    }

    private fun isServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (!am.isEnabled) return false
        val expected = "$packageName/${PollWatchService::class.java.name}"
        val enabled = AndroidSettings.Secure.getString(
            contentResolver,
            AndroidSettings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabled)
        while (splitter.hasNext()) {
            if (splitter.next().equals(expected, ignoreCase = true)) return true
        }
        return false
    }
}
