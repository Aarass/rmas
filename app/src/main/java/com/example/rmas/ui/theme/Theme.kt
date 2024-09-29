package com.example.rmas.ui.theme

import android.app.Activity
import android.app.Application
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    surfaceTint = Color.Black,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surfaceTint = Color.White,
//    #4a80f4

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RMASTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    var colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

//    colorScheme = LightColorScheme;


//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R)  {
//        window.statusBarColor = colorScheme.background.toArgb()
//        window.navigationBarColor = colorScheme.background.toArgb()
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

var systemUiVisibilityDefaultValue: Int? = null;

@Suppress("DEPRECATION")
fun setDarkStatusBarIcons(window: Window) {
    systemUiVisibilityDefaultValue = window.decorView.systemUiVisibility

    window.decorView.systemUiVisibility =
        window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

@Suppress("DEPRECATION")
fun resetSystemNavigationTheme(window: Window) {
    systemUiVisibilityDefaultValue?.let {
        window.decorView.systemUiVisibility = it
    } ?: Log.e("ui visibility", "default is null")
}

//    WindowCompat.setDecorFitsSystemWindows(window, false)
//    WindowCompat.setDecorFitsSystemWindows(window, false)

// 10000 light
// 1792 dark

//00000000000000000010011100010000 light
//00000000000000000000011100000000 dark
//00000000000000000010000000000000

//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT

//        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

// Ovo kao radi
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        window.statusBarColor = Color.TRANSPARENT
//        window.navigationBarColor = Color.TRANSPARENT

