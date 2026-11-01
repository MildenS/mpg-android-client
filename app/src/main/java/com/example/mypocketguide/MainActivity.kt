package com.example.mypocketguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.mypocketguide.ui.AppRoot
import com.example.mypocketguide.ui.theme.MyPocketGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // installSplashScreen() ОБЯЗАТЕЛЬНО до super.onCreate()
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPocketGuideTheme {
                AppRoot()
            }
        }
    }
}