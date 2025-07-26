package com.halion.padide

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.halion.padide.ui.theme.PadideTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.util.Log

class SpelashActivity : ComponentActivity() {

    private val REQUEST_CODE_ENABLE_ADMIN = 123

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PadideTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Welcome to Padide ${BuildConfig.VERSION_NAME}")
                }
            }
        }
        lifecycleScope.launch {
            delay(500)
            openApp()

            delay(3000)
            requestDeviceAdmin()
        }

    }

    private fun requestDeviceAdmin() {
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (!dpm.isAdminActive(componentName)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "برای فعال‌سازی حالت کیوسک، لطفاً این مجوز را فعال کنید."
                )
            }
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
        } else {
            setupLockTask()
        }
    }

    private fun setupLockTask() {
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        try {
            if (dpm.isDeviceOwnerApp(packageName) || dpm.isProfileOwnerApp(packageName)) {
                dpm.setLockTaskPackages(componentName, arrayOf(packageName))
                Toast.makeText(this, "✅ Lock task mode configured", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "❌ App is not device or profile owner", Toast.LENGTH_LONG)
                    .show()
                finish()
                Log.e("SpelashActivity", "App is not device or profile owner")
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "❌ Failed to set lock task: ${e.message}", Toast.LENGTH_LONG)
                .show()
            finish()
            Log.e("SpelashActivity", "SecurityException: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
            if (resultCode == RESULT_OK && dpm.isAdminActive(componentName)) {
                Toast.makeText(this, "✅ Device Admin activated", Toast.LENGTH_SHORT).show()
                setupLockTask()
            } else {
                Toast.makeText(this, "❌ Device Admin activation declined", Toast.LENGTH_LONG).show()
                Log.e("SpelashActivity", "Device admin activation failed or canceled")
            }
        }
    }

    private fun openApp() {
        val packageName = "ir.sep.android.smartpos"
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("SpelashActivity", "Smartpos app opened successfully")
        } else {
            Toast.makeText(this, "Smartpos not installed", Toast.LENGTH_SHORT).show()
        }
    }
}