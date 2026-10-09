package com.armanmojumdar.rotatescreen

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updatePermissionStatus()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))
        }
        root.addView(TextView(this).apply {
            text = "Rotate 360"; textSize = 30f; gravity = Gravity.CENTER
        }, matchWrap())
        root.addView(TextView(this).apply {
            text = "Choose the screen orientation"; textSize = 16f; gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(18))
        }, matchWrap())
        status = TextView(this).apply {
            textSize = 14f; gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(12))
        }
        root.addView(status, matchWrap())
        addButton(root, "Allow modify system settings") { openWriteSettings() }
        addButton(root, "Auto rotate (all sensor directions)") { if (ensurePermission()) setRotation(1, null) }
        addButton(root, "Portrait (0°)") { if (ensurePermission()) setRotation(0, 0) }
        addButton(root, "Landscape (90°)") { if (ensurePermission()) setRotation(0, 1) }
        addButton(root, "Reverse portrait (180°)") { if (ensurePermission()) setRotation(0, 2) }
        addButton(root, "Reverse landscape (270°)") { if (ensurePermission()) setRotation(0, 3) }
        addButton(root, "Restore system auto-rotate") {
            if (ensurePermission()) {
                try {
                    Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1)
                    Toast.makeText(this, "System auto-rotate restored", Toast.LENGTH_SHORT).show()
                    updatePermissionStatus()
                } catch (_: Exception) {
                    Toast.makeText(this, "Could not change rotation settings", Toast.LENGTH_LONG).show()
                }
            }
        }
        root.addView(TextView(this).apply {
            text = "Note: Android and some apps may ignore forced rotation. Reverse portrait availability depends on your device and Android version."
            textSize = 12f; setPadding(0, dp(16), 0, 0)
        }, matchWrap())
        setContentView(root)
        updatePermissionStatus()
    }

    private fun addButton(root: LinearLayout, label: String, action: () -> Unit) {
        root.addView(Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { action() }
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(5)
        })
    }

    private fun ensurePermission(): Boolean {
        if (Settings.System.canWrite(this)) return true
        openWriteSettings()
        Toast.makeText(this, "Enable permission, then return to the app", Toast.LENGTH_LONG).show()
        return false
    }

    private fun openWriteSettings() {
        startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName")))
    }

    private fun setRotation(autoRotate: Int, rotation: Int?) {
        try {
            Settings.System.putInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION, autoRotate)
            if (rotation != null) Settings.System.putInt(contentResolver, Settings.System.USER_ROTATION, rotation)
            Toast.makeText(this, if (autoRotate == 1) "Auto rotation enabled" else "Rotation setting applied", Toast.LENGTH_SHORT).show()
            updatePermissionStatus()
        } catch (_: Exception) {
            Toast.makeText(this, "Unable to change rotation on this device", Toast.LENGTH_LONG).show()
        }
    }

    private fun updatePermissionStatus() {
        status.text = if (Settings.System.canWrite(this)) "Permission: granted" else "Permission needed: tap the first button and allow it"
    }

    private fun matchWrap() = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
