package com.example.mymediapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.mymediapp.preferences.UserPreferences
import com.example.mymediapp.preferences.LocalUserPreferences
import com.example.mymediapp.ui.theme.AppTheme
import com.example.mymediapp.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(applicationContext)
        setContent {
            CompositionLocalProvider(LocalUserPreferences provides userPreferences) {
            AppTheme {
                MyApp()
            }
        }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp() {
    AppNavigation()
}