package com.halion.padide

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.halion.padide.ui.theme.PadideTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpelashActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PadideTheme {
//                startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                openApp()

                lifecycleScope.launch {
                    delay(3000)
                    startActivity(Intent(this@SpelashActivity, MainActivity::class.java))
                    finish()
                }

                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {
                    Text("Welcome to Padide")
                }
            }
        }
    }

    private fun openApp() {
        val packageName = "ir.sep.android.smartpos"
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Smartpos not installed", Toast.LENGTH_SHORT).show()
        }
    }
}