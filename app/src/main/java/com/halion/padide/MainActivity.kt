package com.halion.padide

import android.R.attr.tint
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowInsets
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.halion.padide.ui.theme.PadideTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask()
        }

        if (!isMyAppDefaultLauncher(this)) {
            setDefaultLauncher(this)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)


        setContent {
            PadideTheme {
                var locked by remember { mutableStateOf(false) }
                var url by remember { mutableStateOf(loadSavedUrl(this@MainActivity)) }
                BackHandler {
                    if (!isAppPinned()) {
                        locked = true
                    }
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Padide POS")
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    if (isAppPinned()) {
                                        stopLockTask()
                                    } else {
                                        finish()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.ExitToApp,
                                        contentDescription = "Exit"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    stopLockTask()
                                    startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
                                }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Settings,
                                        contentDescription = "Exit"
                                    )
                                }
                            }
                        )
                    },
                    content = { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            if (locked) {
                                LockScreen { enteredUrl ->
                                    saveUrl(enteredUrl, this@MainActivity)
                                    url = enteredUrl
                                    locked = false
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        startLockTask()
                                    }
                                }
                            } else {
                                AndroidView(
                                    modifier = Modifier.fillMaxSize(),
                                    factory = {
                                        WebView(it).apply {
                                            settings.javaScriptEnabled = true
                                            settings.domStorageEnabled = true
                                            settings.useWideViewPort = true
                                            settings.loadWithOverviewMode = true
                                            settings.allowFileAccess = true
                                            settings.allowContentAccess = true
                                            settings.setSupportZoom(true)
                                            settings.builtInZoomControls = true
                                            settings.displayZoomControls = false
                                            webViewClient = WebViewClient()
                                            loadUrl(url)
                                        }
                                    },
                                    update = {
                                        it.loadUrl(url)
                                    }
                                )
                            }
                        }
                    }
                )

            }

        }
    }


    private fun isAppPinned(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return am.isInLockTaskMode
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    private fun hideSystemUI() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsets.Type.systemBars())
    }

}


@Composable
fun LockScreen(onUnlock: (String) -> Unit) {
    var step by remember { mutableStateOf(1) }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    val correctPassword = "1234"
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (step) {
            1 -> {
                Text("رمز عبور را وارد کنید")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("رمز عبور") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (password == correctPassword) {
                                step = 2
                            } else {
                                Toast.makeText(context, "رمز عبور اشتباه است", Toast.LENGTH_SHORT)
                                    .show()
                                onUnlock(loadSavedUrl(context))
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (password == correctPassword) {
                        step = 2
                    } else {
                        Toast.makeText(context, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show()
                        onUnlock(loadSavedUrl(context))
                    }
                }) {
                    Text("مرحله بعد")
                }
            }

            2 -> {
                Text("لطفاً آدرس سایت را وارد کنید")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("آدرس سایت") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (url.isNotBlank()) {
                                onUnlock(url)
                            } else {
                                Toast.makeText(
                                    context,
                                    "آدرس سایت نمی‌تواند خالی باشد",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    if (url.isNotBlank()) {
                        onUnlock(url)
                    } else {
                        Toast.makeText(context, "آدرس سایت نمی‌تواند خالی باشد", Toast.LENGTH_SHORT)
                            .show()
                    }
                }) {
                    Text("ورود")
                }
            }
        }
    }
}

fun saveUrl(url: String, context: Context) {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    prefs.edit { putString("saved_url", url) }
}

fun loadSavedUrl(context: Context): String {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    return prefs.getString("saved_url", "http://webpos.loanpand.ir/13523080")
        ?: "http://webpos.loanpand.ir/13523080"
}

fun isMyAppDefaultLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    val resolveInfo =
        context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    val currentLauncherPackage = resolveInfo?.activityInfo?.packageName

    return currentLauncherPackage == context.packageName
}


fun setDefaultLauncher(context: Context) {
    val callHomeSettingIntent = Intent(Settings.ACTION_HOME_SETTINGS)
    context.startActivity(callHomeSettingIntent)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PadideTheme {
        Greeting("Android")
    }
}