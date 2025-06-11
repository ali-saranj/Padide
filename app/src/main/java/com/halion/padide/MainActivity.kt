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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.halion.padide.ui.theme.PadideTheme
import androidx.core.content.edit
import com.halion.padide.ui.components.PadideDialog
import com.halion.padide.ui.theme.PadideFontFamily
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

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
                var openDropMenu by remember { mutableStateOf(false) }
                var openDialogPass by remember { mutableStateOf(false) }
                var openDialogSetUrl by remember { mutableStateOf(false) }
                var url by remember { mutableStateOf(loadSavedUrl(this@MainActivity)) }

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
                                                text = { Text("تنظیمات", fontFamily = PadideFontFamily()) },
                                                onClick = {
                                                    openDropMenu = false
                                                    startActivity(Intent(Settings.ACTION_SETTINGS))
                                                }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = { Text("خروج", fontFamily = PadideFontFamily()) },
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
                                                text = { Text("تغیر شماره پایانه", fontFamily = PadideFontFamily()) },
                                                onClick = {
                                                    openDropMenu = false
                                                    openDialogSetUrl = true
                                                }
                                            )

                                        }
                                    }
                                }
                            )
                        },
                        content = { innerPadding ->
                            Column(modifier = Modifier.padding(innerPadding)) {
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
                            AnimatedVisibility(openDialogPass) {
                                PadideDialog(
                                    openDialog = openDialogPass,
                                    message = "برای دسترسی به تنظیمات رمز عبور را وارد کنید",
                                    label = "رمز عبور",
                                    onDismissRequest = {
                                        openDialogPass = false
                                    },
                                    onSubmit = { password ->
                                        val current = LocalDateTime.now()

                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        val PASSWORD = current.format(formatter)
                                        if (password == "11${PASSWORD.replace("-", "")}51") {

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
                            AnimatedVisibility(openDialogSetUrl) {
                                PadideDialog(
                                    openDialog = openDialogSetUrl,
                                    message = "کد دستگاه را وارد کنید",
                                    label = "کد دستگاه",
                                    onDismissRequest = {
                                        openDialogSetUrl = false
                                    },
                                    onSubmit = { newCode ->
                                        if (newCode.isNotBlank()) {
                                            url = "http://webpos.loanpand.ir/$newCode"
                                            saveUrl(url, this@MainActivity)
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