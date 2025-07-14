package com.halion.padide

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.halion.padide.ui.theme.PadideTheme
import androidx.core.content.edit
import androidx.core.view.WindowInsetsCompat
import com.halion.padide.ui.components.PadideDialog
import com.halion.padide.ui.theme.PadideFontFamily
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.R)


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val devicePolicyManager =
                getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            if (devicePolicyManager.isLockTaskPermitted(packageName)) {
                if (!isAppPinned()) {
                    startLockTask()
                } else {
                    Log.d("MainActivity", "App is already pinned.")
                }
            } else {
                Log.w("MainActivity", "LockTask is not permitted for this app on this device.")
            }
        } else {
            Log.w("MainActivity", "LockTask is not permitted for this app on this device.")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!isMyAppDefaultLauncher(this)) {
            setDefaultLauncher(this)
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
//            BackHandler {
//
//            }

            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            // Hide the status bar
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
            // Hide the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            // Configure behavior for immersive mode
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            PadideTheme {
                var openDropMenu by remember { mutableStateOf(false) }
                var openDialogPass by remember { mutableStateOf(false) }
                var openDialogSetCode by remember { mutableStateOf(false) }
                var openDialogSetUrl by remember { mutableStateOf(false) }
                var url by remember {
                    mutableStateOf(
                        "${loadSavedUrl(this@MainActivity)}/${
                            loadSavedCode(
                                this@MainActivity
                            )
                        }"
                    )
                }

                // rtl
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Image(
                                        modifier = Modifier
                                            .height(50.dp)
                                            .padding(end = 16.dp),
                                        painter = painterResource(R.drawable.logo),
                                        contentDescription = null
                                    )
                                },
                                actions = {
                                    IconButton(onClick = {
                                        openDialogPass = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Menu,
                                            contentDescription = "Menu"
                                        )
                                        DropdownMenu(
                                            expanded = openDropMenu,
                                            onDismissRequest = { openDropMenu = false },
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "تنظیمات",
                                                        fontFamily = PadideFontFamily()
                                                    )
                                                },
                                                onClick = {
                                                    openDropMenu = false
                                                    if (isAppPinned()) {
                                                        stopLockTask()
                                                    }
                                                    startActivity(Intent(Settings.ACTION_SETTINGS))
                                                }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "خروج",
                                                        fontFamily = PadideFontFamily()
                                                    )
                                                },
                                                onClick = {
                                                    openDropMenu = false
                                                    if (isAppPinned()) {
                                                        stopLockTask()
                                                    }
                                                    finishAndRemoveTask()
                                                }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "تغیر شماره پایانه",
                                                        fontFamily = PadideFontFamily()
                                                    )
                                                },
                                                onClick = {
                                                    openDropMenu = false
                                                    openDialogSetCode = true
                                                }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        "تغیر آدرس سرویس",
                                                        fontFamily = PadideFontFamily()
                                                    )
                                                },
                                                onClick = {
                                                    openDropMenu = false
                                                    openDialogSetUrl = true
                                                }
                                            )
                                            Text("Version : ${BuildConfig.VERSION_NAME} ")
                                        }
                                    }
                                }
                            )
                        },
                        content = { innerPadding ->
                            Column(modifier = Modifier.padding(innerPadding)) {
                                WebViewScreen(url)
                            }
                            AnimatedVisibility(openDialogPass) {
                                PadideDialog(
                                    openDialog = openDialogPass,
                                    message = "برای دسترسی به تنظیمات رمز عبور را وارد کنید",
                                    label = "رمز عبور",
                                    onDismissRequest = {
                                        openDialogPass = false
                                    },
                                    onSubmit = { password ->
                                        val currentDate = LocalDateTime.now()
                                        val PASSWORD =
                                            "${currentDate.monthValue}11${currentDate.year}54${currentDate.dayOfMonth}"
                                        if (password == PASSWORD) {
                                            openDialogPass = false
                                            openDropMenu = true
                                        } else {
                                            openDialogPass = false
                                            Toast.makeText(
                                                this@MainActivity,
                                                "رمز عبور اشتباه است",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                            AnimatedVisibility(openDialogSetCode) {
                                PadideDialog(
                                    value = loadSavedCode(this@MainActivity),
                                    openDialog = openDialogSetCode,
                                    message = "کد دستگاه را وارد کنید",
                                    label = "کد دستگاه",
                                    onDismissRequest = {
                                        openDialogSetCode = false
                                    },
                                    onSubmit = { newCode ->
                                        if (newCode.isNotBlank()) {
                                            saveCode(newCode, this@MainActivity)
                                            url =
                                                "${loadSavedUrl(this@MainActivity)}/${
                                                    loadSavedCode(
                                                        this@MainActivity
                                                    )
                                                }"
                                            Toast.makeText(
                                                this@MainActivity,
                                                "آدرس سایت ذخیره شد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "آدرس سایت نمی‌تواند خالی باشد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        openDialogSetCode = false
                                    }
                                )
                            }

                            AnimatedVisibility(openDialogSetUrl) {
                                PadideDialog(
                                    openDialog = openDialogSetUrl,
                                    value = loadSavedUrl(this@MainActivity),
                                    message = "آدرس سرویس را وارد کنید",
                                    label = "آدرس سرویس",
                                    onDismissRequest = {
                                        openDialogSetUrl = false
                                    },
                                    onSubmit = { newUrl ->
                                        if (newUrl.isNotBlank()) {
                                            saveUrl(newUrl, this@MainActivity)
                                            url =
                                                "${loadSavedUrl(this@MainActivity)}/${
                                                    loadSavedCode(
                                                        this@MainActivity
                                                    )
                                                }"
                                            Toast.makeText(
                                                this@MainActivity,
                                                "آدرس سایت ذخیره شد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "آدرس سایت نمی‌تواند خالی باشد",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        openDialogSetUrl = false
                                    }
                                )
                            }
                        }
                    )
                }
            }

        }
    }

    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses
        runningProcesses?.forEach { processInfo ->
            if (processInfo.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        if (!isAppRunning(this, "ir.sep.android.smartpos")) {
            startActivity(Intent(this, SpelashActivity::class.java))
            finish()
        }
    }

    @Composable
    fun WebViewScreen(url: String) {
        Log.d("webview", "WebViewScreenUrl: $url")
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    clearCache(true)
                    clearHistory()
                    clearFormData()
                    clearSslPreferences()
                    removeJavascriptInterface("AndroidInterface")
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.setSupportZoom(false)
                    settings.builtInZoomControls = false
                    settings.displayZoomControls = false
                    webViewClient = WebViewClient()
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onElementClicked() {
                            Log.d("test", "onElementClicked")
                            runOnUiThread {
                                stopLockTask()
                            }
                        }
                    }, "AndroidInterface")
                    loadUrl(url)
                    Log.d("webview", "WebViewScreenUrl: $url")
                    // Save the current URL in the tag
                    tag = url
                }
            },
            update = { webView ->
                val currentUrl = webView.tag as? String
                if (currentUrl != url) {
                    webView.loadUrl(url)
                    Log.d("webview", "WebViewScreenUrl: $url")
                    webView.tag = url
                }
            }
        )
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

fun saveUrl(url: String, context: Context) {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    prefs.edit { putString("saved_url", url) }
}

fun loadSavedUrl(context: Context): String {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    return prefs.getString("saved_url", "http://sandboxwebpos.loanpand.ir")
        ?: "http://sandboxwebpos.loanpand.ir"
}

fun saveCode(code: String, context: Context) {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    prefs.edit { putString("saved_code", code) }
}

fun loadSavedCode(context: Context): String {
    val prefs = context.getSharedPreferences("padide_prefs", Context.MODE_PRIVATE)
    return prefs.getString("saved_code", "13523080")
        ?: "13523080"
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